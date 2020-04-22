package com.example.dhucs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.dhucs.model.Activities;
import com.example.dhucs.model.User;
import com.example.dhucs.net.Urls;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserSuggestActivity extends AppCompatActivity
{
    private ImageView title;
    private EditText addAc_content;
    private Button mine_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_suggest);
        title = findViewById(R.id.title);
        addAc_content = findViewById(R.id.addAc_content);
        mine_exit = findViewById(R.id.mine_exit);
        mine_exit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                requestData();
            }
        });
        if (!getIntent().getStringExtra("title").equals(""))
        {
            addAc_content.setText(getIntent().getStringExtra("title"));
            addAc_content.setEnabled(false);
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(@NonNull Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    if (!Boolean.parseBoolean(msg.obj.toString()))
                    {
                        Toast.makeText(UserSuggestActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        Toast.makeText(UserSuggestActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
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
        List<String> strings = new ArrayList<>();
        strings.add(addAc_content.getText().toString());
        activities.setId(getIntent().getIntExtra("ids", 0));
        activities.setSuggestList(strings);
        Gson gson = new Gson();
        String Json = gson.toJson(activities);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.addSuggest)
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

    protected MaterialDialog loadingDialog;

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
