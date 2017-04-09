package com.tattood.tattod;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONObject;

public class SeeMore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more);
        Bundle extras = getIntent().getExtras();
        final String token = extras.getString("token");
        final String tag = extras.getString("TAG");

        final RecyclerView list_view = (RecyclerView) findViewById(R.id.list_view);
        list_view.setLayoutManager(new GridLayoutManager(this, 3));
        list_view.setAdapter(new TattooRecyclerViewAdapter(null, this, list_view, token));
        if (tag != null && tag.equals("RECENT")) {
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
