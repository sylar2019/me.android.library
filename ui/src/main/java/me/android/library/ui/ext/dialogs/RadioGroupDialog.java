package me.android.library.ui.ext.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.common.base.Preconditions;

public class RadioGroupDialog {
    public static AlertDialog show(Context cx, String title, String[] items,
                                   final ItemSeletedCallback callback) {
        Preconditions.checkNotNull(items, "items is null");
        Preconditions.checkState(items.length > 0, "items is empty");

        RadioGroup group = new RadioGroup(cx);
        group.setGravity(Gravity.LEFT);
        group.setOrientation(RadioGroup.VERTICAL);

        RadioButton btn;
        for (String txt : items) {
            btn = new RadioButton(cx);
            btn.setPadding(10, 10, 10, 10);
            btn.setTextSize(20);
            btn.setText(txt);
            group.addView(btn);
        }

        final AlertDialog dialog = DialogHelper.showAlert(title, group);

        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                dialog.dismiss();

                int index = group.indexOfChild(group.findViewById(checkedId));
                if (callback != null) {
                    callback.onItemSeleted(index);
                }
            }
        });

        return dialog;

    }

    public interface ItemSeletedCallback {
        void onItemSeleted(int index);
    }


}
