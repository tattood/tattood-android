package com.tattood.tattood;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
        tattoo = User.getInstance().getTattoo(tattoo_id);
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
                        User.getInstance().private_view.getAdapter().notifyDataSetChanged();
                        User.getInstance().public_view.getAdapter().notifyDataSetChanged();
                        }
                });
                finish();
            }
        });
    }
}
