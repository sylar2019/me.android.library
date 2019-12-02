package me.android.library.ui.ext.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import me.android.library.ui.R;
import me.android.library.ui.ext.OkCancelCallback;

public class DialogHelper {

    // ======================================================================================================
    //                                         FullScreenDialog
    // ======================================================================================================

    // --------------------------------------- showPickDialog ---------------------------------------
    public static Dialog showPickDialog(View view, OkCancelCallback<Dialog> callback) {
        Dialog dialog = newPickerDialog(view, callback);
        return showPickDialog(dialog);
    }

    public static Dialog showPickDialog(Dialog dialog) {
        return show(dialog, Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, R.style.bottom_window_style);
    }

    public static Dialog newPickerDialog(View view, OkCancelCallback<Dialog> callback) {
        return new BasePickerDialog(view.getContext(), view, callback);
    }

    // --------------------------------------- show with gravity & anim ---------------------------------------

    public static Dialog show(View view) {
        return show(view, Gravity.CENTER, -1);
    }

    public static Dialog show(View view, int gravity) {
        return show(view, gravity, -1);
    }

    public static Dialog show(View view, int gravity, int animStyle) {
        Dialog dialog = newDialog(view);
        return show(dialog, gravity, animStyle);
    }

    public static Dialog show(Dialog dialog, int gravity) {
        return show(dialog, gravity, -1);
    }

    public static Dialog show(Dialog dialog, int gravity, int animStyle) {
        Window window = dialog.getWindow();
        window.setGravity(gravity);
        if (animStyle > 0) {
            window.setWindowAnimations(animStyle);
        }
        dialog.show();
        return dialog;
    }

    // --------------------------------------- newDialog ---------------------------------------

    public static Dialog newDialog(View view) {
        Preconditions.checkNotNull(view, "content view of dialog is null");
        Dialog dialog = new Dialog(view.getContext(), R.style.Theme_Dialog_FullScreen);
        dialog.setContentView(view);
        return dialog;
    }


    // ======================================================================================================
    //                                         AlertDialog
    // ======================================================================================================


    // --------------------------------------- Quest ---------------------------------------


    public static AlertDialog showQuestView(Context cx, String title, View view,
                                            final OkCancelCallback<Dialog> callback) {
        AlertDialog.Builder builder = newQuestBuilder(cx, title, callback);
        builder.setView(view);
        AlertDialog dlg = builder.create();
        dlg.show();
        return dlg;
    }


    public static AlertDialog showQuestMsg(Context cx, String title, String message,
                                           final OkCancelCallback<Dialog> callback) {
        AlertDialog.Builder builder = newQuestBuilder(cx, title, callback);
        builder.setMessage(message);
        AlertDialog dlg = builder.create();
        dlg.show();
        return dlg;
    }

    public static AlertDialog.Builder newQuestBuilder(Context cx, String title,
                                                      final OkCancelCallback<Dialog> callback) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        callback.onOK((Dialog) dialog);
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        callback.onCancel((Dialog) dialog);
                    }
                }
            }
        };

        AlertDialog.Builder builder = newBuilder(cx, title);
        builder.setPositiveButton(getTextOK(cx), listener);
        builder.setNegativeButton(getTextCancel(cx), listener);
        return builder;
    }


    // --------------------------------------- Hint ---------------------------------------

    public static AlertDialog showHintView(Context cx, String title, View view) {
        AlertDialog.Builder builder = newBuilder(cx, title);
        builder.setView(view);
        AlertDialog dlg = builder.create();
        dlg.show();
        return dlg;
    }


    public static AlertDialog showHintMsg(Context cx, String title, String message) {
        AlertDialog.Builder builder = newBuilder(cx, title);
        builder.setMessage(message);
        AlertDialog dlg = builder.create();
        dlg.show();
        return dlg;
    }

    // --------------------------------------- Custom Alert ---------------------------------------

    public static AlertDialog showAlert(String title, View view) {
        Preconditions.checkNotNull(view, "content view of dialog is null");
        AlertDialog.Builder builder = newBuilder(view.getContext(), title);
        builder.setView(view);
        AlertDialog dlg = builder.create();
        dlg.show();
        return dlg;
    }

    public static AlertDialog.Builder newBuilder(Context cx, String title) {
        Preconditions.checkNotNull(cx, "context is null");
        AlertDialog.Builder builder = new AlertDialog.Builder(cx);
        if (!Strings.isNullOrEmpty(title)) {
            builder.setTitle(title);
        }

        return builder;
    }


    // --------------------------------------- const ---------------------------------------

    static String getTextOK(Context cx) {
        return cx.getString(R.string.common_ok);
    }

    static String getTextCancel(Context cx) {
        return cx.getString(R.string.common_cancel);
    }


}
