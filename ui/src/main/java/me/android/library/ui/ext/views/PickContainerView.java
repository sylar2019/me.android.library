package me.android.library.ui.ext.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import me.android.library.ui.R;
import me.android.library.ui.ext.OkCancelCallback;

/**
 * Created by sylar on 15/9/20.
 */
public class PickContainerView extends FrameLayout implements View.OnClickListener {

    protected Context cx;
    protected TextView txtConfirm, txtCancel;
    protected FrameLayout divMain;
    protected OkCancelCallback<PickContainerView> callback;


    public PickContainerView(Context cx) {
        super(cx);
        init(cx, null);
    }

    public PickContainerView(Context cx, AttributeSet attrs) {
        super(cx, attrs);
        init(cx, attrs);
    }

    @Override
    public void onClick(View v) {
        if (callback != null) {
            if (v == txtCancel) {
                callback.onCancel(this);

            } else if (v == txtConfirm) {
                callback.onOK(this);
            }
        }
    }


    void init(Context cx, AttributeSet attrs) {
        this.cx = cx;

        LayoutInflater.from(cx).inflate(R.layout.view_pick_container, this, true);
        divMain = this.findViewById(R.id.divMain);
        txtConfirm = this.findViewById(R.id.txtConfirm);
        txtCancel = this.findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(this);
        txtConfirm.setOnClickListener(this);

    }

    public PickContainerView setContentView(View view) {
        divMain.removeAllViews();
        divMain.addView(view);
        return this;
    }

    public PickContainerView setOkCancelCallback(OkCancelCallback<PickContainerView> callback) {
        this.callback = callback;
        return this;
    }

}
