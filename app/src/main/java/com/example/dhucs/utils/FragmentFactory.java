package com.example.dhucs.utils;


import com.example.dhucs.fragment.AdminAcFragment;
import com.example.dhucs.fragment.AdminFengFragment;
import com.example.dhucs.fragment.AdminUserFragment;
import com.example.dhucs.fragment.MineFragment;
import com.example.dhucs.fragment.UserAcFragment;
import com.example.dhucs.fragment.UserFengFragment;

import androidx.fragment.app.Fragment;

/**
 * Created by shan_yao on 2016/6/17.
 */
public class FragmentFactory
{

    public static Fragment createForUser(int position)
    {
        Fragment fragment = null;
        switch (position)
        {
            case 0:
                fragment = new UserAcFragment();
                break;
            case 1:
                fragment = new MineFragment();
                break;
            case 2:
                fragment = new UserFengFragment();
                break;
        }
        return fragment;
    }

    public static Fragment createForAdmin(int position)
    {
        Fragment fragment = null;
        switch (position)
        {
            case 0:
                fragment = new AdminAcFragment();
                break;
            case 1:
                fragment = new AdminUserFragment();
                break;
            case 2:
                fragment = new AdminFengFragment();
                break;
        }
        return fragment;
    }
}
