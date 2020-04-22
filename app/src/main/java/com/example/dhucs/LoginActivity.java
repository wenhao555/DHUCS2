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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dhucs.model.User;
import com.example.dhucs.net.Urls;
import com.example.dhucs.utils.PrefUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText account, password;
    private Button login_commit, regist;
    private TextView forget_pwd;
    private Context context;
    private int isTeacher = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        forget_pwd = findViewById(R.id.forget_pwd);
        login_commit = findViewById(R.id.login_commit);
        regist = findViewById(R.id.regist);
        forget_pwd.setVisibility(View.GONE);
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RegistActivity.class));
            }
        });
        login_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = account.getText().toString();
                String pwd = password.getText().toString();
                if (userName.equals("") || pwd.equals("")) {
                    Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                } else {
                    requestData(userName, pwd);
                }
//                PrefUtils.setBoolean(context, "isAdmin", false);
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }


    private void requestData(String userName, String pwd) {
        String url = "";
        if (userName.equals("admin")) {
            url = Urls.adminLogin;
        } else {
            url = Urls.login;
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        final User user = new User();
        user.setAccount(userName);
        user.setPassword(pwd);
        if (userName.equals("admin")) {
            user.setAdmin(true);
        } else {
            user.setAdmin(false);
        }
        Gson gson = new Gson();
        String Json = gson.toJson(user);
        final RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(url)
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
            }
        });
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {


                if (account.getText().toString().equals("admin")) {
                    if (!Boolean.parseBoolean(msg.obj.toString())) {
                        Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        PrefUtils.setBoolean(context, "isAdmin", true);
                        startActivity(new Intent(context, MainActivity.class));
                        finish();
                    }
                } else {
                    String string = msg.obj.toString();
                    if (string.equals("")) {
                        return false;
                    } else {
                        JsonObject jsonObject = new JsonParser().parse(string).getAsJsonObject();
                        Gson gson = new Gson();
                        User user1 = gson.fromJson(jsonObject, User.class);
                        Log.e("asd", string + "");
                        if (!user1.getAccount().equals("")) {
                            PrefUtils.setString(context, "name", user1.getName());
                            PrefUtils.setString(context, "password", user1.getPassword());
                            PrefUtils.setString(context, "sex", user1.getSex());
                            PrefUtils.setString(context, "birth", user1.getBirth());
                            PrefUtils.setString(context, "account", user1.getAccount());
                            PrefUtils.setString(context, "imgpath", user1.getImage());
                            PrefUtils.setBoolean(context, "isAdmin", false);
                            PrefUtils.setInt(context, "userId", user1.getId());
                            PrefUtils.setString(context, "stuNo", user1.getStuNo());
                            PrefUtils.setString(context, "hobby", user1.getStuNo());
                            PrefUtils.setString(context, "hobby", user1.getHobby());
                            PrefUtils.setString(context, "personalStrengths", user1.getPersonalStrengths());
                            startActivity(new Intent(context, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
            return true;
        }
    });

}
