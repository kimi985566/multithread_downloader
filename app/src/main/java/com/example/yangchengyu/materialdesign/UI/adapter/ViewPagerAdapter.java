package com.example.yangchengyu.materialdesign.UI.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by YangChengyu on 2017/3/28.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private List<View> mViews;
    private Context mContext;

    public ViewPagerAdapter(List<View> views, Context context) {
        this.mViews = views;
        this.mContext = context;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return this.mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }


}
