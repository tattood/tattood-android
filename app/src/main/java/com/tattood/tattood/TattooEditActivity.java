package com.tattood.tattood;

import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.Switch;

import com.android.volley.Response;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class TattooEditActivity extends AppCompatActivity {

    ArrayList<String> tags;
    Tattoo tattoo;
    CompoundButton.OnCheckedChangeListener switch_listener;
    TagView tagGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tattoo_edit);
        Bundle extras = getIntent().getExtras();
        final String tattoo_id = extras.getString("tattoo_id");
        switch_listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tattoo.is_private = isChecked;
                User.getInstance().changeTattooVisibility(tattoo);
            }
        };
        tags = new ArrayList<>();
        tattoo = User.getInstance().getTattoo(tattoo_id);
        tagGroup = (TagView) findViewById(R.id.tag_list_edit);
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(TagView tagView, Tag tag, int index) {
                tagView.remove(index);
                tattoo.tags.remove(index);
            }
        });
        Server.getTattooImage(this, tattoo,
                new Server.ResponseCallback() {
                    @Override
                    public void run() {
                        ImageView tattoo_img = (ImageView)findViewById(R.id.tattoo_image);
                        tattoo_img.setImageBitmap(tattoo.image);
                    }});
        Server.getTattooData(this, tattoo_id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean is_private = response.getString("private").equals("1");
                            Switch sw = (Switch) findViewById(R.id.switch_visibility);
                            sw.setChecked(is_private);
                            sw.setOnCheckedChangeListener(switch_listener);
                            JSONArray tattooJSON = response.getJSONArray("tags");
                            for (int i = 0; i < tattooJSON.length(); i++)
                                tags.add(tattooJSON.getString(i));
                            tattoo.is_private = is_private;
                            tattoo.tags = tags;
                            for (int i = 0; i < tags.size(); i++) {
                                Tag ttag = new Tag(tags.get(i));
                                ttag.tagTextSize = 25f;
                                ttag.isDeletable = true;
                                ttag.layoutColor = getRandomColor();
                                tagGroup.addTag(ttag);
                            }
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
                        String tag = input.getText().toString();
                        tattoo.tags.add(tag);
                        Log.d("TAG", String.valueOf(tattoo.tags.size()));
                        Tag ttag = new Tag(tag);
                        ttag.isDeletable = true;
                        ttag.layoutColor = getRandomColor();
                        ttag.tagTextSize = 25f;
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
                Server.updateTattoo(TattooEditActivity.this, tattoo, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        User.getInstance().private_view.getAdapter().notifyDataSetChanged();
                        User.getInstance().public_view.getAdapter().notifyDataSetChanged();
                        }
                });
                finish();
            }
        });
    }

    public int getRandomColor() {
        ArrayList<String> colors = new ArrayList<>();
//        RED
        colors.add("#70B71C1C");
        colors.add("#70FF5252");
        colors.add("#70EF5353");
        colors.add("#70EF9A9A");
//        PINK
        colors.add("#70AD1457");
        colors.add("#70EC407A");
        colors.add("#70C51162");
//        PURPLE
        colors.add("#70AB47BC");
        colors.add("#70CE93D8");
//        DEEP PURPLE
        colors.add("#705E35B1");
        colors.add("#709775CD");
        colors.add("#706200EA");
//        INDIGO
        colors.add("#70536DFE");
//        BLUE
        colors.add("#701E88E5");
        colors.add("#7090CAF9");
        colors.add("#702979FF");
//        LIGHT BLUE
        colors.add("#7081D4FA");
//        CYAN
        colors.add("#7000BCD4");
//        EMBER
        colors.add("#70FFAB00");
        colors.add("#70FFC107");
        return Color.parseColor(colors.get(new Random().nextInt(colors.size())));
    }

}
