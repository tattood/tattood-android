package com.tattood.tattood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ExtractImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract);
//        final DrawView draw = (DrawView) findViewById(R.id.extract_img_view);
//        Bitmap b;
//        String path = getIntent().getExtras().getString("path");
//        try {
//            b = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://" + path));
//            b.setHasAlpha(true);
//            draw.setImage(b);
//        } catch (IOException e) {
//            e.printStackTrace();
//            b = null;
//        }
//        final Bitmap bitmap = b;
//        Button button = (Button) findViewById(R.id.extract_button);
//        button.setText("OK");
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                float wscale = 1;
//                float hscale = 1;
//                if(bitmap != null) wscale = draw.getWidth() / bitmap.getWidth();
//                if(bitmap != null) hscale = draw.getHeight() / bitmap.getHeight();
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("point_size", all_points.size());
//                for (int i = 0; i < all_points.size(); i++) {
//                    ArrayList<float[]> points = all_points.get(i);
//                    float[] x_points = new float[points.size()];
//                    float[] y_points = new float[points.size()];
//                    for (int j = 0; j < points.size(); j++) {
//                        x_points[j] = points.get(j)[0] / wscale;
//                        y_points[j] = points.get(j)[1] / hscale;
//                    }
//                    Log.d("POINT", String.valueOf(x_points[0]));
//                    resultIntent.putExtra("x_points"+i, x_points);
//                    resultIntent.putExtra("y_points"+i, y_points);
//                }
//                all_points.clear();
//                setResult(Activity.RESULT_OK, resultIntent);
//                finish();
//            }
//        });
    }
}
