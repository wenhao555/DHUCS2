package com.example.dhucs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.dhucs.fragment.AdminAcFragment;
import com.example.dhucs.fragment.AdminFengFragment;
import com.example.dhucs.fragment.AdminUserFragment;
import com.example.dhucs.fragment.MineFragment;
import com.example.dhucs.fragment.UserAcFragment;
import com.example.dhucs.fragment.UserFengFragment;
import com.example.dhucs.utils.PrefUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{
    private BottomNavigationView bottomNavigation, bottomNavigation2;
    private Fragment[] fragmentsAdmin, fragmentsUser;
    private AdminAcFragment adminAcFragment;
    private AdminFengFragment adminFengFragment;
    private AdminUserFragment adminUserFragment;

    private UserAcFragment userAcFragment;
    private UserFengFragment userFengFragment;
    private MineFragment mineFragment;
    private int lastFragments = 0;

    private void init()
    {
        adminAcFragment = new AdminAcFragment();
        adminFengFragment = new AdminFengFragment();
        adminUserFragment = new AdminUserFragment();

        userAcFragment = new UserAcFragment();
        userFengFragment = new UserFengFragment();
        mineFragment = new MineFragment();

        fragmentsAdmin = new Fragment[]{adminAcFragment, adminFengFragment, adminUserFragment};
        fragmentsUser = new Fragment[]{userAcFragment, userFengFragment, mineFragment};
        lastFragments = 0;
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation2 = findViewById(R.id.bottomNavigation2);
        if (PrefUtils.getBoolean(this, "isAdmin", true))
        {
            bottomNavigation2.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.mframeLayout, fragmentsAdmin[0]).show(fragmentsAdmin[0]).commit();
            bottomNavigation.setOnNavigationItemSelectedListener(changeFragment);
        } else
        {
            bottomNavigation.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.mframeLayout, fragmentsUser[0]).show(fragmentsUser[0]).commit();
            bottomNavigation2.setOnNavigationItemSelectedListener(changeFragment);
        }


    }

    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.home:
                    if (lastFragments != 0)
                    {
                        switchFragment(lastFragments, 0);
                        lastFragments = 0;
                    }
                    return true;
                case R.id.mien:
                    if (lastFragments != 1)
                    {
                        switchFragment(lastFragments, 1);
                        lastFragments = 1;
                    }

                    return true;
                case R.id.mine:
                    if (lastFragments != 2)
                    {
                        switchFragment(lastFragments, 2);
                        lastFragments = 2;
                    }

                    return true;
            }

            return false;
        }
    };

    private void switchFragment(int lastFragments, int index)
    {
        if (PrefUtils.getBoolean(this, "isAdmin", true))
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(fragmentsAdmin[lastFragments]);
            if (!fragmentsAdmin[index].isAdded())
            {
                transaction.add(R.id.mframeLayout, fragmentsAdmin[index]);
            }
            transaction.show(fragmentsAdmin[index]).commitAllowingStateLoss();
        } else
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(fragmentsUser[lastFragments]);
            if (!fragmentsUser[index].isAdded())
            {
                transaction.add(R.id.mframeLayout, fragmentsUser[index]);
            }
            transaction.show(fragmentsUser[index]).commitAllowingStateLoss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
}
