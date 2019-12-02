package me.android.library.ui.ext.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import java.util.List;

import me.android.library.common.utils.ToastUtils;
import me.android.library.ui.RefreshView;
import me.android.library.ui.ext.adapters.ExtBaseAdapter;
import me.java.library.common.Callback;

abstract public class AbsAdapterView<T, V extends AdapterView> extends FrameLayout implements RefreshView, AdapterView.OnItemClickListener {

    protected Context cx;
    protected ExtBaseAdapter<T> adapter;
    protected V adapterView;

    public AbsAdapterView(Context context) {
        super(context);
        init(context, null);
    }

    public AbsAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onRefresh();
    }

    void init(Context cx, AttributeSet attrs) {
        this.cx = cx;

        adapterView = getAdapterView();
        adapterView.setOnItemClickListener(this);
        this.addView(adapterView);

        onBeforeSetAdapter();

        adapter = getAdapter();
        adapterView.setAdapter(adapter);
    }

    // ===========================================================================================

    abstract protected V getAdapterView();

    abstract protected ExtBaseAdapter<T> getAdapter();

    abstract protected void onLoad(Callback<List<T>> callback);

    // ===========================================================================================

    @Override
    public void onRefresh() {
        onLoad(new Callback<List<T>>() {
            @Override
            public void onSuccess(List<T> foos) {
                adapter.loadData(foos);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        T foo = (T) adapterView.getAdapter().getItem(i);
        onClickItem(foo);
    }

    protected void onClickItem(T foo) {
    }

    /**
     * 在为listview设置adapter之前，还可以设置listview的head或foot
     */
    protected void onBeforeSetAdapter() {

    }
}
