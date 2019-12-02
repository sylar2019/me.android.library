package me.android.library.ui.ext.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import me.android.library.ui.R;

public class ProgressDialogHelper {


    static Dialog dialog;

    public static void setRunning(Context cx, boolean isRunning) {
        if (isRunning) {
            show(cx);
        } else {
            hide();
        }
    }

    public static Dialog show(Context cx) {
        return show(cx, "正在加载,请稍候...");
    }

    public static Dialog show(Context cx, String msg) {
        return show(cx, msg, false);
    }

    public static Dialog show(Context cx, String msg, boolean cancelable) {
        return show(cx, msg, cancelable, false);
    }

    public static Dialog show(Context cx, String msg,
                              boolean cancelable, boolean canceledOnTouchOutside) {

        if (dialog == null) {
            View view = LayoutInflater.from(cx).inflate(R.layout.dialog_progress, null);
            final ImageView imgAnim = view.findViewById(R.id.loadingImageView);
            final Animation anim = AnimationUtils.loadAnimation(cx, R.anim.common_anim_loading);
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    imgAnim.setAnimation(anim);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    imgAnim.clearAnimation();
                }
            });

            dialog = DialogHelper.show(view);
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        }

        TextView txtMsg = dialog.findViewById(R.id.txtMsg);
        txtMsg.setText(msg);

        return dialog;
    }

    public static void hide() {

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

}
