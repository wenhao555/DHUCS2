package com.example.dhucs.listeners;

import android.view.View;


/**
 * 通用点击事件监听
 * <p/>
 * Created by liumeng on 2016/8/4.
 */
public abstract class CustomClickListener implements View.OnClickListener
{
    private String currentViewClassName;

    public CustomClickListener(String currentViewClassName)
    {
        this.currentViewClassName = currentViewClassName;
    }

    @Override
    public void onClick(View view)
    {

        clickOpt(view);
    }

    public abstract void clickOpt(View v);
}
