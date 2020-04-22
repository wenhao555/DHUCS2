package com.example.dhucs;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.dhucs.utils.ZXingUtils;

public class ImgActivity extends AppCompatActivity
{
    private ImageButton close;
    private ImageView image;
    private String imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);
        image = findViewById(R.id.image);
        close = findViewById(R.id.close);
        imgs = getIntent().getStringExtra("img");
        if (imgs.contains("626128095"))

        {
            Bitmap bitmap = ZXingUtils.createQRImage(imgs.substring(9), 400, 400);
            image.setImageBitmap(bitmap);
        } else
        {
            byte[] decodedString = Base64.decode(imgs
                    .substring(imgs
                            .indexOf(",") + 1), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);
        }
        close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
}
