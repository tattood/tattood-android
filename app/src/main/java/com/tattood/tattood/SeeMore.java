package com.tattood.tattood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class SeeMore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more);
        Bundle extras = getIntent().getExtras();
        final String tag = extras.getString("TAG");
        User user = User.getInstance();
        final RecyclerView list_view = (RecyclerView) findViewById(R.id.list_view);
        list_view.setLayoutManager(new GridLayoutManager(this, 3));
        list_view.setAdapter(new TattooRecyclerViewAdapter(this, list_view));
        if (tag != null) {
            if (tag.equals("RECENT")) {
                Server.getRecent(this,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(response);
                            }
                        }, 100);
            } else if (tag.equals("POPULAR")) {
                Server.getPopular(this,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(response);
                            }
                        }, 100);
            } else if (user != null && tag.equals("PRIVATE")) {
                user.setPrivateView(this, list_view, 100);
            } else if (user != null && tag.equals("PUBLIC")) {
                user.setPublicView(this, list_view, 100);
            } else if (user != null && tag.equals("LIKED")) {
                user.setLikedView(this, list_view, 100);
            } else if (tag.equals("TAG")) {
                String query = extras.getString("query");
                Server.search(SeeMore.this, query,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject tags = response.getJSONObject("tags");
                                    if (!empty(tags))
                                        ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(tags);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 100);
            } else if (tag.equals("USER")) {
                String query = extras.getString("query");
                Server.search(SeeMore.this, query,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject tags = response.getJSONObject("users");
                                    if (!empty(tags))
                                        ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(tags);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 100);
            }
        }
    }

    private boolean empty(JSONObject obj) {
        try {
            obj = obj.getJSONObject("data");
            return obj.length() == 0;
        } catch (JSONException e) {
            return true;
        }
    }

}
