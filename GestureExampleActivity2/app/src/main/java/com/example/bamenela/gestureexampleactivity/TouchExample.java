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
import android.util.SparseArray;
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
    private ArrayList<String> listImageMemory = Singleton.getInstance().listImageMemory;
    private int singletonSize = 0;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private SparseArray<BitmapDrawable> imageList = new SparseArray<>();
    private Pointer[] mPointers = new Pointer[MAX_POINTERS];
    private int index = 0;
    private float newset = 0, set = 0;

    class Pointer {
        float x = 0;
        float y = 0;
        int index = -1;
        int id = -1;
    }

    public TouchExample(Context context) {
        super(context);
        for (int i = 0; i < MAX_POINTERS; i++) {
            mPointers[i] = new Pointer();
        }

        singletonSize = listImageMemory.size();

        mGestureDetector = new GestureDetector(context, new ZoomGesture());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Cette fonction permet d'afficher l'image en fonction de ce qui est souhaité en image par ligne (mac 3 pour eviter le crash a l'ouverture)
     *
     * @param index
     * @param x
     * @param y
     * @param xmax
     * @param ymax
     * @param canvas
     * https://developer.android.com/topic/performance/graphics/load-bitmap#java
     */
    public void dispPicture(int index, int x, int y, int xmax, int ymax, Canvas canvas) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(listImageMemory.get(index), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, xmax - x, ymax - y);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(listImageMemory.get(index), options);
//        imageList.put(index, new BitmapDrawable(bitmap));
//        BitmapDrawable picture = imageList.get(index);
        BitmapDrawable picture = new BitmapDrawable(getContext().getResources(),bitmap);
        picture.setBounds(x, y, xmax, ymax);
        picture.draw(canvas);
    }

    /**
     * On prend en compte le nombre de colonne (image par ligne) pour faire nos calculs de bordure d'image
     *
     * @param currentNbColumn
     *
     */
    public void findPosition(int currentNbColumn) {
        int posx = 0, posy = 0, posxmax = 0, posymax = 0;
        int nbligne = height / (width / currentNbColumn);
        int k = index;
        for (int j = 0; j < nbligne; j++) {
            for (int i = 0; i < currentNbColumn; i++) {
                posx = i * (width / currentNbColumn);
                posxmax = (i + 1) * (width / currentNbColumn);
                posy = j * (width / currentNbColumn);
                posymax = (j + 1) * (width / currentNbColumn);
                dispPicture(k, posx, posy, posxmax, posymax, canvas);
                if (k < singletonSize) {
                    k++;
                }
            }
        }
    }

    /**
     * Fonction principale permettant de faire appel aux fonctions
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        if(index<0)
            index=0;
        if(index>singletonSize)
            index=singletonSize;
        findPosition(currentNbColumn);
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
                for (int id = 0; id < MAX_POINTERS; id++)
                    mPointers[id].index = -1;

                // Now fill in the current pointers
                for (int i = 0; i < pointerCount; i++) {
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
                for (int i = 0; i < pointerCount; i++) {
                    int id = event.getPointerId(i);
                    mPointers[id].index = -1;
                }
                invalidate();
                break;
        }
        return true;
    }

    public class ZoomGesture extends GestureDetector.SimpleOnGestureListener {
//        private boolean normal = true;
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            mScale = normal ? 3f : 1f;
//            mPaint.setTextSize(mScale * mFontSize);
//            normal = !normal;
//            invalidate();
//            bitmapResolution = getBitMapResolution((normal ? 7 : 3), "dpi");
//            return true;
//        }

        @Override
        /**
         * Ici, on utilise la fonction onScroll afin de savoir si le doigt va vers le haut ou le bas
         */
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int Scrollinglimit = 1000;
            set = newset + distanceY;
            if (set > Scrollinglimit) {
                newset = distanceY;
                index = index + currentNbColumn;
                set = 0;
            }
            if (set < -Scrollinglimit) {
                newset = distanceY;
                index = index - currentNbColumn;
                set = 0;
            }
            newset = distanceY;
            return true;
        }
    }

    /**
     * Le scale gesture permet de changer le nombre d'affichage
     */
    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            currentNbColumn =(int)( currentNbColumn/  (mScale));
            if(currentNbColumn<1)
            {
                currentNbColumn=1;
            }else     if(currentNbColumn>7)
            {
                currentNbColumn=7;
            }
            invalidate();
            return true;
        }
    }

    public int getBitMapResolution(int n, String unit) {

        if (n < 1) {
            n = 1;
        } else {
            if (n > 7) {
                n = 7;
            }
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthBitmap = metrics.widthPixels / n;
//        Log.d("ApplicationTagName", "T Display width in px is " + metrics);
//        Log.d("ApplicationTagName", "T Bitmap DPI width " + widthBitmap);
        if (unit.equals("dpi")) {
            return Math.round(widthBitmap / metrics.scaledDensity);
        } else {
            return widthBitmap;
        }

    }
}