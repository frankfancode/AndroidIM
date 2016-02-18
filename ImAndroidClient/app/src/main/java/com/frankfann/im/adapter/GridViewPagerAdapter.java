package com.frankfann.im.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

/**
 * Created by user on 2015/10/15.
 */
public class GridViewPagerAdapter extends PagerAdapter {
    private List<GridView> mLists;

    public GridViewPagerAdapter(Context context, List<GridView> array) {
        this.mLists = array;
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {

        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup arg0, int arg1) {
        (arg0).addView(mLists.get(arg1));
        return mLists.get(arg1);
    }

    @Override
    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        (arg0).removeView((View) arg2);
    }
}