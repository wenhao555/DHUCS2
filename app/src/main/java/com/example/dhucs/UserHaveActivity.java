package com.example.dhucs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.dhucs.adapter.BaseRecyclerAdapter;
import com.example.dhucs.listeners.OnItemClickListener;
import com.example.dhucs.model.Activities;
import com.example.dhucs.model.User;
import com.example.dhucs.net.Urls;
import com.example.dhucs.utils.PrefUtils;
import com.example.dhucs.utils.ZXingUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserHaveActivity extends AppCompatActivity {
    private ImageView haveacback;
    private RecyclerView recyvle;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_have);
        haveacback = findViewById(R.id.haveacback);
        haveacback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyvle = findViewById(R.id.recyvle);
        requestData();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //这里获取数据的逻辑
                requestData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        recyvle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        initData();
        recyvle.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull int position) {
                final Activities activities = activitiesList.get(position);
                startActivity(new Intent(UserHaveActivity.this, UserHavesActivity.class).putExtra("title", activities.getTitle())
                        .putExtra("img", activities.getImage())
                        .putExtra("content", activities.getContent())
                        .putExtra("place", activities.getPlace())
                        .putExtra("title", activities.getTitle())
                        .putExtra("workLong", activities.getWorkLong())
                        .putExtra("timeLong", activities.getTimeLong())
                        .putExtra("ids", activities.getId()))
                ;
            }
        });
    }

    private BaseRecyclerAdapter adapter;
    private List<Activities> activitiesList = new ArrayList<>();
    protected MaterialDialog loadingDialog;


    private void initData() {
        adapter = new BaseRecyclerAdapter() {
            @Override
            protected void onBindView(@NonNull BaseViewHolder holder, @NonNull final int position) {
                final Activities activities = activitiesList.get(position);
                ImageView item_img = holder.getView(R.id.item_img);
                TextView item_title = holder.getView(R.id.item_title);
                TextView item_new = holder.getView(R.id.item_new);
                Button item_pass = holder.getView(R.id.item_pass);
                Button item_setting = holder.getView(R.id.item_setting);
                item_title.setText(activities.getTitle());
                item_new.setText(activities.getContent());
                if (!activities.getImage().equals("")) {
                    item_img.setVisibility(View.VISIBLE);
                    byte[] decodedString = Base64.decode(activities.getImage().substring(activities.getImage().indexOf(",") + 1), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    item_img.setImageBitmap(decodedByte);
                } else {
                    item_img.setVisibility(View.GONE);
                }
                if (activities.getActivityUserList() != null && activities.getActivityUserList().size() > 0) {
                    for (User user : activities.getActivityUserList()) {
                        if (PrefUtils.getInt(UserHaveActivity.this, "userId", 0) == user.getId()) {//当此用户存在于活动用户
                            if (user.getAccess()) {//通过审核
                                item_pass.setText("签到");
                                item_setting.setVisibility(View.VISIBLE);
                                item_setting.setText("签退");
                                if (user.getActivityAdmin() != null)
                                    if (user.getActivityAdmin()) {//当该用户为管理员时
                                        item_setting.setText("二维码");
                                        item_pass.setVisibility(View.GONE);
                                    } else {
                                        if (user.getSign() != null)
                                            if (user.getSign()) {//签到
                                                item_pass.setText("已签到");
                                            }
                                    }
                            } else {
                                //没通过审核
                                item_pass.setText("审核中");
                                item_setting.setVisibility(View.GONE);
                            }

                        }
                    }
                }
                item_pass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item_pass.getText().toString().equals("签到"))
                            startActivityForResult(new Intent(UserHaveActivity.this, ScanQrActivity.class).putExtra("ids", activities.getId()), 1111);
                        else if (item_pass.getText().toString().equals("已签到")) {
                            Toast.makeText(UserHaveActivity.this, "请勿重复签到", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserHaveActivity.this, "审核中...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                item_setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item_setting.getText().toString().equals("二维码"))
                            startActivity(new Intent(UserHaveActivity.this, ImgActivity.class).putExtra("img", "626128095" + activities.getId()));
                        else {
                            if (!item_pass.getText().toString().equals("已签到")) {
                                Toast.makeText(UserHaveActivity.this, "请先签到", Toast.LENGTH_SHORT).show();
                            } else
                                requestSignoff(activities.getId());
                        }
                    }
                });

            }

            @Override
            protected int getLayoutResId(int position) {
                return R.layout.item_audit;
            }

            @Override
            public int getItemCount() {
                if (activitiesList != null)
                    return activitiesList.size();
                else return 0;
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111 && resultCode == 111) {
            String string = data.getStringExtra("signCode");
            String stringInt = data.getStringExtra("ids");
            if (string.equals(stringInt)) {
                requestsignOn(Integer.parseInt(string));
            }
        }
    }

    private void requestsignOn(int id) {
        showLoadingDialog();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Activities activities = new Activities();
        idss = id;
        activities.setId(id);
        User user = new User();
        user.setId(PrefUtils.getInt(this, "userId", 0));
        user.setName(PrefUtils.getString(this, "name", ""));
        user.setSex(PrefUtils.getString(this, "sex", ""));
        user.setBirth(PrefUtils.getString(this, "birth", ""));
        user.setAccount(PrefUtils.getString(this, "account", ""));
        user.setStuNo(PrefUtils.getString(this, "stuNo", ""));
        List<User> users = new ArrayList<>();
        users.add(user);
        activities.setSignUserList(users);
        Gson gson = new Gson();
        String Json = gson.toJson(activities);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.signOnActivity)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("error", "connectFail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = 3;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
                dismissLoadingDialog();
            }
        });
    }

    private void requestSignoff(int id) {
        showLoadingDialog();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Activities user = new Activities();
        user.setId(id);
        idss = id;
        User user1 = new User();
        user1.setId(PrefUtils.getInt(this, "userId", 0));
        user1.setName(PrefUtils.getString(this, "name", ""));
        user1.setSex(PrefUtils.getString(this, "sex", ""));
        user1.setBirth(PrefUtils.getString(this, "birth", ""));
        user1.setAccount(PrefUtils.getString(this, "account", ""));
        user1.setStuNo(PrefUtils.getString(this, "stuNo", ""));
        user1.setActivityAdmin(false);
        user1.setAccess(true);
        user1.setSign(false);
        user.setSignOffUser(user1);
        Gson gson = new Gson();
        String Json = gson.toJson(user);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.signOffActivity)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("error", "connectFail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = 2;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
                dismissLoadingDialog();
            }
        });
    }

    private int idss = 0;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    String string = (String) msg.obj;
                    Gson gson = new Gson();
                    Log.e("测试返回用户活动", string);
                    activitiesList = gson.fromJson(string, new TypeToken<List<Activities>>() {
                    }.getType());
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    if (!Boolean.parseBoolean(msg.obj.toString())) {
                        Toast.makeText(UserHaveActivity.this, "签退失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserHaveActivity.this, "签退成功", Toast.LENGTH_SHORT).show();
                        requestData();
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserHaveActivity.this);
                        builder.setTitle("提示")
                                .setMessage("是否填写反馈")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(UserHaveActivity.this, UserSuggestActivity.class).putExtra("ids", idss)
                                                .putExtra("title", ""));
                                    }
                                })
                                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                    break;
                case 3:
                    if (!Boolean.parseBoolean(msg.obj.toString())) {
                        Toast.makeText(UserHaveActivity.this, "签到失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserHaveActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                        requestData();
                        finish();
                    }
                    break;

            }
            return true;
        }
    });

    private void requestData() {

        showLoadingDialog();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        User user = new User();
        user.setId(PrefUtils.getInt(this, "userId", 0));
        Gson gson = new Gson();
        String Json = gson.toJson(user);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.getAllActivityForUser)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("error", "connectFail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
                dismissLoadingDialog();
            }
        });
    }

    protected void showLoadingDialog() {
        loadingDialog = new MaterialDialog.Builder(this).content(R.string.loading).progress(true, 0).build();
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    /**
     * 隐藏loading对话框
     */
    protected void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
