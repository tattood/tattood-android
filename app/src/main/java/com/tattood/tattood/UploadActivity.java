package com.tattood.tattood;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

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
    ArrayList<float[]> x = new ArrayList<>();
    ArrayList<float[]> y = new ArrayList<>();
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
                tagView.remove(index);
            }
        });
        image = (DrawView) findViewById(R.id.tattoo_image);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
            image.setColorFilter(Color.parseColor("#000000"));
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
                String label = tattoo.is_private ? "Private" : "Public";
                TextView private_label = (TextView) findViewById(R.id.label_visibility);
                private_label.setText(label);
            }
        });
        TextView tv_tag = (TextView) findViewById(R.id.new_tag);
        tv_tag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String tag = String.valueOf(textView.getText());
                    if (tag.isEmpty()) {
                        textView.setError("Tag cannot be empty");
                    } else if (tag_exists(tag)) {
                        textView.setError("Tags must be unique");
                    } else {
                        TattooTag ttag = new TattooTag(tag, true);
                        tattoo.tags.add(ttag);
                        tagGroup.addTag(ttag);
                        textView.setText("");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });
        final ImageView crop_button = (ImageView) findViewById(R.id.crop_button);
        final ImageView crop_revert = (ImageView) findViewById(R.id.crop_revert);
        crop_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image. clear();
                crop_revert.setVisibility(View.INVISIBLE);
            }
        });
        crop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setDrawable(true);
                crop_button.setVisibility(View.INVISIBLE);
            }
        });
        image.setListener(new DrawView.Listener() {
            @Override
            public void onFinish() {
                image.setDrawable(false);
//                [TODO] UnComment below
//                extract();
                crop_revert.setVisibility(View.VISIBLE);
                crop_button.setVisibility(View.VISIBLE);
            }
        });
        final ImageView upload_button = (ImageView) findViewById(R.id.edit_finish);
        upload_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Server.uploadImage(UploadActivity.this, path, tattoo, x, y,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    tattoo.tattoo_id = response.getString("id");
                                    if (tattoo.is_private)
                                        User.getInstance().addPrivate(tattoo);
                                    else
                                        User.getInstance().addPublic(tattoo);
                                    User.getInstance().private_view.getAdapter().notifyDataSetChanged();
                                    User.getInstance().public_view.getAdapter().notifyDataSetChanged();
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        });
//        [TODO] Uncomment below
//        extract();
    }

    public void extract() {
        x = new ArrayList<>();
        y = new ArrayList<>();
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
                                String tag = arr.getString(i);
                                TattooTag ttag = new TattooTag(tag, true);
                                tagGroup.addTag(ttag);
                                tattoo.tags.add(ttag);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public boolean tag_exists(String t) {
        for (TattooTag tt: tattoo.tags) {
            if (t.equals(tt.text))
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to cancel Upload?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
