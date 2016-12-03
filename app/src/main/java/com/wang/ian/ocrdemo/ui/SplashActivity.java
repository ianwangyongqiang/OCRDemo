package com.wang.ian.ocrdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wang.ian.ocrdemo.R;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                startActivity(new Intent(SplashActivity.this, CaptureActivity.class));
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0, 3000);
    }
}
