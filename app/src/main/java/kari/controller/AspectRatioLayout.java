package kari.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by ws-kari on 15-4-20.
 */
public class AspectRatioLayout extends FrameLayout {

    public AspectRatioLayout(Context context) {
        super(context);
        init();
    }

    public AspectRatioLayout(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public void init() {
        setWillNotDraw(false);
    }

    /*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specModeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int specSizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specModeHeight == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    */

    public void setSize(int w, int h) {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("w && h must > 0");
        }
        setVisibility(View.VISIBLE);
        getLayoutParams().width = w;
        getLayoutParams().height = h;
        forceLayout();
    }
}