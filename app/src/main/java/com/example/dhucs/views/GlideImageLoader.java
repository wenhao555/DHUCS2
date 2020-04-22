package com.example.dhucs.views;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

public class GlideImageLoader extends ImageLoader
{
    @Override
    public void displayImage(Context context, Object path, ImageView imageView)
    {
        //Glide 加载图片简单用法
        Glide.with(context).load(path).into(imageView);

        //Picasso 加载图片简单用法

        //用fresco加载图片简单用法，记得要写下面的createImageView方法
    }

    @Override
    public ImageView createImageView(Context context)
    {
        ImageView imageView = new ImageView(context);
        return imageView;
    }
}
