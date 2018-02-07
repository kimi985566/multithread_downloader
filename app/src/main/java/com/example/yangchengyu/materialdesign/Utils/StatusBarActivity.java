package com.example.yangchengyu.materialdesign.Utils;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.example.yangchengyu.materialdesign.R;

/**
 * Created by YangChengyu on 2017/3/27.
 */


public abstract class StatusBarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutResId());
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    abstract protected int getLayoutResId();
}
