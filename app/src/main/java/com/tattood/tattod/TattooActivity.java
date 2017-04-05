package com.tattood.tattod;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import static java.security.AccessController.getContext;

public class TattooActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tattoo);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        final String token = "1"; //extras.getString("token");
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
//                            JSONObject obj = response.getJSONObject("data");
//                            boolean priv = Boolean.valueOf(obj.getString("private"));
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
                            for (int i = 0; i < tattooJSON.length(); i++)
                                tags.add(tattooJSON.getString(i));
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
