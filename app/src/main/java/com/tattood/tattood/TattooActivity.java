package com.tattood.tattood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tattoo);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        final String token = extras.getString("token");
        final String tattoo_id = extras.getString("tid");
        Server.getTattooImage(this, tattoo_id, 1, token,
                new Server.ResponseCallback() {
                    @Override
                    public void run(String id, int i) {
                        try {
                            String name = id + ".jpg";
                            FileInputStream stream = openFileInput(name);
                            Bitmap img = BitmapFactory.decodeStream(stream);
                            ImageView tattoo = (ImageView)findViewById(R.id.tattoo_image);
                            tattoo.setImageBitmap(img);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }});
        Server.getTattooData(this, tattoo_id, token,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final String username = response.getString("owner");
                            TextView owner = (TextView) findViewById(R.id.owner_name);
                            owner.setText(username);
                            Log.d("Tattoo", username);
                            owner.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent myIntent = new Intent(TattooActivity.this, UserActivity.class);
                                    myIntent.putExtra("token", token);
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
                            int like_count = response.getInt("like_count");
                            final TextView like_label = (TextView) findViewById(R.id.like_count);
                            like_label.setText("Likes:" + like_count);
                            int is_liked = response.getInt("is_liked");
                            final Button like_button = (Button) findViewById(R.id.like_button);
                            if (is_liked == 1) {
                                like_button.setText("Unlike");
                            }
                            like_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Button button = (Button) view;
                                    String text = String.valueOf(button.getText());
                                    Response.Listener<JSONObject> callback = new Response.Listener<JSONObject>(){
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            String test = String.valueOf(like_button.getText());
                                            String label = String.valueOf(like_label.getText());
                                            int like_count = Integer.parseInt(label.substring(6));
                                            Log.d("Like", test);
                                            if (test.equals("Like")) {
                                                like_button.setText("Unlike");
                                                like_count++;
                                            }
                                            else {
                                                like_button.setText("Like");
                                                like_count--;
                                            }
                                            like_label.setText("Likes:" + like_count);
                                        }
                                    };
                                    Server.like(TattooActivity.this, token, tattoo_id, text.equals("Like") ? 1 : 0, callback);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
