package com.example.dhucs.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.dhucs.AddActivity;
import com.example.dhucs.AddFengActivity;
import com.example.dhucs.R;
import com.example.dhucs.UserActivity;
import com.example.dhucs.adapter.BaseRecyclerAdapter;
import com.example.dhucs.listeners.OnItemClickListener;
import com.example.dhucs.listeners.OnItemLongClickListener;
import com.example.dhucs.model.User;
import com.example.dhucs.net.Urls;
import com.example.dhucs.utils.Constants;
import com.example.dhucs.utils.EventMsg;
import com.example.dhucs.utils.PrefUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminUserFragment extends Fragment {

    public AdminUserFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyvle;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_user, container, false);
        requestData();
        recyvle = view.findViewById(R.id.recyvle);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //这里获取数据的逻辑
                requestData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        recyvle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        initData();
        recyvle.setAdapter(adapter);
        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onLongItemClick(@NonNull int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示");
                builder.setMessage("是否删除此用户");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final User activities = activitiesList.get(position);

                        requestDelete(activities.getId());
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
//        adapter.setOnItemLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(getActivity(), "asd", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull int position) {
                final User activities = activitiesList.get(position);
                startActivity(new Intent(getActivity(), UserActivity.class)
                        .putExtra("name", activities.getName())
                        .putExtra("img", activities.getImage())
                        .putExtra("account", activities.getAccount())
                        .putExtra("sex", activities.getSex())
                        .putExtra("birth", activities.getBirth())
                        .putExtra("hobby", activities.getHobby())
                        .putExtra("personalStrengths", activities.getPersonalStrengths())
                        .putExtra("ids", activities.getId()));
            }
        });
        return view;
    }

    private void requestDelete(int  user) {
        showLoadingDialog();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        User user1 = new User();
        user1.setId(user);
        String json = new Gson().toJson(user1);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        final Request request = new Request.Builder()
                .url(Urls.removeUser)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUserActivity(EventMsg msg) {
        switch (msg.getTag()) {

            case Constants.CONNET_SUCCESS:
                Log.e("测试接收", "接收");
                requestData();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private BaseRecyclerAdapter adapter;
    private List<User> activitiesList = new ArrayList<>();
    protected MaterialDialog loadingDialog;


    private void initData() {
        adapter = new BaseRecyclerAdapter() {
            @Override
            protected void onBindView(@NonNull BaseViewHolder holder, @NonNull final int position) {
                final User activities = activitiesList.get(position);
                ImageView item_img = holder.getView(R.id.item_img);
                TextView item_title = holder.getView(R.id.item_title);
                TextView item_new = holder.getView(R.id.item_new);
                item_title.setText(activities.getName());
                item_new.setText(activities.getAccount());
                if (!activities.getImage().equals("")) {
                    item_img.setVisibility(View.VISIBLE);
                    byte[] decodedString = Base64.decode(activities.getImage().substring(activities.getImage().indexOf(",") + 1), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    item_img.setImageBitmap(decodedByte);
                }
            }

            @Override
            protected int getLayoutResId(int position) {
                return R.layout.iten_adapter;
            }

            @Override
            public int getItemCount() {
                return activitiesList.size();
            }
        };
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    String string = (String) msg.obj;
                    Gson gson = new Gson();
                    activitiesList = gson.fromJson(string, new TypeToken<List<User>>() {
                    }.getType());
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    if (!Boolean.parseBoolean(msg.obj.toString())) {
                        Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        requestData();
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
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        final Request request = new Request.Builder()
                .url(Urls.getAllUserInfo)
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
        loadingDialog = new MaterialDialog.Builder(getActivity()).content(R.string.loading).progress(true, 0).build();
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
