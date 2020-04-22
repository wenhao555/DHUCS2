package com.example.dhucs.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dhucs.LoginActivity;
import com.example.dhucs.R;
import com.example.dhucs.UpdatePwdActivity;
import com.example.dhucs.UserActivity;
import com.example.dhucs.UserHaveActivity;
import com.example.dhucs.utils.Constants;
import com.example.dhucs.utils.EventMsg;
import com.example.dhucs.utils.PrefUtils;
import com.example.dhucs.views.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment {

    public MineFragment() {
        // Required empty public constructor
    }

    private CircleImageView mine_img;
    private TextView mine_name;
    private LinearLayout ming_data, ming_uppwd, ming_haveac;
    private Button mine_exit;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        mine_img = view.findViewById(R.id.mine_img);
        ming_haveac = view.findViewById(R.id.ming_haveac);
        mine_exit = view.findViewById(R.id.mine_exit);
        if (!PrefUtils.getString(getActivity(), "imgpath", " ").equals("")) {
            byte[] decodedString = Base64.decode(PrefUtils.getString(getActivity(), "imgpath", " ")
                    .substring(PrefUtils.getString(getActivity(), "imgpath", " ")
                            .indexOf(",") + 1), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            mine_img.setImageBitmap(decodedByte);
        }
        ming_haveac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserHaveActivity.class));
            }
        });
        mine_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.setString(getActivity(), "name", "");
                PrefUtils.setString(getActivity(), "password", "");
                PrefUtils.setString(getActivity(), "sex", "");
                PrefUtils.setString(getActivity(), "birth", "");
                PrefUtils.setString(getActivity(), "account", "");
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        mine_name = view.findViewById(R.id.mine_name);
        ming_data = view.findViewById(R.id.ming_data);
        ming_uppwd = view.findViewById(R.id.ming_uppwd);
        mine_name.setText(PrefUtils.getString(getActivity(), "account", ""));
        ming_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserActivity.class));
            }
        });
        ming_uppwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdatePwdActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUserActivity(EventMsg msg) {
        switch (msg.getTag()) {

            case Constants.CONNET_SUCCESS:
                Log.e("测试接收", "接收");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!PrefUtils.getString(getActivity(), "imgpath", " ").endsWith("")) {
                            byte[] decodedString = Base64.decode(PrefUtils.getString(getActivity(), "imgpath", " ")
                                    .substring(PrefUtils.getString(getActivity(), "imgpath", " ")
                                            .indexOf(",") + 1), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            mine_img.setImageBitmap(decodedByte);
                        }
                    }
                });
                break;
        }
    }

}
