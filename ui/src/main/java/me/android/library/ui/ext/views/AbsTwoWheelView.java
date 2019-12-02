package me.android.library.ui.ext.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import java.util.List;

import me.android.library.ui.R;

/**
 * Created by sylar on 15/7/21.
 */
abstract public class AbsTwoWheelView extends FrameLayout {

    protected WheelView wv1, wv2;
    WheelView.OnSelectListener wv1_Listener = new WheelView.OnSelectListener() {
        @Override
        public void endSelect(int index, Object item) {
            List<?> list = getList2(item);
            wv2.setData(list);
        }

        @Override
        public void selecting(int index, Object item) {
        }
    };

    public AbsTwoWheelView(Context cx) {
        super(cx);
        init(cx, null);
    }

    public AbsTwoWheelView(Context cx, AttributeSet attrs) {
        super(cx, attrs);
        init(cx, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadData();
    }

    void init(Context cx, AttributeSet attrs) {

        LayoutInflater.from(cx).inflate(R.layout.abs_view_two_wheel, this,
                true);

        wv1 = findViewById(R.id.wv1);
        wv2 = findViewById(R.id.wv2);

        wv1.setOnSelectListener(wv1_Listener);
    }

    protected void loadData() {
        List<?> list1 = getList1();
        wv1.setData(list1);
        wv1.setDefault(list1.size() / 2);
    }

    public <T> T getSelectedItem1() {
        return (T) wv1.getSelectedTag();
    }

    public <T> T getSelectedItem2() {
        return (T) wv2.getSelectedTag();
    }


    //--------------------------------------------------------------------------------------------------------------------
    //abstract
    //--------------------------------------------------------------------------------------------------------------------

    abstract protected List<?> getList1();

    abstract protected List<?> getList2(Object item);

    //--------------------------------------------------------------------------------------------------------------------
    //abstract
    //--------------------------------------------------------------------------------------------------------------------


}
