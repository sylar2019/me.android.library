package me.android.library.utils.crop.cropoverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import me.android.library.common.utils.DisplayUtils;
import me.android.library.common.utils.ViewUtils;
import me.android.library.utils.crop.cropoverlay.edge.Edge;
import me.android.library.utils.crop.cropoverlay.utils.PaintUtil;
import me.android.library.utils.crop.photoview.PhotoViewAttacher;


/**
 * @author GT
 * Modified/stripped down Code from cropper library : https://github.com/edmodo/cropper
 */
public class CropOverlayView extends View implements PhotoViewAttacher.IGetImageBounds {
    static final float CORNER_RADIUS = 6;

    Context cx;
    Paint mBorderPaint;
    Paint mGuidelinePaint;
    float scale = 1f;

    public CropOverlayView(Context context) {
        super(context);
        this.cx = context;

        init();
    }

    public CropOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.cx = context;

        init();
    }


    public void setScale(int cropWidth, int cropHeight) {
        scale = 1f * cropHeight / cropWidth;
        onInit();
//        invalidate();
    }

    void init() {
        mBorderPaint = PaintUtil.newBorderPaint(cx);
        mGuidelinePaint = PaintUtil.newGuidelinePaint();
        onInit();
    }

    void onInit() {
        if (this.isInEditMode()) {
            int w = cx.getResources().getDisplayMetrics().widthPixels;
            int h = w;
            setEdge(w, h);
        } else {
            ViewUtils.getViewSize(this, new ViewUtils.OnCalcSizeCallback() {
                @Override
                public void onCompleted(ViewUtils.Size size) {
                    setEdge(size.width, size.height);
                }
            });
        }
    }

    void setEdge(int w, int h) {
        int mMarginSide = DisplayUtils.dip2px(cx, 10);
        int cropWidth = w - 2 * mMarginSide;
        int cropHeight = (int) (cropWidth * scale);
        int mMarginTop = (int) ((h - cropHeight) / 2f);

        int edgeT = mMarginTop;
        int edgeB = mMarginTop + cropHeight;
        int edgeL = mMarginSide;
        int edgeR = mMarginSide + cropWidth;

        Edge.TOP.setCoordinate(edgeT);
        Edge.BOTTOM.setCoordinate(edgeB);
        Edge.LEFT.setCoordinate(edgeL);
        Edge.RIGHT.setCoordinate(edgeR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        final float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS, cx.getResources().getDisplayMetrics());

        RectF rectF = new RectF(Edge.LEFT.getCoordinate(),
                Edge.TOP.getCoordinate(),
                Edge.RIGHT.getCoordinate(),
                Edge.BOTTOM.getCoordinate());
        Path clipPath = new Path();
        clipPath.addRoundRect(rectF, radius, radius, Path.Direction.CW);
        canvas.clipPath(clipPath, Region.Op.DIFFERENCE);
        canvas.drawARGB(204, 41, 48, 63);
        canvas.save();
        canvas.restore();
        canvas.drawRoundRect(rectF, radius, radius, mBorderPaint);

//        drawRuleOfThirdsGuidelines(canvas);
    }

    public Rect getImageBounds() {
        return new Rect((int) Edge.LEFT.getCoordinate(), (int) Edge.TOP.getCoordinate(), (int) Edge.RIGHT.getCoordinate(), (int) Edge.BOTTOM.getCoordinate());
    }


    private void drawRuleOfThirdsGuidelines(Canvas canvas) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        // Draw vertical guidelines.
        final float oneThirdCropWidth = Edge.getWidth() / 3;

        final float x1 = left + oneThirdCropWidth;
        canvas.drawLine(x1, top, x1, bottom, mGuidelinePaint);
        final float x2 = right - oneThirdCropWidth;
        canvas.drawLine(x2, top, x2, bottom, mGuidelinePaint);

        // Draw horizontal guidelines.
        final float oneThirdCropHeight = Edge.getHeight() / 3;

        final float y1 = top + oneThirdCropHeight;
        canvas.drawLine(left, y1, right, y1, mGuidelinePaint);
        final float y2 = bottom - oneThirdCropHeight;
        canvas.drawLine(left, y2, right, y2, mGuidelinePaint);
    }

}
