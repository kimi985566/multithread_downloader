package com.example.yangchengyu.materialdesign.UI;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.yangchengyu.materialdesign.R;
import com.example.yangchengyu.materialdesign.Utils.StatusBarActivity;

import cn.lemon.view.Action;
import cn.lemon.view.CountdownView;

/**
 * Created by YangChengyu on 2017/3/27.
 */

public class SplashActivity extends StatusBarActivity {
    private static final long DELAY_MILLIS = 1000;
    private CountdownView mCountdownView;
    private Handler mHandler;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared_preferences();
    }

    private void shared_preferences() {
        mCountdownView = (CountdownView) findViewById(R.id.splash_activity_count_down);
        mHandler = new Handler();
        //打开Preferences，名称为setting，假设存在则打开它，否则创建新的Preferences
        SharedPreferences sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        //获取setting，用编辑器进行编辑，设置真值为true
        Boolean isFirst = sharedPreferences.getBoolean("setting", true);
        //如果isTrue为ture,说明这个为第一次打开，则要转向引导页
        if (isFirst == true) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCountdownView.setText("欢迎");
                    Intent intent = new Intent(SplashActivity.this, FirstInActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }, DELAY_MILLIS);
            //否则就直接转向主页
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCountdownView.setText("跳过");
                    mCountdownView.setTextSize(12);
                    mCountdownView.setTime(5000);
                    mCountdownView.star();
                    mCountdownView.setOnFinishAction(new Action() {
                        @Override
                        public void onAction() {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            SplashActivity.this.finish();
                        }
                    });
                    mCountdownView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            SplashActivity.this.finish();
                        }
                    });
                }
            }, DELAY_MILLIS);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //第一次打开，则设置真值为false，既为不是第一次
        editor.putBoolean("setting", false);
        //保存编译器的值
        editor.commit();
    }
}