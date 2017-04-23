package com.tattood.tattood;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle extras = getIntent().getExtras();
        final String query = extras.getString("query");
        final RecyclerView tag_list = (RecyclerView) findViewById(R.id.tag_list);
        tag_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tag_list.setAdapter(new TattooRecyclerViewAdapter(null, this, tag_list));
        final RecyclerView user_list = (RecyclerView) findViewById(R.id.user_list);
        user_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        user_list.setLayoutManager(new GridLayoutManager(this, 3));
        user_list.setAdapter(new TattooRecyclerViewAdapter(null, this, user_list));
        Server.search(SearchActivity.this, query,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject tags = response.getJSONObject("tags");
                            JSONObject users = response.getJSONObject("users");
                            if (!empty(users))
                                ((TattooRecyclerViewAdapter) user_list.getAdapter()).set_data(users);
                            else {
                                TextView tv = (TextView) findViewById(R.id.text_user);
                                tv.setVisibility(View.INVISIBLE);
                                Button button = (Button) findViewById(R.id.see_more_user);
                                button.setVisibility(View.INVISIBLE);
                                user_list.setVisibility(View.INVISIBLE);
                            }
                            if (!empty(tags))
                                ((TattooRecyclerViewAdapter) tag_list.getAdapter()).set_data(tags);
                            else {
                                TextView tv = (TextView) findViewById(R.id.text_tag);
                                tv.setVisibility(View.INVISIBLE);
                                Button button = (Button) findViewById(R.id.see_more_tag);
                                button.setVisibility(View.INVISIBLE);
                                tag_list.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        handleIntent(getIntent());
    }

    public boolean empty(JSONObject obj) {
        try {
            obj = obj.getJSONObject("data");
            return obj.length() == 0;
        } catch (JSONException e) {
            return true;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("SEARCH", query);
        }
    }
}
