package com.example.dhucs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import android.Manifest;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class ScanQrActivity extends AppCompatActivity implements QRCodeView.Delegate, EasyPermissions.PermissionCallbacks
{
    private ImageView title;
    private static final String TAG = "扫描二维码";
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    public static final int RESULT_CODE = 111;
    public QRCodeView mQRCodeView;
    public TextView tvLight;
    private boolean isLight = false;
    private String type = "";
    private int ids = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        title = findViewById(R.id.title);
        mQRCodeView = findViewById(R.id.zxingview);
        tvLight = findViewById(R.id.tv_light);
        ids = getIntent().getIntExtra("ids", 0);
        title.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        requestCodeQrcodePermissions();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override
    protected void onStop()
    {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate()
    {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result)
    {
        vibrate();
        Intent intent = new Intent();
        intent.putExtra("signCode", result);
        intent.putExtra("ids", ids + "");
        setResult(RESULT_CODE, intent);
        finish();
    }


    @Override
    public void onScanQRCodeOpenCameraError()
    {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQrcodePermissions()
    {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms))
        {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms)
    {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms)
    {

    }
}
