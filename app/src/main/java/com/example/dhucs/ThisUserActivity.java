package com.example.dhucs;

import androidx.annotation.NonNull;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 查看报名用户
 */
public class ThisUserActivity extends AppCompatActivity
{
    private ImageView haveaacback;
    private RecyclerView recyvle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_this_user);
        haveaacback = findViewById(R.id.haveacbacks);
        haveaacback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        recyvle = findViewById(R.id.recyvle);
        userList = (List<User>) getIntent().getSerializableExtra("userList");
        requestData();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                //这里获取数据的逻辑
                requestData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyvle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        initData();
        recyvle.setAdapter(adapter);
//        adapter.setOnItemClickListener(new OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(@NonNull int position)
//            {
//                final User activities = activitiesList.get(position);
//                startActivity(new Intent(ThisUserActivity.this, AddActivity.class).putExtra("title", activities.getName())
//                        .putExtra("img", activities.getImage())
//                        .putExtra("content", activities.getAccount())
//                        .putExtra("ids", activities.getId()));
//            }
//        });
    }

    private BaseRecyclerAdapter adapter;
    private List<User> activitiesList = new ArrayList<>();
    protected MaterialDialog loadingDialog;


    private void initData()
    {
        adapter = new BaseRecyclerAdapter()
        {
            @Override
            protected void onBindView(@NonNull BaseViewHolder holder, @NonNull final int position)
            {
                final User activities = activitiesList.get(position);
                ImageView item_img = holder.getView(R.id.item_img);
                TextView item_title = holder.getView(R.id.item_title);
                TextView item_new = holder.getView(R.id.item_new);
                Button item_pass = holder.getView(R.id.item_pass);
                Button item_setting = holder.getView(R.id.item_setting);
                if (activities.getAccess() != null)
                {//通过集合
                    if (activities.getAccess())
                    {
                        item_pass.setText("已通过");
                        if (activities.getActivityAdmin() != null)
                        {//是否是管理员
                            if (activities.getActivityAdmin())
                            {

                                item_setting.setText("取消管理");
                            }
                        } else
                        {

                            item_setting.setText("设置管理");
                        }
                    } else
                    {
                        item_pass.setText("通过");
                    }

                } else
                {
                    item_pass.setText("通过");
                    item_setting.setText("设置管理");
                }
                item_pass.setOnClickListener(new View.OnClickListener()
                {// 通过
                    @Override
                    public void onClick(View v)
                    {
                        if (item_pass.getText().toString().equals("已通过"))
                            Toast.makeText(ThisUserActivity.this, "不能重复通过", Toast.LENGTH_SHORT).show();
                        else
                            requestTongguo(activities);
                    }
                });

                item_setting.setOnClickListener(new View.OnClickListener()
                {//取消管理员||设置管理员
                    @Override
                    public void onClick(View v)
                    {
                        if (item_pass.getText().toString().contains("已"))
                        {
                            if (item_setting.getText().toString().contains("设置"))
                                requestGuanLi(activities);
                            else if (item_setting.getText().toString().equals("取消管理"))
                                requestCancel(activities);
                        } else

                        {
                            Toast.makeText(ThisUserActivity.this, "请先通过审核", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                item_title.setText(activities.getName());
                item_new.setText(activities.getAccount());
                if (!activities.getImage().equals(""))
                {
                    item_img.setVisibility(View.VISIBLE);
                    byte[] decodedString = Base64.decode(activities.getImage().substring(activities.getImage().indexOf(",") + 1), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    item_img.setImageBitmap(decodedByte);
                } else
                {
                    item_img.setVisibility(View.GONE);
                }
            }

            @Override
            protected int getLayoutResId(int position)
            {
                return R.layout.item_audit;
            }

            @Override
            public int getItemCount()
            {
                return activitiesList.size();
            }
        };
    }

    private void requestCancel(User user)
    {
        showLoadingDialog();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Activities activities = new Activities();
        activities.setId(getIntent().getIntExtra("ids", 0));
//        activities.setActivityAdminUser(user);
        Gson gson = new Gson();
        String Json = gson.toJson(activities);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.removeActivityAdminUser)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                Log.e("error", "connectFail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                Message msg = new Message();
                msg.what = 4;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
                dismissLoadingDialog();
            }
        });
    }

    private void requestTongguo(User user)
    {
        showLoadingDialog();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        List<User> users = new ArrayList<>();
        List<Integer> integers = new ArrayList<>();
        integers.add(getIntent().getIntExtra("ids", 0));
        user.setAccessActivityList(integers);
        user.setAccess(true);
        users.add(user);
        Activities activities = new Activities();
        activities.setAccessUserList(users);
        activities.setId(getIntent().getIntExtra("ids", 0));
        Gson gson = new Gson();
        String Json = gson.toJson(activities);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.accessUserForActivity)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                Log.e("error", "connectFail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                Message msg = new Message();
                msg.what = 3;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
                dismissLoadingDialog();
            }
        });
    }

    private Handler mHandler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(@NonNull Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    String string = (String) msg.obj;
                    Gson gson = new Gson();
                    Log.e("返回用户", string);
                    activitiesList = gson.fromJson(string, new TypeToken<List<User>>()
                    {
                    }.getType());
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    if (!Boolean.parseBoolean(msg.obj.toString()))
                    {
                        Toast.makeText(ThisUserActivity.this, "设置管理员失败", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        Toast.makeText(ThisUserActivity.this, "设置管理员成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    break;
                case 3:
                    if (!Boolean.parseBoolean(msg.obj.toString()))
                    {
                        Toast.makeText(ThisUserActivity.this, "审核失败", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        Toast.makeText(ThisUserActivity.this, "审核成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    break;
                case 4:
                    if (!Boolean.parseBoolean(msg.obj.toString()))
                    {
                        Toast.makeText(ThisUserActivity.this, "取消管理员失败", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        Toast.makeText(ThisUserActivity.this, "取消管理员成功", Toast.LENGTH_SHORT).show();
//                        requestData();
                        finish();
                    }

                    break;
            }
            return true;
        }
    });

    private void requestData()
    {

        showLoadingDialog();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Activities activities = new Activities();
        activities.setId(getIntent().getIntExtra("ids", 0));
        Gson gson = new Gson();
        String Json = gson.toJson(activities);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.getUserListForActivity)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                Log.e("error", "connectFail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 设置管理员
     *
     * @param user
     */
    private void requestGuanLi(User user)
    {

        showLoadingDialog();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Activities activities = new Activities();
        activities.setId(getIntent().getIntExtra("ids", 0));
        activities.setActivityAdminUser(user);
        Gson gson = new Gson();
        String Json = gson.toJson(activities);
        Log.e("测试管理员", Json);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.setActivityAdminUser)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                Log.e("error", "connectFail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                Message msg = new Message();
                msg.what = 2;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
                dismissLoadingDialog();
            }
        });
    }

    protected void showLoadingDialog()
    {
        loadingDialog = new MaterialDialog.Builder(this).content(R.string.loading).progress(true, 0).build();
        if (loadingDialog != null && !loadingDialog.isShowing())
        {
            loadingDialog.show();
        }
    }

    /**
     * 隐藏loading对话框
     */
    protected void dismissLoadingDialog()
    {
        if (loadingDialog != null && loadingDialog.isShowing())
        {
            loadingDialog.dismiss();
        }
    }
}
