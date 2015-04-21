package kari.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by ws-kari on 15-4-20.
 */
public class ScreenView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    final static String TAG = "ScreenView";

    private Paint mPaint;
    SurfaceHolder mHolder;
    private int mX;
    private int mY;
    private boolean mRunFlag = true;

    public ScreenView(Context context) {
        super(context);
        init(context);
    }

    public ScreenView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    public void init(Context context) {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void drawCircle(Canvas canvas) {
        Log.d(TAG, "drawCircle");

        if (mX > 0 && mY > 0) {
            canvas.drawCircle((float) mX, (float) mY, 20.0f, mPaint);
        }
    }

    public void notifyEvent(int x, int y) {
        Log.d(TAG, "notifyEvent:x=" + x + ",y=" + y);
        mX = x;
        mY = y;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mRunFlag = false;
    }

    @Override
    public void run() {
        while (mRunFlag) {

            synchronized (mHolder) {
                Canvas canvas = mHolder.lockCanvas(null);
                canvas.drawColor(Color.BLACK);
                mHolder.unlockCanvasAndPost(canvas);
            }

            if (mX > 0) {
                synchronized (mHolder) {
                    Canvas canvas = mHolder.lockCanvas();
                    drawCircle(canvas);
                    mHolder.unlockCanvasAndPost(canvas);
                }
                mX = -1;
            }

            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
