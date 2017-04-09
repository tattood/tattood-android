package com.tattood.tattood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        final String token = extras.getString("token");
        final String username = extras.getString("username");
        final String tag = extras.getString("tag");
        String by = null;
        String what = null;
        if (username != null) {
            by = "user";
            what = username;
        } else if (tag != null) {
            by = "tag";
            what = tag;
        }
        if (by != null) {
            final RecyclerView list_view = (RecyclerView) findViewById(R.id.result_list);
//            mListener = new OnListFragmentInteractionListener(this);
            list_view.setLayoutManager(new GridLayoutManager(this, 3));
            list_view.setAdapter(new TattooRecyclerViewAdapter(null, this, list_view, token));
            Server.search(SearchActivity.this, token, by, what,
                    new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Search", String.valueOf(response));
                        ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(token, response);
                    }
                });
        } else {
            TextView zero = (TextView) findViewById(R.id.no_result);
            RecyclerView list = (RecyclerView) findViewById(R.id.result_list);
            list.setVisibility(View.INVISIBLE);
            zero.setVisibility(View.VISIBLE);
        }
    }

}
