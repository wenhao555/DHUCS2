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

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dhucs.model.User;
import com.example.dhucs.net.Urls;
import com.example.liangmutian.mypicker.DatePickerDialog;
import com.example.liangmutian.mypicker.DateUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegistActivity extends AppCompatActivity {

    private EditText account, password, name, stuNo;
    private Button regist_commit;
    private Dialog dateDialog;
    private TextView user_bth;
    private RadioGroup user_group;
    private String isMan = "男";
    private RadioButton user_man, user_woman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        account = findViewById(R.id.account);
        name = findViewById(R.id.name);
        stuNo = findViewById(R.id.stuNo);
        user_bth = findViewById(R.id.user_bth);
        user_group = findViewById(R.id.user_group);

        user_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.user_man:
                        isMan = "男";
                        break;
                    case R.id.user_woman:
                        isMan = "女";
                        break;
                }
            }
        });
        user_bth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyy-MM-ss").format(new Date());
                showDateDialog(DateUtil.getDateForString(date));

            }
        });
        password = findViewById(R.id.password);
        regist_commit = findViewById(R.id.regist_commit);
        regist_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = password.getText().toString();
                String accounts = account.getText().toString();
                String names = name.getText().toString();
                String bth = user_bth.getText().toString();
                String stuNos = stuNo.getText().toString();
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build();


                if (name.equals("") || pwd.equals("")) {
                    Toast.makeText(RegistActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User();
                user.setAccount(accounts);
                if (pwd.length() > 6)
                    user.setPassword(pwd);
                else {
                    Toast.makeText(RegistActivity.this, "密码过短", Toast.LENGTH_SHORT).show();
                    return;
                }
                user.setName(names);
                user.setSex(isMan);
                user.setBirth(bth);
                user.setImage("");
                user.setAdmin(false);
                user.setStuNo(stuNos);
                user.setAccess(false);
                user.setActivityAdmin(false);
                user.setHobby("");
                user.setPersonalStrengths("");
                Gson gson = new Gson();
                String Json = gson.toJson(user);
                RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
                final Request request = new Request.Builder()
                        .url(Urls.createUser)
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
        });
    }

    private void showDateDialog(List<Integer> date) {
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(this);
        builder.setOnDateSelectedListener(new DatePickerDialog.OnDateSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSelected(int[] dates) {

                user_bth.setText(dates[0] + "-" + (dates[1] > 9 ? dates[1] : ("0" + dates[1])) + "-"
                        + (dates[2] > 9 ? dates[2] : ("0" + dates[2])));
            }

            @Override
            public void onCancel() {

            }
        })
                .setSelectYear(date.get(0) - 1)
                .setSelectMonth(date.get(1) - 1)
                .setSelectDay(date.get(2) - 1);

        builder.setMaxYear(DateUtil.getYear());
        builder.setMaxMonth(DateUtil.getDateForString(DateUtil.getToday()).get(1));
        builder.setMaxDay(DateUtil.getDateForString(DateUtil.getToday()).get(2));
        dateDialog = builder.create();
        dateDialog.show();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
//                boolean string = (Boolean) msg.obj;
                if (!Boolean.parseBoolean(msg.obj.toString())) {
                    Toast.makeText(RegistActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                }


            }
        }
    };
}


