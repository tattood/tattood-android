package com.tattood.tattod;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Response;

import org.json.JSONObject;

public class SeeMore extends AppCompatActivity {

    private OnListFragmentInteractionListener mListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more);

        mListener = new OnListFragmentInteractionListener();
        Bundle extras = getIntent().getExtras();
        final String token = extras.getString("token");
        final String tag = extras.getString("TAG");

        final RecyclerView list_view = (RecyclerView) findViewById(R.id.list_view);
        list_view.setLayoutManager(new GridLayoutManager(this, 3));
//        list_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list_view.setAdapter(new TattooRecyclerViewAdapter(mListener, this, list_view, 100));
        if (tag.equals("RECENT")) {
            Server.getRecent(this,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(token, response);
                        }
                    }, 100);
        } else {
            Server.getPopular(this,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(token, response);
                        }
                    }, 100);
        }
    }

}
