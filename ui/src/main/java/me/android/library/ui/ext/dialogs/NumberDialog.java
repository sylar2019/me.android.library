package me.android.library.ui.ext.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import me.android.library.common.utils.ViewUtils;
import me.android.library.ui.ext.OkCancelCallback;

public class NumberDialog {

    public static AlertDialog show(Context cx, String title, final int min,
                                   final int max, int value, final NumberSeletedCallback callback) {

        final NumberPicker view = new NumberPicker(cx);
        view.setFocusable(false);
        view.setMinValue(min);
        view.setMaxValue(max);
        view.setValue(value);
        view.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        return DialogHelper.showQuestView(cx, title, view, new OkCancelCallback<Dialog>() {
            @Override
            public void onOK(Dialog dialog) {
                ViewUtils.setDialogShowField(dialog, true);
                int v = view.getValue();
                if (v < min || v > max) {
                    ViewUtils.setDialogShowField(dialog, false);
                } else {
                    if (callback != null) {
                        callback.onNumberSeleted(v);
                    }
                }
            }

            @Override
            public void onCancel(Dialog dialog) {

            }
        });
    }

    public interface NumberSeletedCallback {
        void onNumberSeleted(int value);
    }
}
