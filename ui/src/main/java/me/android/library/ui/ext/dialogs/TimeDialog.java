package me.android.library.ui.ext.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import me.android.library.common.utils.ViewUtils;
import me.android.library.ui.ext.OkCancelCallback;

public class TimeDialog {

    public static AlertDialog show(Context cx, String title, Date time,
                                   final TimeSeletedCallback callback) {

        final TimePicker tpView = new TimePicker(cx);
        tpView.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        tpView.setIs24HourView(true);

        if (time != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(time);
            tpView.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            tpView.setCurrentMinute(c.get(Calendar.MINUTE));
        }

        return DialogHelper.showQuestView(cx, title, tpView, new OkCancelCallback<Dialog>() {
            @Override
            public void onOK(Dialog dialog) {
                tpView.clearFocus();
                ViewUtils.setDialogShowField(dialog, true);

                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, tpView.getCurrentHour());
                c.set(Calendar.MINUTE, tpView.getCurrentMinute());

                if (callback != null) {
                    callback.onTimeSeleted(c.getTime());
                }
            }

            @Override
            public void onCancel(Dialog dialog) {

            }
        });
    }

    public interface TimeSeletedCallback {
        void onTimeSeleted(Date time);
    }


}
