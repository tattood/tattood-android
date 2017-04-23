package com.tattood.tattood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class TattooActivity extends AppCompatActivity {

    int is_liked;
    int like_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tattoo);
        Bundle extras = getIntent().getExtras();
        final String tattoo_id = extras.getString("tid");
        final Tattoo tattoo = new Tattoo(tattoo_id, null);
        Server.getTattooImage(this, tattoo_id, 1,
                new Server.ResponseCallback() {
                    @Override
                    public void run(String id, int i) {
                        try {
                            String name = id + ".png";
                            FileInputStream stream = openFileInput(name);
                            Bitmap img = BitmapFactory.decodeStream(stream);
                            ImageView tattoo_img = (ImageView)findViewById(R.id.tattoo_image);
                            tattoo_img.setImageBitmap(img);
                            tattoo.image = img;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }});
        Server.getTattooData(this, tattoo_id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final String username = response.getString("owner");
                            final String owner_id = response.getString("owner_id");
                            TextView owner = (TextView) findViewById(R.id.owner_name);
                            owner.setText(username);
                            Log.d("Tattoo", username);
                            tattoo.owner_id = owner_id;
                            owner.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent myIntent = new Intent(TattooActivity.this, UserActivity.class);
                                    myIntent.putExtra("username", username);
                                    startActivity(myIntent);
                                }});
                            JSONArray tattooJSON = response.getJSONArray("tags");
                            ArrayList<String> tags = new ArrayList<>();
                            for (int i = 0; i < tattooJSON.length(); i++) {
                                String t = tattooJSON.getString(i);
                                if (!t.isEmpty())
                                    tags.add(t);
                            }
                            tattoo.tags = tags;
                            ListView tag_list = (ListView) findViewById(R.id.tag_list);
                            tag_list.setAdapter(new ArrayAdapter<>(TattooActivity.this, android.R.layout.simple_list_item_1, tags));
                            tag_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent myIntent = new Intent(TattooActivity.this, SearchActivity.class);
                                    String entry = (String) adapterView.getItemAtPosition(i);
                                    Log.d("Clicked-Tag", entry);
                                    myIntent.putExtra("tag", entry);
                                    startActivity(myIntent);
                                }
                            });
                            like_count = response.getInt("like_count");
                            is_liked = response.getInt("is_liked");
                            Log.d("LIKE", String.valueOf(is_liked));
                            refreshLikeButton();
                            final Button like_button = (Button) findViewById(R.id.like_button);
                            like_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Button button = (Button) view;
                                    String text = String.valueOf(button.getText());
                                    Response.Listener<JSONObject> callback = new Response.Listener<JSONObject>(){
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            like_count += is_liked == 1 ? -1 : 1;
                                            is_liked = 1 - is_liked;
                                            User.getInstance().addLike(tattoo);
                                            refreshLikeButton();
                                        }
                                    };
                                    Server.like(TattooActivity.this, tattoo_id, text.equals("Like"), callback);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CAMERA", "Clicked");
                Intent myIntent = new Intent(TattooActivity.this, CameraActivity.class);
                startActivity(myIntent);
            }
        });
    }

    private void refreshLikeButton() {
        final Button like_button = (Button) findViewById(R.id.like_button);
        final TextView like_label = (TextView) findViewById(R.id.like_count);
        if (is_liked == 1) {
            like_button.setText("Unlike");
        } else {
            like_button.setText("Like");
        }
        like_label.setText("Likes:" + like_count);
    }
}
