package com.example.bamenela.gestureexampleactivity;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;


public class TouchExample extends View {
    private static final int MAX_POINTERS = 5;
    private float mScale = 1f;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private Pointer[] mPointers = new Pointer[MAX_POINTERS];
    private Paint mPaint;
    private float mFontSize;

    class Pointer {
        float x = 0;
        float y = 0;
        int index = -1;
        int id = -1;
    }

    public TouchExample(Context context) {
        super(context);
        for (int i = 0; i<MAX_POINTERS; i++) {
            mPointers[i] = new Pointer();
        }

        mFontSize = 16 * getResources().getDisplayMetrics().density;
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(mFontSize);

        mGestureDetector = new GestureDetector(context, new ZoomGesture());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());

        for (int i=1; i<=7; i++){
            Log.d("ApplicationTagName", "T Display width in dpi is " + getBitMapResolution(i, "dpi"));
            Log.d("ApplicationTagName", "T Display width in px is " + getBitMapResolution(i, "px"));
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Pointer p : mPointers) {
            if (p.index != -1) {
                String text = "Index: " + p.index + " ID: " + p.id;
                canvas.drawText(text, p.x, p.y, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);

        int pointerCount = Math.min(event.getPointerCount(), MAX_POINTERS);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                // clear previous pointers
                for (int id = 0; id<MAX_POINTERS; id++)
                    mPointers[id].index = -1;

                // Now fill in the current pointers
                for (int i = 0; i<pointerCount; i++) {
                    int id = event.getPointerId(i);
                    Pointer pointer = mPointers[id];
                    pointer.index = i;
                    pointer.id = id;
                    pointer.x = event.getX(i);
                    pointer.y = event.getY(i);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                for (int i = 0; i<pointerCount; i++) {
                    int id = event.getPointerId(i);
                    mPointers[id].index = -1;
                }
                invalidate();
                break;
        }
        return true;
    }

    public class ZoomGesture extends GestureDetector.SimpleOnGestureListener {
        private boolean normal = true;

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mScale = normal ? 3f : 1f;
            mPaint.setTextSize(mScale*mFontSize);
            normal = !normal;
            invalidate();
            getBitMapResolution((normal ? 7 : 3),"dpi");
            return true;
        }
    }

    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            mPaint.setTextSize(mScale*mFontSize);
            invalidate();
            getBitMapResolution((int)(8-Math.floor(mScale/1f)),"dpi");
//            Toast.makeText(getContext(), "|"+(8-Math.floor(mScale/1f)), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public int getBitMapResolution(int n, String unit){

        if(n<1) {
            n=1;
        } else {
            if (n > 7) {
                n = 7;
            }
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthBitmap = metrics.widthPixels/n;
//        Log.d("ApplicationTagName", "T Display width in px is " + metrics);
//        Log.d("ApplicationTagName", "T Bitmap DPI width " + widthBitmap);
        if(unit == "dpi"){
            return Math.round(widthBitmap/metrics.scaledDensity);
        }else{
            return widthBitmap;
        }

    }
}
