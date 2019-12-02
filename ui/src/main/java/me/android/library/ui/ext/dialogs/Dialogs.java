package me.android.library.ui.ext.dialogs;

import android.app.Dialog;
import android.content.Context;

import java.util.Calendar;
import java.util.List;

import me.android.library.ui.ext.OkCancelCallback;
import me.android.library.ui.ext.views.DateWheelView;
import me.android.library.ui.ext.views.SimpleAreaWheelView;
import me.android.library.ui.ext.views.TimeWheelView;
import me.android.library.ui.ext.views.WheelView;
import me.java.library.common.Callback;

/**
 * Created by sylar on 15/9/21.
 */
public class Dialogs {

    public static Dialog newDatePicker(Context cx, Calendar current, final Callback<Calendar> callback) {
        final DateWheelView view = new DateWheelView(cx);
        view.setDefault(current);

        return me.android.library.ui.ext.dialogs.DialogHelper.newPickerDialog(view, new OkCancelCallback<Dialog>() {
            @Override
            public void onOK(Dialog dialog) {
                if (callback != null) {
                    callback.onSuccess(view.getSelected());
                }
            }

            @Override
            public void onCancel(Dialog dialog) {

            }
        });

    }

    public static Dialog newTimePicker(Context cx, Calendar current, final Callback<Calendar> callback) {
        final TimeWheelView view = new TimeWheelView(cx);
        view.setDefault(current);

        return me.android.library.ui.ext.dialogs.DialogHelper.newPickerDialog(view, new OkCancelCallback<Dialog>() {
            @Override
            public void onOK(Dialog dialog) {
                if (callback != null) {
                    callback.onSuccess(view.getSelected());
                }
            }

            @Override
            public void onCancel(Dialog dialog) {

            }
        });
    }

    public static Dialog newSimpleAreaPicker(Context cx, final Callback<String> callback) {
        final SimpleAreaWheelView view = new SimpleAreaWheelView(cx);

        return me.android.library.ui.ext.dialogs.DialogHelper.newPickerDialog(view, new OkCancelCallback<Dialog>() {
            @Override
            public void onOK(Dialog dialog) {
                String str = String.format("%s%s%s",
                        view.getSelectedItem1(),
                        view.getSelectedItem2(),
                        view.getSelectedItem3());
                if (callback != null) {
                    callback.onSuccess(str);
                }
            }

            @Override
            public void onCancel(Dialog dialog) {

            }
        });
    }

    public static <T> Dialog newWheelPicker(Context cx, List<T> list, int defaultIndex, final Callback<T> callback) {
        final WheelView view = WheelView.newWheelView(cx, list, defaultIndex);
        return me.android.library.ui.ext.dialogs.DialogHelper.newPickerDialog(view, new OkCancelCallback<Dialog>() {
            @Override
            @SuppressWarnings("unchecked")
            public void onOK(Dialog dialog) {
                Object obj = view.getSelectedTag();
                if (callback != null) {
                    callback.onSuccess((T) obj);
                }
            }

            @Override
            public void onCancel(Dialog dialog) {

            }
        });
    }
}
