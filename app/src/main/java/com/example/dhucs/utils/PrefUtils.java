package com.example.dhucs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;

/**
 * Created by ywh on 2018/4/26.
 */

public class PrefUtils
{
    public static final String PREF_NAME = "config";

    //check
    public static boolean getBoolean(Context ctx, String key,
                                     boolean defaultValue)
    {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    //text
    public static String getString(Context ctx, String key, String defaultValue)
    {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);

        return sp.getString(key, defaultValue);

    }

    public static void setString(Context ctx, String key, String value)
    {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    //radio
    public static int getInt(Context ctx, String key, int defaultValue)
    {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    public static void setBoolean(Context ctx, String key, boolean value)
    {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static void setInt(Context ctx, String key, int value)
    {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    //得到系统的时间
    public static String getSystemTime()
    {
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month + 1;
        int date = t.monthDay;
        int hour = t.hour; // 0-23
        int minute = t.minute;
        String tag = "AM";
        if (hour >= 12)
        {
            tag = "PM";
        }
        String time = date + "/" + month + "/" + year + " " + hour + ":"
                + minute + " " + tag;
        return time;
    }

}
