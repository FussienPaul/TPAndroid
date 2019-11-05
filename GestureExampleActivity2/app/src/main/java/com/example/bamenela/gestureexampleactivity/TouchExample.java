package com.example.bamenela.gestureexampleactivity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class TouchExample extends View {
    private static final int MAX_POINTERS = 5;
    final BitmapFactory.Options options = new BitmapFactory.Options();
    private float mScale = 1f;
    private int currentNbColumn = 7;
    private int bitmapResolution = getBitMapResolution(currentNbColumn, "dpi");
    //private int maxBitmapRow = getBitmapRow(bitmapResolution);
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private ArrayList<String> images;
    private HashMap<Integer, BitmapDrawable> imageList = new HashMap<Integer, BitmapDrawable>();
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
        mPaint.setColor(Color.BLUE);
        mPaint.setTextSize(mFontSize);

        mGestureDetector = new GestureDetector(context, new ZoomGesture());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());

//        Toast.makeText(getContext(), "Row: "+maxBitmapRow, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = BitmapFactory.decodeFile(Singleton.getInstance().listImageMemory.get(0), options);
        imageList.put(0,new BitmapDrawable(getResources(), bitmap));
        BitmapDrawable image = imageList.get(0);
        image.draw(canvas);


        canvas.drawBitmap(image.getBitmap(), 0, 0, mPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
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
            bitmapResolution = getBitMapResolution((normal ? 7 : 3),"dpi");
            //maxBitmapRow = getBitmapRow(bitmapResolution);
            return true;
        }
    }

    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            mPaint.setTextSize(mScale*mFontSize);
            invalidate();
            bitmapResolution = getBitMapResolution((int)(8-Math.floor(mScale/1f)),"dpi");
            //maxBitmapRow = getBitmapRow(bitmapResolution);
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
        if(unit.equals("dpi")){
            return Math.round(widthBitmap/metrics.scaledDensity);
        }else{
            return widthBitmap;
        }

    }
}
