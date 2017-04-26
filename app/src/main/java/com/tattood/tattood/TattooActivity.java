package com.tattood.tattood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TattooActivity extends AppCompatActivity {

    boolean is_liked;
    int like_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tattoo);
        Bundle extras = getIntent().getExtras();
        final String tattoo_id = extras.getString("tid");
        final Tattoo tattoo = new Tattoo(tattoo_id, null);
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
                            final String username = response.getString("owner");
                            final String owner_id = response.getString("owner_id");
                            final String url = response.getString("url");
                            TextView owner = (TextView) findViewById(R.id.owner_name);
                            owner.setText(username);
                            Log.d("Tattoo", username);
                            final ImageView img = (ImageView) findViewById(R.id.user_image);
                            BasicImageDownloader dl = new BasicImageDownloader(img);
                            Log.d("PROFILE2", String.valueOf(url));
                            dl.execute(String.valueOf(url));
                            tattoo.owner_id = owner_id;
                            owner.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent myIntent = new Intent(TattooActivity.this, UserActivity.class);
                                    myIntent.putExtra("username", username);
                                    startActivity(myIntent);
                                }});
                            JSONArray tattooJSON = response.getJSONArray("tags");
                            TagView tagGroup = (TagView) findViewById(R.id.tag_group);
                            for (int i = 0; i < tattooJSON.length(); i++) {
                                String t = tattooJSON.getString(i);
                                if (!t.isEmpty()) {
                                    TattooTag ttag = new TattooTag(t);
                                    tattoo.tags.add(ttag);
                                    tagGroup.addTag(ttag);
                                }
                            }
                            tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
                                @Override
                                public void onTagClick(Tag tag, int position) {
                                    Intent myIntent = new Intent(TattooActivity.this, SearchActivity.class);
                                    myIntent.putExtra("TAG", "TAG");
                                    myIntent.putExtra("query", tag.text);
                                    startActivity(myIntent);
                                }
                            });
                            like_count = response.getInt("like_count");
                            is_liked = response.getBoolean("is_liked");
                            Log.d("LIKE", String.valueOf(is_liked));
                            refreshLikeButton();
                            final LikeButton like_button = (LikeButton) findViewById(R.id.like_button);
                            like_button.setLiked(is_liked);
                            like_button.setOnLikeListener(new OnLikeListener() {
                                @Override
                                public void liked(LikeButton likeButton) {
                                    Response.Listener<JSONObject> callback = new Response.Listener<JSONObject>(){
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            like_count++;
                                            User.getInstance().addLike(tattoo);
                                            refreshLikeButton();
                                        }
                                    };
                                    Server.like(TattooActivity.this, tattoo_id, true, callback);
                                }

                                @Override
                                public void unLiked(LikeButton likeButton) {
                                    Response.Listener<JSONObject> callback = new Response.Listener<JSONObject>(){
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            like_count--;
                                            User.getInstance().removeLike(tattoo);
                                            refreshLikeButton();
                                        }
                                    };
                                    Server.like(TattooActivity.this, tattoo_id, false, callback);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        ImageView fab = (ImageView) findViewById(R.id.fab_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CAMERA", "Clicked");
                Intent myIntent = new Intent(TattooActivity.this, CameraActivity.class);
                startActivity(myIntent);
            }
        });
    }

    private void refreshLikeButton() {
        final TextView like_label = (TextView) findViewById(R.id.like_count);
        like_label.setText(like_count + " likes");
    }
}
