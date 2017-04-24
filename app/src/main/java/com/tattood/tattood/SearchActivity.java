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

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle extras = getIntent().getExtras();
        query = extras.getString("query");
        final RecyclerView tag_list = (RecyclerView) findViewById(R.id.tag_list);
        tag_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tag_list.setAdapter(new TattooRecyclerViewAdapter(this, tag_list));
        final RecyclerView user_list = (RecyclerView) findViewById(R.id.user_list);
        user_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_list.setAdapter(new TattooRecyclerViewAdapter(this, user_list));
        Server.search(SearchActivity.this, query,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject tags = response.getJSONObject("tags");
                            JSONObject users = response.getJSONObject("users");
                            boolean not_found = false;
                            if (!empty(users)) {
                                TextView tv = (TextView) findViewById(R.id.text_user);
                                tv.setVisibility(View.VISIBLE);
                                Button button = (Button) findViewById(R.id.see_more_user);
                                button.setVisibility(View.VISIBLE);
                                user_list.setVisibility(View.VISIBLE);
                                ((TattooRecyclerViewAdapter) user_list.getAdapter()).set_data(users);
                            }
                            else {
                                not_found = true;
                            }
                            if (!empty(tags)) {
                                TextView tv = (TextView) findViewById(R.id.text_tag);
                                tv.setVisibility(View.VISIBLE);
                                Button button = (Button) findViewById(R.id.see_more_tag);
                                button.setVisibility(View.VISIBLE);
                                tag_list.setVisibility(View.VISIBLE);
                                ((TattooRecyclerViewAdapter) tag_list.getAdapter()).set_data(tags);
                            }
                            else {
                                if (not_found) {
                                    findViewById(R.id.tv_no_results).setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Button see_more = (Button) findViewById(R.id.see_more_tag);
        see_more.setOnClickListener(this);
        see_more = (Button) findViewById(R.id.see_more_user);
        see_more.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        Intent myIntent = new Intent(this, SeeMore.class);
        myIntent.putExtra("query", query);
        if (view.getId() == R.id.see_more_tag) {
            myIntent.putExtra("TAG", "TAG");
        } else if (view.getId() == R.id.see_more_user) {
            myIntent.putExtra("TAG", "USER");
        }
        startActivity(myIntent);
    }
}
