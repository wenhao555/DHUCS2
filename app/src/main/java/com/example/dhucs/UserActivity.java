package com.example.dhucs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dhucs.model.User;
import com.example.dhucs.net.Urls;
import com.example.dhucs.utils.Constants;
import com.example.dhucs.utils.EventMsg;
import com.example.dhucs.utils.PrefUtils;
import com.example.liangmutian.mypicker.DatePickerDialog;
import com.example.liangmutian.mypicker.DateUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView title;
    private TextView user_bth, info;
    private EditText user_name, user_stuNo, user_person, user_hob;
    private RadioGroup user_group;
    private Dialog dateDialog;
    private String isMan = "男";
    private RadioButton user_man, user_woman;
    private ImageView user_img;
    private Button mine_exit;

    /**
     * 检查是否有对应权限
     *
     * @param activity   上下文
     * @param permission 要检查的权限
     * @return 结果标识
     */
    public int verifyPermissions(Activity activity, java.lang.String permission) {
        int Permission = ActivityCompat.checkSelfPermission(activity, permission);
        if (Permission == PackageManager.PERMISSION_GRANTED) {
            return 1;
        } else {
            return 0;
        }
    }

    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        title = findViewById(R.id.title);
        user_stuNo = findViewById(R.id.user_stuNo);
        info = findViewById(R.id.info);
        mine_exit = findViewById(R.id.mine_exit);
        mine_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });
        user_img = findViewById(R.id.user_img);
        if (!PrefUtils.getString(this, "imgpath", " ").equals("")) {
            byte[] decodedString = Base64.decode(PrefUtils.getString(this, "imgpath", " ")
                    .substring(PrefUtils.getString(this, "imgpath", " ")
                            .indexOf(",") + 1), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            user_img.setImageBitmap(decodedByte);
        }
        title.setOnClickListener(this);
        user_bth = findViewById(R.id.user_bth);
        user_hob = findViewById(R.id.user_hob);
        user_person = findViewById(R.id.user_person);
        user_hob.setText(PrefUtils.getString(this, "hobby", ""));
        user_person.setText(PrefUtils.getString(this, "personalStrengths", ""));
        user_woman = findViewById(R.id.user_woman);
        dianji = findViewById(R.id.dianji);
        user_man = findViewById(R.id.user_man);
        user_bth.setText(PrefUtils.getString(this, "birth", ""));
        user_bth.setOnClickListener(this);
        user_name = findViewById(R.id.user_name);
        user_name.setText(PrefUtils.getString(this, "name", ""));
        user_stuNo.setText(PrefUtils.getString(this, "stuNo", ""));
        user_stuNo.setEnabled(false);
        user_group = findViewById(R.id.user_group);

        if (PrefUtils.getString(this, "sex", "").equals("男")) {
            user_man.setChecked(true);
        } else {
            user_woman.setChecked(true);
        }

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

        user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPicture();
            }
        });
        if (PrefUtils.getBoolean(this, "isAdmin", true)) {
            if (!getIntent().getStringExtra("img").equals("")) {
                byte[] decodedString = Base64.decode(getIntent().getStringExtra("img")
                        .substring(getIntent().getStringExtra("img")
                                .indexOf(",") + 1), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                user_img.setImageBitmap(decodedByte);
            }
            dianji.setVisibility(View.INVISIBLE);
            user_name.setText(getIntent().getStringExtra("name"));
            user_bth.setText(getIntent().getStringExtra("birth"));
            user_bth.setEnabled(false);
            user_name.setEnabled(false);
            user_person.setEnabled(false);
            user_hob.setEnabled(false);
            user_img.setEnabled(false);
            user_man.setEnabled(false);
            user_woman.setEnabled(false);
            info.setText("用户资料");

            if (getIntent().getStringExtra("sex").equals("男")) {
                user_man.setChecked(true);
            } else {
                user_woman.setChecked(true);
            }
            mine_exit.setVisibility(View.INVISIBLE);
        }
    }

    private TextView dianji;
    private String picturePath = "";
    public static File file;
    public static Uri uri01;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:   //相册返回的数据（相册的返回码）
                uri01 = data.getData();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri01));
                    user_img.setImageBitmap(bitmap);
                    Uri uri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    file = new File(picturePath);

                    cursor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private void request() {

        String account = PrefUtils.getString(this, "account", "");
        String name = user_name.getText().toString();
        String sex = isMan;
        String bth = user_bth.getText().toString();
        String hob = user_hob.getText().toString();
        String per = user_person.getText().toString();
        if (!name.equals("")) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            final User user = new User();
            user.setAccount(account);
            user.setPassword(PrefUtils.getString(this, "password", ""));
            user.setBirth(bth);
            user.setName(name);
            user.setSex(sex);
            user.setHobby(hob);
            user.setPersonalStrengths(per);

            if (file != null) {
                user.setImage(fileToBase64(file));

            } else if (!PrefUtils.getString(this, "imgpath", " ").equals("")) {
                user.setImage(PrefUtils.getString(this, "imgpath", " "));
            } else {
                user.setImage("");
            }

            Gson gson = new Gson();
            String Json = gson.toJson(user);
            Log.e("测试json", Json);
            final RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
            final Request request = new Request.Builder()
                    .url(Urls.setUserInfo)
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
        } else {
            Toast.makeText(this, "输入内容有误", Toast.LENGTH_SHORT).show();
        }
    }

    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return base64;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUserActivity(EventMsg msg) {
        switch (msg.getTag()) {

            case Constants.CONNET_SUCCESS:
                Log.e("测试接收", "接收1111");

                break;
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                if (msg.obj.toString().equals("")) {
                    Toast.makeText(UserActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                } else {
                    JsonObject jsonObject = new JsonParser().parse(msg.obj.toString()).getAsJsonObject();
                    Gson gson = new Gson();
                    User user1 = gson.fromJson(jsonObject, User.class);
                    Toast.makeText(UserActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    PrefUtils.setString(UserActivity.this, "name", user1.getName());
                    PrefUtils.setString(UserActivity.this, "sex", user1.getSex());
                    PrefUtils.setString(UserActivity.this, "birth", user1.getBirth());
                    PrefUtils.setString(UserActivity.this, "imgpath", user1.getImage());
                    PrefUtils.setString(UserActivity.this, "hobby", user1.getHobby());
                    PrefUtils.setString(UserActivity.this, "personalStrengths", user1.getPersonalStrengths());
                    EventMsg message = new EventMsg();
                    message.setTag(Constants.CONNET_SUCCESS);//发送链接成功的信号
                    EventBus.getDefault().post(message);
                    finish();
                }


            } else if (msg.what == 2) {
                String string = msg.obj.toString();
                JsonObject jsonObject = new JsonParser().parse(string).getAsJsonObject();
                Gson gson = new Gson();
                User user1 = gson.fromJson(jsonObject, User.class);
                PrefUtils.setString(UserActivity.this, "name", user1.getName());
                PrefUtils.setString(UserActivity.this, "password", user1.getPassword());
                PrefUtils.setString(UserActivity.this, "sex", user1.getSex());
                PrefUtils.setString(UserActivity.this, "birth", user1.getBirth());
                PrefUtils.setString(UserActivity.this, "account", user1.getAccount());
            }
        }
    };


    private void request2() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        final User user = new User();
        user.setAccount(PrefUtils.getString(this, "account", ""));
        Gson gson = new Gson();
        String Json = gson.toJson(user);
        final RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.getUserInfo)
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
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title:
                finish();
                break;
            case R.id.user_bth:
                @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyy-MM-ss").format(new Date());
                showDateDialog(DateUtil.getDateForString(date));
                break;
            case R.id.user_name:
                request();
                break;
        }
    }

    private void showDateDialog(List<Integer> date) {
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(this);
        builder.setOnDateSelectedListener(new DatePickerDialog.OnDateSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSelected(int[] dates) {

//                mTextView.setText(dates[0] + "-" + (dates[1] > 9 ? dates[1] : ("0" + dates[1])) + "-"
//                        + (dates[2] > 9 ? dates[2] : ("0" + dates[2])));
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
}

