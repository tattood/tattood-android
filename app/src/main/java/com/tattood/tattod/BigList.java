package com.tattood.tattod;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tattood.tattod.dummy.DummyContent;
import com.tattood.tattod.dummy.DummyContent2;

public class BigList extends AppCompatActivity {

    private DiscoveryFragment.OnListFragmentInteractionListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_big_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        String data_type = getIntent().getExtras().getString("TAG");
        if (data_type.equals("RECENT"))
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(DummyContent2.RECENT_ITEMS, mListener));
        else
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(DummyContent2.POPULAR_ITEMS, mListener));
    }
}
