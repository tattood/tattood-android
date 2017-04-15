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

import java.io.IOException;
import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity {

    TagItemAdapter adapter;
    ArrayList<String> tags;
    Tattoo tattoo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Bundle extras = getIntent().getExtras();
        tattoo = new Tattoo();
        final String token = extras.getString("token");
        final Uri path = Uri.parse("file://" + extras.getString("path"));
        Log.d("Upload", String.valueOf(path));
        ListView tag_list = (ListView) findViewById(R.id.tag_list_edit);
        adapter = new TagItemAdapter(this, token, new ArrayList<String>(), tattoo);
        tag_list.setAdapter(adapter);
        ImageView image = (ImageView) findViewById(R.id.tattoo_image_upload);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
            image.setImageBitmap(bitmap);
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
        Button upload_button = (Button) findViewById(R.id.upload_button);
        upload_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Server.uploadImage(UploadActivity.this, path, token, tattoo,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("UPLOAD", "Refresh");
                                finish();
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
//            String returnValue = data.getStringExtra("x_");
            float[] x = data.getFloatArrayExtra("x_points");
            float[] y = data.getFloatArrayExtra("y_points");
            Log.d("Upload", String.valueOf(x.length));
            Log.d("Upload", String.valueOf(y.length));
        }
    }
}
