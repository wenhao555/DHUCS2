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

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dhucs.model.Activities;
import com.example.dhucs.model.HappyContent;
import com.example.dhucs.net.Urls;
import com.example.dhucs.utils.Constants;
import com.example.dhucs.utils.EventMsg;
import com.example.dhucs.utils.PrefUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AddFengActivity
        extends BaseActivity {


    @Override
    public int getLayoutId() {
        return R.layout.activity_add;
    }

    private String content, titles, imgs;
    private int ids;
    private boolean isFirst;

    @Override
    public void initData() {

        lin_area = findViewById(R.id.lin_area);
        lin_timee = findViewById(R.id.lin_timee);
        lin_date = findViewById(R.id.lin_date);
        lin_area.setVisibility(View.GONE);
        lin_timee.setVisibility(View.GONE);
        lin_date.setVisibility(View.GONE);
        isFirst = false;
        if (PrefUtils.getBoolean(this, "isAdmin", true)) {

            if (Objects.equals(getIntent().getStringExtra("content"), "")) {
                addAc_type.setText("发布通告");
                isFirst = true;

            } else {
                addAc_type.setText("编辑通告");
                titles = getIntent().getStringExtra("title");
                content = getIntent().getStringExtra("content");
                imgs = getIntent().getStringExtra("img");
                ids = getIntent().getIntExtra("ids", 0);
                addAc_title.setText(titles);
                addAc_title.setEnabled(true);
                addAc_content.setText(content);
                addAc_content.setEnabled(true);
                byte[] decodedString = Base64.decode(imgs
                        .substring(imgs
                                .indexOf(",") + 1), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                addAc_img.setImageBitmap(decodedByte);
                if (imgs.equals(""))
                    addAc_img.setImageResource(R.mipmap.addimg);
            }
        } else {
            addAc_type.setText("通告详情");
            addAc_commit.setVisibility(View.GONE);
            addAc_delete.setVisibility(View.GONE);
            titles = getIntent().getStringExtra("title");
            content = getIntent().getStringExtra("content");
            imgs = getIntent().getStringExtra("img");
            addAc_title.setText(titles);
            addAc_title.setEnabled(false);
            addAc_content.setText(content);
            addAc_content.setEnabled(false);
            byte[] decodedString = Base64.decode(imgs
                    .substring(imgs
                            .indexOf(",") + 1), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            addAc_img.setImageBitmap(decodedByte);
        }
        addAc_back.setOnClickListener(this);
        addAc_img.setOnClickListener(this);
        addAc_commit.setOnClickListener(this);
        addAc_delete.setOnClickListener(this);
    }

    private ImageView addAc_back;
    private TextView addAc_type;
    private EditText addAc_title, addAc_content;
    private ImageView addAc_img;
    private Button addAc_commit, addAc_delete, search_user, search_suggest;
    private LinearLayout isUserLinear, lin_area, lin_timee, lin_date;

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        search_user = findViewById(R.id.search_user);
        isUserLinear = findViewById(R.id.isUserLinear);
        search_user.setVisibility(View.GONE);
        isUserLinear.setVisibility(View.GONE);
        search_suggest = findViewById(R.id.search_suggest);
        search_suggest.setVisibility(View.GONE);
        addAc_back = findViewById(R.id.addAc_back);
        addAc_type = findViewById(R.id.addAc_type);
        addAc_title = findViewById(R.id.addAc_title);
        addAc_content = findViewById(R.id.addAc_content);
        addAc_img = findViewById(R.id.addAc_img);
        addAc_commit = findViewById(R.id.addAc_commit);
        addAc_delete = findViewById(R.id.addAc_delete);

    }

    private void requestReCommit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        final Activities activities = new Activities();
        activities.setContent(addAc_content.getText().toString());
        activities.setTitle(addAc_title.getText().toString());
        activities.setId(ids);
        if (haveImg)
            activities.setImage(fileToBase64(file));
        else
            activities.setImage("");
        Gson gson = new Gson();
        String Json = gson.toJson(activities);
        final RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.modifyHappyContent)
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
            }
        });
    }

    @Override
    public void requestCommit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        final HappyContent activities = new HappyContent();
        activities.setContent(addAc_content.getText().toString());
        activities.setTitle(addAc_title.getText().toString());
        if (haveImg)
            activities.setImage(fileToBase64(file));
        else
            activities.setImage("");
        Gson gson = new Gson();
        String Json = gson.toJson(activities);
        Log.e("测试通告", Json);
        final RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.addHappyContent)
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
            switch (msg.what) {
                case 1:
                    if (!Boolean.parseBoolean(msg.obj.toString())) {
                        Toast.makeText(AddFengActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddFengActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                        EventMsg message = new EventMsg();
                        message.setTag(Constants.CONNET_SUCCESS);//发送链接成功的信号
                        EventBus.getDefault().post(message);
                        finish();
                    }
                    break;
                case 2:
                    if (!Boolean.parseBoolean(msg.obj.toString())) {
                        Toast.makeText(AddFengActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddFengActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        EventMsg message = new EventMsg();
                        message.setTag(Constants.CONNET_SUCCESS);//发送链接成功的信号
                        EventBus.getDefault().post(message);
                        finish();
                    }
                    break;
                case 3:
                    if (!Boolean.parseBoolean(msg.obj.toString())) {
                        Toast.makeText(AddFengActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddFengActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        EventMsg message = new EventMsg();
                        message.setTag(Constants.CONNET_SUCCESS);//发送链接成功的信号
                        EventBus.getDefault().post(message);
                        finish();
                    }
                    break;
            }
            return true;
        }
    });

    @Override
    public void requestDelete() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        final Activities activities = new Activities();
        activities.setId(ids);
        Gson gson = new Gson();
        String Json = gson.toJson(activities);
        final RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), Json);
        final Request request = new Request.Builder()
                .url(Urls.removeHappyContent)
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
            case R.id.addAc_back:
                finish();
                break;
            case R.id.addAc_img:
                if (PrefUtils.getBoolean(this, "isAdmin", true)) {
                    toPicture();
                } else {
                    startActivity(new Intent(this, ImgActivity.class).putExtra("img", imgs));

                }

                break;
            case R.id.addAc_commit:
                if (isFirst)
                    requestCommit();
                else
                    requestReCommit();
                break;
            case R.id.addAc_delete:
                requestDelete();
                break;
        }
    }

    private String picturePath = "";
    public static File file = null;
    public static Uri uri01;
    private boolean haveImg;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:   //相册返回的数据（相册的返回码）
                uri01 = data.getData();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri01));
                    addAc_img.setImageBitmap(bitmap);
                    Uri uri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    file = new File(picturePath);
                    haveImg = true;
                    cursor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
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
}


