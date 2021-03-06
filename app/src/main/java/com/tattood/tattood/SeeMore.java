package com.tattood.tattood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SeeMore extends AppCompatActivity {

    private final int LIST_SIZE = 15;
    private int previousTotal = LIST_SIZE;
    private boolean loading = false;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private final User user = User.getInstance();
    private RecyclerView list_view;
    private String tag;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more);
        extras = getIntent().getExtras();
        tag = extras.getString("TAG");
        list_view = (RecyclerView) findViewById(R.id.list_view);
        list_view.setLayoutManager(new GridLayoutManager(this, 3));
        list_view.setAdapter(new TattooRecyclerViewAdapter(this, true));
        list_view.setHasFixedSize(true);
        list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager mLayoutManager = (GridLayoutManager)recyclerView.getLayoutManager();
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    ArrayList<Tattoo> val = ((TattooRecyclerViewAdapter)recyclerView.getAdapter()).mValues;
                    if (val.get(val.size() - 1) == null)
                        return;
                    populate_list(String.valueOf(val.size()));
                }

            }
        });
        if (tag != null) {
            populate_list(null);
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

    private void populate_list(final String latest) {
        if (tag.equals("RECENT")) {
            Server.getRecent(this,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(response);
                            if (latest != null) {
                                loading = true;
                                totalItemCount += LIST_SIZE;
                            }
                        }
                    }, LIST_SIZE, latest);
        } else if (tag.equals("POPULAR")) {
            Server.getPopular(this,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(response);
                        }
                    }, LIST_SIZE, latest);
        } else if (user != null && tag.equals("PRIVATE")) {
            user.setPrivateView(this, list_view, LIST_SIZE, false, latest);
        } else if (user != null && tag.equals("PUBLIC")) {
            user.setPublicView(this, list_view, LIST_SIZE, false, latest);
        } else if (user != null && tag.equals("LIKED")) {
            user.setLikedView(this, list_view, LIST_SIZE, false, latest);
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
                    }, LIST_SIZE, latest);
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
                    }, LIST_SIZE, latest);
        }
        if (latest != null) {
            loading = true;
            totalItemCount += LIST_SIZE;
        }
    }
}
