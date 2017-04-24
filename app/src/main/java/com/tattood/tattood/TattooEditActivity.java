package com.tattood.tattood;

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
import android.widget.CompoundButton;
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
    Tattoo tattoo;
    CompoundButton.OnCheckedChangeListener switch_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tattoo_edit);
        Bundle extras = getIntent().getExtras();
        final String tattoo_id = extras.getString("tattoo_id");
        final String owner_id = extras.getString("owner_id");
        switch_listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tattoo.is_private = isChecked;
                User.getInstance().changeTattooVisibility(tattoo);
            }
        };
        Switch sw = (Switch) findViewById(R.id.switch_visibility);
        sw.setOnCheckedChangeListener(switch_listener);
        tags = new ArrayList<>();
        tattoo = new Tattoo(tattoo_id, owner_id);
        Server.getTattooImage(this, tattoo_id, 1,
                new Server.ResponseCallback() {
                    @Override
                    public void run(String id, int i) {
                        try {
                            String name = id + ".png";
                            FileInputStream stream = openFileInput(name);
                            Bitmap img = BitmapFactory.decodeStream(stream);
                            ImageView tattoo = (ImageView)findViewById(R.id.tattoo_image);
                            tattoo.setImageBitmap(img);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }});
        Server.getTattooData(this, tattoo_id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean is_private = response.getString("private").equals("1");
                            Switch sw = (Switch) findViewById(R.id.switch_visibility);
                            sw.setOnCheckedChangeListener(null);
                            sw.setChecked(is_private);
                            sw.setOnCheckedChangeListener(switch_listener);
                            JSONArray tattooJSON = response.getJSONArray("tags");
                            for (int i = 0; i < tattooJSON.length(); i++)
                                tags.add(tattooJSON.getString(i));
                            tattoo.is_private = is_private;
                            tattoo.tags = tags;
                            ListView tag_list = (ListView) findViewById(R.id.tag_list_edit);
                            adapter = new TagItemAdapter(TattooEditActivity.this, tags, tattoo);
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
                        if (adapter.list == null)
                            adapter.list = new ArrayList<>();
                        adapter.list.add(input.getText().toString());
                        adapter.notifyDataSetChanged();
                        tattoo.tags = tags;
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
        Button upload_button = (Button) findViewById(R.id.edit_finish);
        upload_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Server.updateTattoo(TattooEditActivity.this, tattoo, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("EDIT", "NOTIFIED");
                        Log.d("POST-EDIT2", String.valueOf(((TattooRecyclerViewAdapter)User.getInstance().private_view.getAdapter()).mValues.size()));
                        Log.d("POST-EDIT2", String.valueOf(((TattooRecyclerViewAdapter)User.getInstance().public_view.getAdapter()).mValues.size()));
                        User.getInstance().private_view.getAdapter().notifyDataSetChanged();
                        User.getInstance().public_view.getAdapter().notifyDataSetChanged();
                        }
                });
                finish();
            }
        });
    }
}
