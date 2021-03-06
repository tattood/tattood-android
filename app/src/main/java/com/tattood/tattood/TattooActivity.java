package com.tattood.tattood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.tattood.unity.UnityPlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;

public class TattooActivity extends AppCompatActivity {

    boolean is_liked;
    int like_count;
    private String tattoo_id = null;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tattoo);
        Bundle extras = getIntent().getExtras();
        tattoo_id = extras.getString("tid");
        final Tattoo tattoo = new Tattoo(tattoo_id, null);
        final SimpleDraweeView tattoo_img = (SimpleDraweeView) findViewById(R.id.tattoo_image);
        Uri uri = Uri.parse(Server.host + "/tattoo?id="+tattoo.tattoo_id+"&token="+User.getInstance().token);
        Postprocessor redMeshPostprocessor = new BasePostprocessor() {
            @Override
            public String getName() {
                return "redMeshPostprocessor";
            }

            @Override
            public void process(Bitmap bitmap) {
                bmp = bitmap.copy(bitmap.getConfig(), true);
            }
        };
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(redMeshPostprocessor)
                .build();
        PipelineDraweeController controller = (PipelineDraweeController)
                Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(tattoo_img.getController())
                        .build();
        tattoo_img.setController(controller);

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
                            owner.setTextColor(getResources().getColor(R.color.colorWhite));
                            owner.setTypeface(null, Typeface.BOLD_ITALIC);
                            owner.setTextSize(16);
                            final SimpleDraweeView img = (SimpleDraweeView) findViewById(R.id.user_image);
                            img.setImageURI(url);
                            tattoo.owner_id = owner_id;
                            owner.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent myIntent = new Intent(TattooActivity.this, com.tattood.tattood.UserActivity.class);
                                    myIntent.putExtra("username", username);
                                    myIntent.putExtra("other_user_photo", url);
                                    startActivity(myIntent);
                                }});
                            img.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent myIntent = new Intent(TattooActivity.this, com.tattood.tattood.UserActivity.class);
                                    myIntent.putExtra("username", username);
                                    myIntent.putExtra("other_user_photo", url);
                                    startActivity(myIntent);
                                }});
                            JSONArray tattooJSON = response.getJSONArray("tags");
                            TagView tagGroup = (TagView) findViewById(R.id.tag_group);
                            for (int i = 0; i < tattooJSON.length(); i++) {
                                String t = tattooJSON.getString(i);
                                if (!t.isEmpty()) {
                                    com.tattood.tattood.TattooTag ttag = new com.tattood.tattood.TattooTag(t);
                                    tattoo.tags.add(ttag);
                                    tagGroup.addTag(ttag);
                                }
                            }
                            tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
                                @Override
                                public void onTagClick(Tag tag, int position) {
                                    Intent myIntent = new Intent(TattooActivity.this, com.tattood.tattood.SeeMore.class);
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
                                            com.tattood.tattood.User.getInstance().addLike(tattoo);
                                            refreshLikeButton();
                                        }
                                    };
                                    com.tattood.tattood.Server.like(TattooActivity.this, tattoo_id, true, callback);
                                }

                                @Override
                                public void unLiked(LikeButton likeButton) {
                                    Response.Listener<JSONObject> callback = new Response.Listener<JSONObject>(){
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            like_count--;
                                            com.tattood.tattood.User.getInstance().removeLike(tattoo);
                                            refreshLikeButton();
                                        }
                                    };
                                    com.tattood.tattood.Server.like(TattooActivity.this, tattoo_id, false, callback);
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
                Intent myIntent = new Intent(TattooActivity.this, UnityPlayerActivity.class);
                final String path = tattoo_id + ".png";
                try {
                    FileOutputStream fos = openFileOutput(path, Context.MODE_PRIVATE);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                myIntent.putExtra("path", path);
                startActivity(myIntent);
                Log.d("CAMERA", "RETURNED");
            }
        });
    }

    private void refreshLikeButton() {
        final TextView like_label = (TextView) findViewById(R.id.like_count);
        if(like_count != 0) {
            like_label.setTypeface(null, Typeface.BOLD);
            like_label.setText(like_count + " likes");
        }
        else {
            like_label.setText("");
        }
    }


}
