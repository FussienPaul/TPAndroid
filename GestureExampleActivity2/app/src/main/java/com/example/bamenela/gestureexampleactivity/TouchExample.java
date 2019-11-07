package com.example.bamenela.gestureexampleactivity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.LogRecord;


public class TouchExample extends View {
    private static final int MAX_POINTERS = 5;
    private Canvas canvas;
    final BitmapFactory.Options options = new BitmapFactory.Options();
    private float mScale = 1f;
    private int currentNbColumn = 3;
    private int height = getResources().getDisplayMetrics().heightPixels;
    private int width = getResources().getDisplayMetrics().widthPixels;
    private int bitmapResolution = getBitMapResolution(currentNbColumn, "dpi");
    //private int maxBitmapRow = getBitmapRow(bitmapResolution);
    private GestureDetector mGestureDetector;
    int load=0;
    private Handler objectHandler;
    private ScaleGestureDetector mScaleGestureDetector;
    private ArrayList<String> images;
    private HashMap<Integer, BitmapDrawable> imageList = new HashMap<Integer, BitmapDrawable>();
    private Pointer[] mPointers = new Pointer[MAX_POINTERS];
    private Paint mPaint;
    private float mFontSize;
    private int index=0;
    private float touchPositionY=0,mouvement=0;

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
        //loadWithThreads(Singleton.getInstance().listImageMemory.size());

//        Toast.makeText(getContext(), "Row: "+maxBitmapRow, Toast.LENGTH_SHORT).show();

    }
/*
    public void loadWithThreads(final int Maxindex){
        load = 0;
        final Handler handlerObject = new Handler();
        handlerObject.post(new Runnable() {
            @Override
            public void run() {
                if (load < Singleton.getInstance().listImageMemory.size()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(Singleton.getInstance().listImageMemory.get(load), options);
                    Log.d("LOADTREAD", "Thread PRE loaded load ID------------------- :"+load);
                    imageList.put(load, new BitmapDrawable(getResources(), bitmap));
                    load++;
                    if(load<Maxindex)
                    {
                        handlerObject.post(this);
                    }
                }
            }
        });
    }
*/
    public void dispPicture(int index, int x, int y,int xmax, int ymax, Canvas canvas)
    {
            Bitmap bitmap = BitmapFactory.decodeFile(Singleton.getInstance().listImageMemory.get(index), options);
            imageList.put(index,new BitmapDrawable(getResources(), bitmap));
        BitmapDrawable image = imageList.get(index);

        image.setBounds(x,y,xmax,ymax);
        Log.d("LOADTREAD", "Thread PRE loaded ID------------------- :"+index);
        image.draw(canvas);
    }

    public void findPosition(int currentNbColumn){
        int posx=0, posy=0, posxmax=0, posymax=0;
        int nbligne= height/(width/currentNbColumn);
        for(int j=0;j<nbligne;j++){
            for(int i=0;i<currentNbColumn;i++) {
                posx = i * (width / currentNbColumn);
                posxmax = (i+1) * (width / currentNbColumn);
                posy = j * (width / currentNbColumn);
                posymax = (j+1) * (width / currentNbColumn);
                dispPicture(index,posx,posy,posxmax,posymax,canvas);
                if(index<Singleton.getInstance().listImageMemory.size())
                {
                    index++;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas=canvas;
        //Bitmap bitmap = BitmapFactory.decodeFile(Singleton.getInstance().listImageMemory.get(0), options);
        //imageList.put(0,new BitmapDrawable(getResources(), bitmap));
        //BitmapDrawable image = imageList.get(0);
        //image.draw(canvas);
        findPosition(currentNbColumn);
        //loadWithThreads(Singleton.getInstance().listImageMemory.size());
        //canvas.drawBitmap(image.getBitmap(), 0, 0, mPaint);
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
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
            mouvement = touchPositionY + distanceY;
            if(mouvement>400){
                Log.d("MOVE", "MOUVE DOWN ");
                touchPositionY  = distanceY;
                index = index+currentNbColumn;
                mouvement =0;
            }
            if(mouvement<-400){
                Log.d("MOVE", "MOUVE UP ");
                touchPositionY  = distanceY;

                index = index-currentNbColumn;
                mouvement =0;
            }
            Log.d("MOVE", "Y : "+distanceY +" mouv " +mouvement+"index = "+index);
            touchPositionY  = distanceY;
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