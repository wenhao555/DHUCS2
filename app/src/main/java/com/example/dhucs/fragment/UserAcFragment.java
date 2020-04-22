package com.example.dhucs.fragment;

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

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.dhucs.AddActivity;
import com.example.dhucs.R;
import com.example.dhucs.adapter.BaseRecyclerAdapter;
import com.example.dhucs.listeners.OnItemClickListener;
import com.example.dhucs.model.Activities;
import com.example.dhucs.net.Urls;
import com.example.dhucs.views.GlideImageLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAcFragment extends Fragment {

    public UserAcFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyvle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Banner banner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_ac, container, false);
        requestData();
        recyvle = view.findViewById(R.id.recyvle);
        banner = view.findViewById(R.id.banner);
        List<Integer> images = new ArrayList<>();
        images.add(R.mipmap.bac1);
        images.add(R.mipmap.bac2);
        images.add(R.mipmap.bac3);
        banner.setImages(images).setImageLoader(new GlideImageLoader()).start();
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
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull int position) {
                final Activities activities = activitiesList.get(position);
                startActivity(new Intent(getActivity(), AddActivity.class)
                        .putExtra("title", activities.getTitle())
                        .putExtra("img", activities.getImage())
                        .putExtra("content", activities.getContent())
                        .putExtra("place", activities.getPlace())
                        .putExtra("ids", activities.getId()));
            }
        });
        return view;

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
                TextView item_date = holder.getView(R.id.item_date);
                item_title.setText(activities.getTitle());
                item_date.setText(activities.getDate());
                item_new.setText(activities.getContent());
                if (!activities.getImage().equals("")) {
                    item_img.setVisibility(View.VISIBLE);
                    byte[] decodedString = Base64.decode(activities.getImage().substring(activities.getImage().indexOf(",") + 1), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    item_img.setImageBitmap(decodedByte);
                } else {
                    item_img.setVisibility(View.GONE);
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
                    Log.e("测试接口", string);
                    Gson gson = new Gson();
                    activitiesList = gson.fromJson(string, new TypeToken<List<Activities>>() {
                    }.getType());
                    adapter.notifyDataSetChanged();
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
                .url(Urls.getAllActivity)
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
