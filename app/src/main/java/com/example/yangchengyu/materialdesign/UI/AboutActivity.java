package com.example.yangchengyu.materialdesign.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.example.yangchengyu.materialdesign.R;
import com.example.yangchengyu.materialdesign.Utils.StatusBarActivity;

/**
 * Created by YangChengyu on 2017/4/10.
 */

public class AboutActivity extends StatusBarActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.about_activity_toolBar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
