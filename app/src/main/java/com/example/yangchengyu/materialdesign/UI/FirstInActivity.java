package com.example.yangchengyu.materialdesign.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.yangchengyu.materialdesign.R;
import com.example.yangchengyu.materialdesign.UI.adapter.ViewPagerAdapter;
import com.example.yangchengyu.materialdesign.Utils.StatusBarActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YangChengyu on 2017/3/27.
 */

public class FirstInActivity extends StatusBarActivity implements ViewPager.OnClickListener {
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<View> mViewList;
    private Button mButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initView();
    }

    public void initView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mViewList = new ArrayList<>();
        mViewList.add(inflater.inflate(R.layout.guide_one, null));
        mViewList.add(inflater.inflate(R.layout.guide_two, null));
        mViewPagerAdapter = new ViewPagerAdapter(mViewList, this);
        mViewPager = (ViewPager) findViewById(R.id.firstIn_activity_viewPager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnClickListener(this);
        this.mButton = (Button) mViewList.get(1).findViewById(R.id.guide_two_btn_enter);
        this.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstInActivity.this, MainActivity.class);
                startActivity(intent);
                FirstInActivity.this.finish();
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_firstin;
    }

    @Override
    public void onClick(View v) {
    }
}
