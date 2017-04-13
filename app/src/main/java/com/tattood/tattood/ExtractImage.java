package com.tattood.tattood;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class ExtractImage extends AppCompatActivity {

    public class DrawView extends View {

        Paint paint = new Paint();
        private ArrayList<ArrayList<float[]>> all_points;
        private ArrayList<float[]> points;

        public DrawView(Context context, String path){
            super(context);
            paint.setColor(Color.GRAY);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(50);
            setBackground(Drawable.createFromPath(path));
            paint.setStrokeCap(Paint.Cap.ROUND);
            //paint.setShadowLayer(30, 0, 0, Color.RED);
            all_points = new ArrayList<>();
        }

        @Override
        public void onDraw(Canvas canvas){
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

        public void setPoints(ArrayList<float[]> points){
            this.points = points;
        }


        //private ArrayList<float[]> xy = new ArrayList<float[]>();

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.


            Time time = new Time();

            float x_down, y_down, x_up, y_up;
            float[] point = new float[2];
            switch (e.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    points = new ArrayList<float[]>();
                    x_down = e.getX();
                    y_down = e.getY();
                    point[0] = x_down;
                    point[1] = y_down;
                    points.add(point);
                    invalidate();
                    break;

                case MotionEvent.ACTION_MOVE:

                    x_up = e.getX();
                    y_up = e.getY();

                    point[0] = x_up;
                    point[1] = y_up;
                    points.add(point);
                    invalidate();

                    //Log.d("time", time.toString());
                    break;


                case MotionEvent.ACTION_UP:
                    all_points.add(points);
                    invalidate();
                    break;
                default:
                    break;


            }
//        for(int i=0; i<xy.size(); i++) {
//            String str = "";
//            str = xy.get(i)[0] + " " + xy.get(i)[1];
//            Log.d("xy", str);
//        }
            return true;
        }
    }

    DrawView dview;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_extract);
        String path = getIntent().getExtras().getString("path");
        dview = new DrawView(this, path);
        dview.setBackgroundColor(Color.argb(255, 0, 0, 0));
        setContentView(dview);
//        Bitmap myBitmap = BitmapFactory.decodeFile(path);
    }

}
