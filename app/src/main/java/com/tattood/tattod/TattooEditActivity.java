package com.tattood.tattod;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class TattooEditActivity extends AppCompatActivity {

    TagItemAdapter adapter;
    ArrayList<String> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tattoo_edit);
        Bundle extras = getIntent().getExtras();
        final String token = extras.getString("token");
        final String tattoo_id = extras.getString("tattoo_id");
        tags = new ArrayList<>();
        Server.getTattooImage(this, tattoo_id, 1, token,
                new Server.ResponseCallback() {
                    @Override
                    public void run(String id, int i) {
                        try {
                            String name = id + ".jpg";
                            FileInputStream stream = openFileInput(name);
                            Bitmap img = BitmapFactory.decodeStream(stream);
                            ImageView tattoo = (ImageView)findViewById(R.id.tattoo_image_edit);
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
                            boolean priv = Boolean.valueOf(response.getString("private"));
                            Switch sw = (Switch) findViewById(R.id.switch_public);
                            sw.setChecked(!priv);
                            JSONArray tattooJSON = response.getJSONArray("tags");
                            Log.d("TAGS", String.valueOf(tattooJSON));
                            for (int i = 0; i < tattooJSON.length(); i++)
                                tags.add(tattooJSON.getString(i));
                            Log.d("TAGS", ""+tags.size());
                            ListView tag_list = (ListView) findViewById(R.id.tag_list_edit);
                            adapter = new TagItemAdapter(tags, TattooEditActivity.this);
                            tag_list.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Button add_tag = (Button) findViewById(R.id.add_tag);
        add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TattooEditActivity.this);
                builder.setTitle("New Tag");
                final EditText input = new EditText(TattooEditActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tags.add(input.getText().toString());
                        adapter.notifyDataSetChanged();
//                        Server.add_new_tag();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }
}
