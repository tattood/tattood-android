package com.tattood.tattood;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;

import org.json.JSONObject;

public class DiscoveryActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recent_view;
    RecyclerView popular_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        String token = getIntent().getExtras().getString("token");
        String username = getIntent().getExtras().getString("username");
        Uri photo = Uri.parse(getIntent().getExtras().getString("photo-uri"));
        User.setInstance(token, username, photo);
        popular_view = (RecyclerView) findViewById(R.id.popular_list);
        popular_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popular_view.setAdapter(new TattooRecyclerViewAdapter(this, popular_view));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL);
        popular_view.addItemDecoration(dividerItemDecoration);
        Server.getPopular(this,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) popular_view.getAdapter()).set_data(response);
                    }
                });

        recent_view = (RecyclerView) findViewById(R.id.recent_list);
        recent_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recent_view.setAdapter(new TattooRecyclerViewAdapter(this, recent_view));
        recent_view.addItemDecoration(dividerItemDecoration);
        Server.getRecent(this,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) recent_view.getAdapter()).set_data(response);
                    }
                });

        Button see_more = (Button) findViewById(R.id.see_more_recent);
        see_more.setOnClickListener(this);
        see_more = (Button) findViewById(R.id.see_more_popular);
        see_more.setOnClickListener(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_profile);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent myIntent = new Intent(DiscoveryActivity.this, ProfileActivity.class);
//                ActivityOptions options =
//                        ActivityOptions.makeCustomAnimation(DiscoveryActivity.this,
//                                android.R.anim.slide_in_left,
//                                android.R.anim.slide_out_right);
//                startActivity(myIntent, options.toBundle());
//            }
//        });

        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Server.getRecent(DiscoveryActivity.this,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                ((TattooRecyclerViewAdapter) recent_view.getAdapter()).set_data(response);
                                swipeContainer.setRefreshing(false);
                            }
                        });
                Server.getPopular(DiscoveryActivity.this,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                ((TattooRecyclerViewAdapter) popular_view.getAdapter()).set_data(response);
                                swipeContainer.setRefreshing(false);
                            }
                        });
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public void onClick(View v) {
        Intent myIntent = new Intent(this, SeeMore.class);
        if (v.getId() == R.id.see_more_recent) {
            myIntent.putExtra("TAG", "RECENT");
        } else if (v.getId() == R.id.see_more_popular) {
            myIntent.putExtra("TAG", "POPULAR");
        }
        startActivity(myIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.profile) {
            Intent myIntent = new Intent(DiscoveryActivity.this, ProfileActivity.class);
            ActivityOptions options =
                    ActivityOptions.makeCustomAnimation(DiscoveryActivity.this,
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
            startActivity(myIntent, options.toBundle());
        }
        return super.onOptionsItemSelected(item);
    }
}
