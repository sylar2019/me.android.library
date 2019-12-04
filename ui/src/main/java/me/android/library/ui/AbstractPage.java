package me.android.library.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import java.lang.ref.WeakReference;

import me.android.library.common.enums.ConnectivityMode;
import me.android.library.common.event.ConnectionModeChangedEvent;
import me.android.library.common.utils.SoftInputUtils;
import me.java.library.utils.base.guava.AsyncEventUtils;

abstract public class AbstractPage extends Fragment implements Page {

    protected String pageKey;
    protected String title;
    protected FragmentActivity activity;
    protected Context cx;
    protected Resources r;
    protected View rootView;
    protected MyHandler handler;

    @Override
    public String getPageKey() {
        return pageKey;
    }

    @Override
    public void setPageKey(String pageKey) {
        this.pageKey = pageKey;
    }

    @Override
    public String getPageTitle() {
        return title;
    }

    @Override
    public void setPageTitle(String pageTitle) {
        this.title = pageTitle;
    }

    @Override
    public void onPageActivated() {

    }

    @Override
    public void onPageInActivated() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncEventUtils.regist(this);

        activity = getActivity();
        cx = activity;
        r = cx.getResources();

        handler = new MyHandler(this) {
            public void handleMessage(Message msg) {
                if (frm.get() == null) {
                    return;
                }
                processMessage(msg);
            }

        };
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AsyncEventUtils.unregist(this);
        handler = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
//            MemoryUtils.disposeView(rootView);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SoftInputUtils.hide(activity);

        if (rootView != null) {
            rootView.setOnTouchListener(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (rootView != null) {
            rootView.setOnTouchListener(new OnTouchListener() {

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler = null;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//            //在androidx中，不需要这种解决方法。
//            try {
//                java.lang.reflect.Field childFragmentManager = Fragment.class
//                        .getDeclaredField("mChildFragmentManager");
//                if (childFragmentManager != null) {
//                    childFragmentManager.setAccessible(true);
//                    childFragmentManager.set(this, null);
//                }
//
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onEvent(ConnectionModeChangedEvent event) {
        onConnectionModeChanged(event.getContent());
    }

    protected void onConnectionModeChanged(ConnectivityMode mode) {
    }

    protected void postDelay(long delay, Runnable runnable) {
        if (handler != null) {
            handler.postDelayed(runnable, delay);
        }
    }

    protected void sendEmptyMessage(int what) {
        if (handler != null) {
            handler.sendEmptyMessage(what);
        }
    }

    protected void sendMessage(Message msg) {
        if (handler != null) {
            handler.sendMessage(msg);
        }
    }

    protected void processMessage(Message msg) {
    }

    protected void postEvent(Object event) {
        AsyncEventUtils.postEvent(event);
    }

    static private class MyHandler extends Handler {
        WeakReference<Fragment> frm;

        MyHandler(Fragment frm) {
            this.frm = new WeakReference<>(frm);
        }
    }

}
