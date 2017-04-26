package com.tattood.tattood;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class TattooEditActivity extends AppCompatActivity {

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
                String label = tattoo.is_private ? "Private" : "Public";
                TextView private_label = (TextView) findViewById(R.id.label_visibility);
                private_label.setText(label);
                User.getInstance().changeTattooVisibility(tattoo);
            }
        };
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
                            tattoo.is_private = is_private;
                            for (int i = 0; i < tattooJSON.length(); i++) {
                                String tag = tattooJSON.getString(i);
                                TattooTag ttag = new TattooTag(tag, true);
                                tattoo.tags.add(ttag);
                                tagGroup.addTag(ttag);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        TextView add_tag = (TextView) findViewById(R.id.new_tag);
        add_tag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        ImageView upload_button = (ImageView) findViewById(R.id.edit_finish);
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

    public boolean tag_exists(String t) {
        for (TattooTag tt: tattoo.tags) {
            if (t.equals(tt.text))
                return true;
        }
        return false;
    }
}
