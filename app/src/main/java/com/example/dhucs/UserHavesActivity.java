package com.example.dhucs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class UserHavesActivity extends AppCompatActivity {
    private EditText addAc_title, addAc_content, ac_area, ac_time, ac_date;
    private ImageView addAc_img;
    String imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_haves);
        addAc_title = findViewById(R.id.addAc_title);
        addAc_content = findViewById(R.id.addAc_content);
        ac_area = findViewById(R.id.ac_area);
        ac_time = findViewById(R.id.ac_time);
        ac_date = findViewById(R.id.ac_date);
        addAc_img = findViewById(R.id.addAc_img);
        addAc_title.setText(getIntent().getStringExtra("title"));
        ac_area.setText(getIntent().getStringExtra("place"));
        ac_time.setText(getIntent().getStringExtra("workLong"));
        ac_date.setText(getIntent().getStringExtra("timeLong"));
        addAc_title.setEnabled(false);
        addAc_content.setEnabled(false);
        ac_area.setEnabled(false);
        ac_time.setEnabled(false);
        ac_date.setEnabled(false);
        addAc_content.setText(getIntent().getStringExtra("content"));
        imgs = getIntent().getStringExtra("img");
        byte[] decodedString = Base64.decode(imgs
                .substring(imgs
                        .indexOf(",") + 1), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        addAc_img.setImageBitmap(decodedByte);

    }

    public void mFinish(View view) {
        finish();
    }

    public void mImage(View view) {
        if (!imgs.equals(""))
            startActivity(new Intent(this, ImgActivity.class).putExtra("img", imgs));
    }
}
