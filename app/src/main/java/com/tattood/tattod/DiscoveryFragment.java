package com.tattood.tattod;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;

import org.json.JSONObject;

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
        popular_view.setAdapter(new TattooRecyclerViewAdapter(mListener, context, popular_view, 20));
        Server.getPopular(context,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) popular_view.getAdapter()).set_data(token, response);
                    }
                });

        final RecyclerView recent_view = (RecyclerView) view.findViewById(R.id.recent_list);
        recent_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recent_view.setAdapter(new TattooRecyclerViewAdapter(mListener, context, recent_view, 20));
        Server.getRecent(context,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) recent_view.getAdapter()).set_data(token, response);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Intent myIntent = new Intent(this.getContext(), SeeMore.class);
        if (v.getId() == R.id.seemore_recent) {
            myIntent.putExtra("TAG", "RECENT");
        } else if (v.getId() == R.id.seemore_popular) {
            myIntent.putExtra("TAG", "POPULAR");
        }
        myIntent.putExtra("token", token);
        getContext().startActivity(myIntent);
    }
}
