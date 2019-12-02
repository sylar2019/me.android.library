package me.android.library.common.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class ViewUtils {
    private ViewUtils() {
        throw new AssertionError();
    }

    static public void getViewSize(final View view, final OnCalcSizeCallback callback) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                Size size = new Size();
                size.width = view.getWidth();
                size.height = view.getHeight();

                if (callback != null) {
                    callback.onCompleted(size);
                }
            }
        });
    }

    static public void setTransparentBackground(View view) {

        if (view != null) {
            int color = view.getContext().getResources()
                    .getColor(android.R.color.transparent);
            view.setBackgroundColor(color);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * @param dialog
     * @param closable true - 可以关闭 false - 不能关闭
     */
    public static void setDialogShowField(DialogInterface dialog,
                                          boolean closable) {
        try {
            Field field = dialog.getClass().getSuperclass()
                    .getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, closable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setFullScreen(Context cx, Dialog dialog) {
        setFullScreen(cx, dialog, Gravity.FILL);
    }

    // -----------------------------------------------Dialog----------------------------------------------

    public static void setFullScreen(Context cx, Dialog dialog, int gravity) {

        Activity atv = (Activity) cx;
        Window window = dialog.getWindow();

        // 此处可以设置dialog显示的位置
        window.setGravity(gravity);

        //设置宽高
        WindowManager.LayoutParams lp = window.getAttributes();
        Display display = atv.getWindowManager().getDefaultDisplay();
        lp.width = display.getWidth();
        lp.height = display.getHeight();

        window.setAttributes(lp);
    }

    static public void scrollTop(final ScrollView sv) {

        sv.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 50);
    }

    static public void scrollBottom(final ScrollView sv) {
        sv.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 50);
    }


    // -----------------------------------------------ViewPager----------------------------------------------

//    static public void setScroolDuration(ViewPager pager, int duration) {
//        try {
//            Field field = ViewPager.class.getDeclaredField("mScroller");
//            field.setAccessible(true);
//            FixedSpeedScroller scroller = new FixedSpeedScroller(
//                    pager.getContext(), new AccelerateInterpolator());
//            field.set(pager, scroller);
//            scroller.setmDuration(duration);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public interface OnCalcSizeCallback {
        void onCompleted(Size size);
    }

    static public class Size {
        public int width, height;
    }

    static public class FixedSpeedScroller extends Scroller {
        private int mDuration = 1000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy,
                                int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public int getmDuration() {
            return mDuration;
        }

        public void setmDuration(int time) {
            mDuration = time;
        }
    }
}
