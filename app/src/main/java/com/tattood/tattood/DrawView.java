package com.tattood.tattood;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by eksi on 22/04/17.
 */

public class DrawView extends android.support.v7.widget.AppCompatImageView {
    public Paint paint = new Paint();
    public ArrayList<ArrayList<float[]>> all_points;
    private ArrayList<float[]> points;
    public int wscale = 1;
    public int hscale = 1;
    private int imw = -1, imh = -1;
    private boolean drawable = true;
    private Bitmap bitmap;
    boolean cleared = false;
    Listener listener;
    Context context;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        all_points = new ArrayList<>();
        paint.setColor(Color.GRAY);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(25);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawColor(Color.TRANSPARENT);
        for(int j= 0; j<all_points.size(); j++){
            for(int i=1; i<all_points.get(j).size(); i++){
                canvas.drawLine(all_points.get(j).get(i-1)[0],
                        all_points.get(j).get(i-1)[1],
                        all_points.get(j).get(i)[0],
                        all_points.get(j).get(i)[1], paint);
            }
        }
        if (points != null) {
            for(int i=1; i<points.size(); i++){
                canvas.drawLine(points.get(i-1)[0], points.get(i-1)[1], points.get(i)[0], points.get(i)[1], paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (drawable) {
            getParent().requestDisallowInterceptTouchEvent(true);
            float[] point = new float[2];
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    points = new ArrayList<>();
                case MotionEvent.ACTION_MOVE:
                    point[0] = e.getX();
                    point[1] = e.getY();
                    points.add(point);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    all_points.add(points);
                    invalidate();
                    listener.onFinish();
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d("LAYOUT", String.valueOf(getHeight())); //height is ready
        if (imw > 0)
            wscale = getWidth() / imw;
        if (imh > 0)
            hscale = getHeight() / imh;
    }

    public void setImage(Bitmap b) {
        bitmap = b;
        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        setBackground(d);
        Log.d("setImage", String.valueOf(bitmap.getWidth()));
        imw = bitmap.getWidth();
        imh = bitmap.getHeight();
    }

    public void setDrawable(boolean d) {
        drawable = d;
    }

    public boolean isDrawable() { return drawable; }

    public void setListener(Listener l) {
        listener = l;
    }

    public void clear() {
        cleared = true;
        all_points.clear();
        points.clear();
        paint.reset();
        invalidate();
    }

    public abstract static class Listener {
        abstract public void onFinish();
    }
}
