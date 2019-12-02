package me.android.library.ui.ext.popups;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.google.common.collect.Lists;

import java.util.Calendar;
import java.util.List;

import me.android.library.ui.R;
import me.android.library.ui.ext.OkCancelCallback;
import me.android.library.ui.ext.views.DateWheelView;
import me.android.library.ui.ext.views.SimpleAreaWheelView;
import me.android.library.ui.ext.views.TimeWheelView;
import me.android.library.ui.ext.views.WheelView;
import me.java.library.common.Callback;

/**
 * Created by sylar on 15/9/21.
 */
public class Popups {


    public static PopupWindow newDatePicker(Context cx, Calendar current, final Callback<Calendar> callback) {
        final DateWheelView view = new DateWheelView(cx);
        view.setDefault(current);

        return PopupHelper.newPickerPopup(view, new OkCancelCallback<PopupWindow>() {
            @Override
            public void onOK(PopupWindow popupWindow) {
                if (callback != null) {
                    callback.onSuccess(view.getSelected());
                }
            }

            @Override
            public void onCancel(PopupWindow popupWindow) {

            }
        });

    }

    public static PopupWindow newTimePicker(Context cx, Calendar current, final Callback<Calendar> callback) {
        final TimeWheelView view = new TimeWheelView(cx);
        view.setDefault(current);

        return PopupHelper.newPickerPopup(view, new OkCancelCallback<PopupWindow>() {
            @Override
            public void onOK(PopupWindow popupWindow) {
                if (callback != null) {
                    callback.onSuccess(view.getSelected());
                }
            }

            @Override
            public void onCancel(PopupWindow popupWindow) {

            }
        });
    }

    public static PopupWindow newSimpleAreaPicker(Context cx, final Callback<String> callback) {
        final SimpleAreaWheelView view = new SimpleAreaWheelView(cx);

        return PopupHelper.newPickerPopup(view, new OkCancelCallback<PopupWindow>() {
            @Override
            public void onOK(PopupWindow popupWindow) {
                String str = String.format("%s%s%s",
                        view.getSelectedItem1(),
                        view.getSelectedItem2(),
                        view.getSelectedItem3());
                if (callback != null) {
                    callback.onSuccess(str);
                }
            }

            @Override
            public void onCancel(PopupWindow popupWindow) {

            }
        });
    }

    public static PopupWindow newWheelPicker(Context cx, int min, int max, int defaultValue, final Callback<Integer> callback) {
        List<Integer> list = Lists.newArrayList();
        for (int i = min; i < max; i++) {
            list.add(i);
        }

        return newWheelPicker(cx, list, list.indexOf(defaultValue), callback);
    }

    public static <T> PopupWindow newWheelPicker(Context cx, List<T> list, int defaultIndex, final Callback<T> callback) {
        final WheelView view = (WheelView) LayoutInflater.from(cx).inflate(R.layout.view_wheel, null);
        view.setData(list);

        if (defaultIndex >= 0 && defaultIndex < list.size()) {
            view.setDefault(defaultIndex);
        } else {
            view.setDefault(list.size() / 2);
        }

        return PopupHelper.newPickerPopup(view, new OkCancelCallback<PopupWindow>() {
            @Override
            public void onOK(PopupWindow popupWindow) {
                Object obj = view.getSelectedTag();
                if (callback != null) {
                    callback.onSuccess((T) obj);
                }
            }

            @Override
            public void onCancel(PopupWindow popupWindow) {

            }
        });
    }

    public static PopupWindow setPickerPopup(PopupWindow pop, View contentView, Drawable drawable) {
        pop.setContentView(contentView);

        pop.setBackgroundDrawable(drawable);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setAnimationStyle(R.style.bottom_window_style);
        pop.setFocusable(true);
        pop.setOutsideTouchable(false);

        return pop;
    }

}
