package com.tattood.tattod;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DiscoveryFragment extends Fragment implements View.OnClickListener {

    private OnListFragmentInteractionListener mListener;
    private String token;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DiscoveryFragment() {
    }

    // TODO: Customize parameter initialization
    public static DiscoveryFragment newInstance(String token) {
        DiscoveryFragment fragment = new DiscoveryFragment();
        Bundle args = new Bundle();
        args.putString("token", token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            token = getArguments().getString("token");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        Context context = view.getContext();

        final RecyclerView popular_view = (RecyclerView) view.findViewById(R.id.popular_list);
        popular_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        popular_view.setAdapter(new TattooRecyclerViewAdapter(mListener, context, popular_view, 25));
        Server.getPopular(context, token,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) popular_view.getAdapter()).set_data(token, response);
                    }
                });

        final RecyclerView recent_view = (RecyclerView) view.findViewById(R.id.recent_list);
        recent_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recent_view.setAdapter(new TattooRecyclerViewAdapter(mListener, context, recent_view, 25));
        Server.getRecent(context, token,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) recent_view.getAdapter()).set_data(token, response);
//                        set_data(view, R.id.recent_list, response);
                    }
                });

        Button see_more = (Button) view.findViewById(R.id.seemore_recent);
        see_more.setOnClickListener(this);
        see_more = (Button) view.findViewById(R.id.seemore_popular);
        see_more.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
//        Intent myIntent = new Intent(this.getContext(), BigList.class);
//        if (v.getId() == R.id.seemore_recent) {
//            myIntent.putExtra("TAG", "RECENT");
//        } else if (v.getId() == R.id.seemore_popular) {
//            myIntent.putExtra("TAG", "POPULAR");
//        }
//        getContext().startActivity(myIntent);
    }
}
