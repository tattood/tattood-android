package com.tattood.tattood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;


public class ExtractImage extends AppCompatActivity {

    DrawView dview;
    private ArrayList<ArrayList<float[]>> all_points;
    private ArrayList<float[]> points;

    public class DrawView extends View {
        Paint paint = new Paint();

        public DrawView(Context context, String path){
            super(context);
            paint.setColor(Color.GRAY);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(25);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse("file://" + path));
                bitmap.setHasAlpha(true);
                Drawable d = new BitmapDrawable(getResources(), bitmap);
                setBackground(d);
            } catch (IOException e) {
                e.printStackTrace();
            }
            paint.setStrokeCap(Paint.Cap.ROUND);
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

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            float[] point = new float[2];
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    points = new ArrayList<>();
                    point[0] = e.getX();
                    point[1] = e.getY();
                    points.add(point);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    point[0] = e.getX();
                    point[1] = e.getY();
                    points.add(point);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    all_points.add(points);
                    invalidate();
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String path = getIntent().getExtras().getString("path");
        dview = new DrawView(this, path);
        setContentView(dview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.extract_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.submit) {
            Intent resultIntent = new Intent();
            float[] x_points = new float[points.size()];
            float[] y_points = new float[points.size()];
            for (int i = 0; i < points.size(); i++) {
                x_points[i] = points.get(i)[0];
                y_points[i] = points.get(i)[1];
            }
            resultIntent.putExtra("x_points", x_points);
            resultIntent.putExtra("y_points", y_points);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
        return true;
    }
}
