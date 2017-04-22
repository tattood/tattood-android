package com.tattood.tattood;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.IOException;
import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity {

    TagItemAdapter adapter;
    Tattoo tattoo;
    Uri path = null;
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tattoo_edit);
//        setContentView(R.layout.activity_upload);
        Bundle extras = getIntent().getExtras();
        tattoo = new Tattoo();
        token = extras.getString("token");
        path = Uri.parse("file://" + extras.getString("path"));
        Log.d("Upload", String.valueOf(path));
        ListView tag_list = (ListView) findViewById(R.id.tag_list_edit);
        adapter = new TagItemAdapter(this, token, new ArrayList<String>(), tattoo);
        tag_list.setAdapter(adapter);
        ImageView image = (ImageView) findViewById(R.id.tattoo_image);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
            image.setImageBitmap(bitmap);
            tattoo.image = bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(UploadActivity.this, ExtractImage.class);
                myIntent.putExtra("path", path.toString());
                startActivityForResult(myIntent, 1);
            }
        });
        final Switch sw = (Switch) findViewById(R.id.switch_visibility);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                tattoo.is_private = b;
            }
        });
        Button add_tag = (Button) findViewById(R.id.add_tag);
        add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                builder.setTitle("New Tag");
                final EditText input = new EditText(UploadActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.list.add(input.getText().toString());
                        tattoo.tags = adapter.list;
                        adapter.notifyDataSetChanged();
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
                Server.uploadImage(UploadActivity.this, path, token, tattoo,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    tattoo.tattoo_id = response.getString("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Upload1", String.valueOf(resultCode));
        Log.d("Upload2", String.valueOf(Activity.RESULT_OK));
        ArrayList<float[]> x = new ArrayList<>();
        ArrayList<float[]> y = new ArrayList<>();
        if (resultCode == Activity.RESULT_OK) {
            Log.d("Upload3", "HERE");
            int size = data.getIntExtra("point_size", 0);
            for (int i = 0; i < size; i++) {
                x.add(data.getFloatArrayExtra("x_points"+i));
                y.add(data.getFloatArrayExtra("y_points"+i));
            }
        }
        Server.extractTags(UploadActivity.this, path, token, x, y,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("data");
                            adapter.list.clear();
                            for(int i = 0; i < arr.length(); i++) {
                                adapter.list.add(arr.getString(i));
                            }
                            tattoo.tags = adapter.list;
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
