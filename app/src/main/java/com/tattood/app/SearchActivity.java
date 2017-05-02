package com.tattood.app;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
        tag_list.setLayoutManager(new GridLayoutManager(this, 3));
        tag_list.setAdapter(new TattooRecyclerViewAdapter(this, true));

        Server.search(SearchActivity.this, query,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject tags = response.getJSONObject("tags");
                            boolean not_found = false;
                            if (!empty(tags)) {
                                tag_list.setVisibility(View.VISIBLE);
                                ((TattooRecyclerViewAdapter) tag_list.getAdapter()).set_data(tags);
                            }
                            if (not_found) {
                                findViewById(R.id.tv_no_results).setVisibility(View.VISIBLE);
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
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }
    }


    @Override
    public void onClick(View view) {
        Intent myIntent = new Intent(this, SeeMore.class);
        myIntent.putExtra("query", query);
        /*if (view.getId() == R.id.see_more_tag) {
            myIntent.putExtra("TAG", "TAG");
        } else if (view.getId() == R.id.see_more_user) {
            myIntent.putExtra("TAG", "USER");
        }*/
        startActivity(myIntent);
    }
}