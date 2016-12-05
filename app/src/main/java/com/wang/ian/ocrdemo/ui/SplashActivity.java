package com.wang.ian.ocrdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.api.services.translate.model.LanguagesListResponse;
import com.wang.ian.ocrdemo.R;
import com.wang.ian.ocrdemo.logic.TranslateManager;
import com.wang.ian.ocrdemo.logic.TranslateObservable;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Handler handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                startActivity(new Intent(SplashActivity.this, CaptureActivity.class));
                finish();
            }
        };

        TranslateObservable.getSupportLanguageObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<LanguagesListResponse>() {
                    @Override
                    public void call(LanguagesListResponse languagesListResponse) {
                        TranslateManager.getInstance().setSupportLanguageList(languagesListResponse);
                        handler.sendEmptyMessageDelayed(0, 1000);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(SplashActivity.this, R.string.initial_error, Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}
