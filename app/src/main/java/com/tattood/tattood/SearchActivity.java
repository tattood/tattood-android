package com.tattood.tattood;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(query);
        setSupportActionBar(myToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
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
        startActivity(myIntent);
    }


}
