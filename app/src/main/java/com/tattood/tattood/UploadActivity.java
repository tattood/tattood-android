package com.tattood.tattood;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.android.volley.Response;

import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity {

    TagItemAdapter adapter;
    ArrayList<String> tags;
    Tattoo tattoo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        tattoo = new Tattoo();
        final String token = extras.getString("token");
        final Uri path = Uri.parse(extras.getString("path"));
        adapter = new TagItemAdapter(this, token, new ArrayList<String>(), tattoo);
        ImageView image = (ImageView) findViewById(R.id.tattoo_image_upload);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(UploadActivity.this, ExtractImage.class);
//                ImageView img = (ImageView) view;
                myIntent.putExtra("path", path);
                startActivity(myIntent);
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
                        tattoo.tags = tags;
                        adapter.list.add(input.getText().toString());
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
                            }
                        });
            }
        });
    }

}
