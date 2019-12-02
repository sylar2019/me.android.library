package me.android.library.ui.ext.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class DelayEditText extends EditText {

    static final int MSGCODE = 0;
    int msgCount = 0;
    long delayMillis = 1000 * 2;
    OnTextChangedCallback callabck;
    MyHandler handler = new MyHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            if (refObj.get() == null) return;
            if (msg.what == MSGCODE) {
                if (msgCount == 1) {
                    msgCount = 0;

                    if (callabck != null) {
                        callabck.onTextChanged(DelayEditText.this.getText()
                                .toString());
                    }

                } else {
                    msgCount--;
                }

            }
        }
    };

    public DelayEditText(Context context) {
        super(context);
        init();
    }

    public DelayEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public DelayEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        setSingleLine();
        setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        this.setOnEditorActionListener(new OnEditorActionListener() {
                                           @Override
                                           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                               return actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
                                           }
                                       }

        );
    }

    public void setDelay(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public void setOnTextChangedCallback(OnTextChangedCallback callabck) {
        this.callabck = callabck;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start,
                                 int lengthBefore, int lengthAfter) {
        if (!isInEditMode() && handler != null) {
            msgCount++;
            handler.sendEmptyMessageDelayed(MSGCODE, delayMillis);
        }

        if (callabck != null) {
            callabck.onTextChangedWithoutDelay(text.toString());
        }
    }

    public interface OnTextChangedCallback {
        void onTextChanged(String value);

        void onTextChangedWithoutDelay(String value);
    }

    static class MyHandler extends Handler {
        WeakReference<Object> refObj;

        MyHandler(Object obj) {
            this.refObj = new WeakReference<Object>(obj);
        }
    }


}
