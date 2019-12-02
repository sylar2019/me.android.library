package me.android.library.ui.ext.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by sylar on 15/6/4.
 */
public class ExtPageAdapter extends PagerAdapter {

    protected List<View> list = Lists.newArrayList();

    public void loadViews(List<View> views) {

        list.clear();
        notifyDataSetChanged();

        if (views != null && views.size() > 0) {
            list.addAll(views);
            notifyDataSetChanged();
        }
    }

    public List<View> getViews() {
        return list;
    }

    public View getPage(int position) {
        Preconditions.checkState(position >= 0 && position < list.size(), "参数越界:ExtPageAdapter.getPage()");
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = list.get(position);
        container.addView(view);
        return view;
    }
}
