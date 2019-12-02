package me.android.library.ui.ext.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import me.android.library.ui.R;
import me.android.library.ui.ext.OkCancelCallback;
import me.android.library.ui.ext.views.PickContainerView;

/**
 * Created by sylar on 15/9/20.
 */
public class BasePickerDialog extends Dialog {

    public BasePickerDialog(Context cx, View view, final OkCancelCallback<Dialog> callback) {
        super(cx, R.style.Theme_Dialog_FullScreen);

        PickContainerView container = new PickContainerView(cx);
        container.setContentView(view).setOkCancelCallback(new OkCancelCallback<PickContainerView>() {

            @Override
            public void onOK(PickContainerView pickContainerView) {
                dismiss();
                if (callback != null) {
                    callback.onOK(BasePickerDialog.this);
                }
            }

            @Override
            public void onCancel(PickContainerView pickContainerView) {
                dismiss();
                if (callback != null) {
                    callback.onCancel(BasePickerDialog.this);
                }
            }

        });

        setContentView(container);
    }
}
