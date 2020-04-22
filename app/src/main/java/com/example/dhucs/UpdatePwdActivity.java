package com.example.dhucs;

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

import com.example.dhucs.model.User;
import com.example.dhucs.net.Urls;
import com.example.dhucs.utils.PrefUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class UpdatePwdActivity extends AppCompatActivity
{

    private EditText old_pwd, new_pwd, new_pwd2;
    private Button mine_exit;
    private ImageView title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        title = findViewById(R.id.title);
        title.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        old_pwd = findViewById(R.id.old_pwd);
        new_pwd = findViewById(R.id.new_pwd);
        new_pwd2 = findViewById(R.id.new_pwd2);
        mine_exit = findViewById(R.id.mine_exit);
        mine_exit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                request();

            }
        });
    }

    private void request()
    {

        String old_pwds = old_pwd.getText().toString();
        String new_pwds = new_pwd.getText().toString();
        String new_pwd2s = new_pwd2.getText().toString();
        if (old_pwds.equals(PrefUtils.getString(this, "password", "")) && new_pwd2s.equals(new_pwds) &&
                !new_pwd2s.equals("") && !new_pwds.equals(""))
        {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            final User user = new User();
            user.setAccount(PrefUtils.getString(this, "account", ""));
            user.setPassword(new_pwd2s);
            Gson gson = new Gson();
            String Json = gson.toJson(user);
            final RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
            final Request request = new Request.Builder()
                    .url(Urls.modifyPass)
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
                }
            });
        } else
        {
            Toast.makeText(this, "输入内容有误", Toast.LENGTH_SHORT).show();
        }
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == 1)
            {
                if (!Boolean.parseBoolean(msg.obj.toString()))
                {
                    Toast.makeText(UpdatePwdActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(UpdatePwdActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    PrefUtils.setString(UpdatePwdActivity.this, "password", new_pwd2.getText().toString());
                    finish();
                }


            }
        }
    };
}
