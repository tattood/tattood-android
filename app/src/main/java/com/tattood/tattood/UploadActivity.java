package com.tattood.tattood;

import android.content.DialogInterface;
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
import android.widget.Switch;

import com.android.volley.Response;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity {

    Tattoo tattoo;
    Uri path = null;
    TagView tagGroup;
    Bitmap bitmap = null;
    DrawView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Bundle extras = getIntent().getExtras();
        tattoo = new Tattoo(null, null);
        path = Uri.parse("file://" + extras.getString("path"));
        Log.d("Upload", String.valueOf(path));
        tagGroup = (TagView) findViewById(R.id.tag_list_edit);
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(TagView tagView, Tag tag, int index) {
                tattoo.tags.remove(index);
            }
        });
        image = (DrawView) findViewById(R.id.tattoo_image);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
            image.setImage(bitmap);
            image.setDrawable(false);
            Log.d("IMAGE", String.valueOf(path));
            tattoo.image = bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        String tag = input.getText().toString();
                        tattoo.tags.add(tag);
                        Tag ttag = new Tag(tag);
                        ttag.isDeletable = true;
                        tagGroup.addTag(ttag);
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
                extract();
                Server.uploadImage(UploadActivity.this, path, tattoo,
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

    public void extract() {
        ArrayList<float[]> x = new ArrayList<>();
        ArrayList<float[]> y = new ArrayList<>();
        float wscale = 1;
        float hscale = 1;
        if(bitmap != null) wscale = image.getWidth() / bitmap.getWidth();
        if(bitmap != null) hscale = image.getHeight() / bitmap.getHeight();
        for (int i = 0; i < image.all_points.size(); i++) {
            ArrayList<float[]> points = image.all_points.get(i);
            float[] x_points = new float[points.size()];
            float[] y_points = new float[points.size()];
            for (int j = 0; j < points.size(); j++) {
                x_points[j] = points.get(j)[0] / wscale;
                y_points[j] = points.get(j)[1] / hscale;
            }
            x.add(x_points);
            y.add(y_points);
        }
        image.all_points.clear();
        Server.extractTags(UploadActivity.this, path, x, y,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("data");
                            tagGroup.removeAll();
                            tattoo.tags.clear();
                            for(int i = 0; i < arr.length(); i++) {
                                Tag ttag = new Tag(arr.getString(i));
                                ttag.isDeletable = true;
                                tagGroup.addTag(ttag);
                                tattoo.tags.add(arr.getString(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
