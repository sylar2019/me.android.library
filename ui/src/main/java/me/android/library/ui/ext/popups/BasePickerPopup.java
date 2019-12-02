package me.android.library.ui.ext.popups;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.PopupWindow;

import me.android.library.ui.R;
import me.android.library.ui.ext.OkCancelCallback;
import me.android.library.ui.ext.views.PickContainerView;

/**
 * Created by sylar on 15/9/20.
 */
public class BasePickerPopup extends PopupWindow {

    public BasePickerPopup(View view, final OkCancelCallback<PopupWindow> callback) {
        this(view, callback, view.getContext().getResources().getDrawable(R.color.main_background));
    }

    public BasePickerPopup(View view, final OkCancelCallback<PopupWindow> callback, Drawable drawable) {
        super(view.getContext());

        PickContainerView container = new PickContainerView(view.getContext());
        container.setContentView(view).setOkCancelCallback(new OkCancelCallback<PickContainerView>() {
            @Override
            public void onOK(PickContainerView pickContainerView) {
                dismiss();
                if (callback != null) {
                    callback.onOK(BasePickerPopup.this);
                }
            }

            @Override
            public void onCancel(PickContainerView pickContainerView) {
                dismiss();
                if (callback != null) {
                    callback.onCancel(BasePickerPopup.this);
                }
            }
        });
        Popups.setPickerPopup(this, container, drawable);
    }
}
