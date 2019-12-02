package me.android.library.ui.ext.popups;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import me.android.library.ui.ext.OkCancelCallback;

/**
 * Created by sylar on 15/8/4.
 */
public class PopupHelper {

    //  ======================================================================================================
    //                                          show
    //  ======================================================================================================


    //  --------------------------------------- PickerPopup ---------------------------------------

    public static PopupWindow showPickerPopup(View parent, View view, OkCancelCallback<PopupWindow> callback) {
        PopupWindow pop = newPickerPopup(view, callback);
        return showPickerPopup(parent, pop);
    }

    public static PopupWindow showPickerPopup(View parent, View view, OkCancelCallback<PopupWindow> callback, Drawable drawable) {
        PopupWindow pop = newPickerPopup(view, callback, drawable);
        return showPickerPopup(parent, pop);
    }

    public static PopupWindow showPickerPopup(View parent, PopupWindow pop) {
        return showAtLocation(parent, pop, Gravity.BOTTOM | Gravity.FILL_HORIZONTAL);
    }

    public static PopupWindow newPickerPopup(View view, OkCancelCallback<PopupWindow> callback) {
        BasePickerPopup pop = new BasePickerPopup(view, callback);
        return pop;
    }

    public static PopupWindow newPickerPopup(View view, OkCancelCallback<PopupWindow> callback, Drawable drawable) {
        BasePickerPopup pop = new BasePickerPopup(view, callback, drawable);
        return pop;
    }


    //  --------------------------------------- showAsDropDown ---------------------------------------


    public static PopupWindow showAsDropDown(View parent, PopupWindow pop) {
        pop.showAsDropDown(parent);
        return pop;
    }

    public static PopupWindow showAsDropDown(View parent, PopupWindow pop, int xoff, int yoff) {
        pop.showAsDropDown(parent, xoff, yoff);
        return pop;
    }

    //  --------------------------------------- showAtLocation ---------------------------------------


    public static PopupWindow showAtLocation(View parent, PopupWindow pop, int gravity) {
        return showAtLocation(parent, pop, gravity, 0, 0);
    }

    public static PopupWindow showAtLocation(View parent, PopupWindow pop, int gravity, int x, int y) {
        pop.showAtLocation(parent, gravity, x, y);
        return pop;
    }

    //  ======================================================================================================
    //                                          Picker PopupWindow
    //  ======================================================================================================


    //---------------------------BasePickerPopupWindow---------------------------

}
