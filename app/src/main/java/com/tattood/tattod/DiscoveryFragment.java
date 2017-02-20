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

    private void set_data(View view, int id, JSONObject obj) {
        final Context context = view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(id);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new TattooRecyclerViewAdapter(mListener, this.getContext(), recyclerView, 25));
        try {
            JSONArray arr;
            obj = obj.getJSONObject("data");
            Iterator<String> keys = obj.keys();
            while( keys.hasNext() ) {
                String key = keys.next();
                int i = Integer.parseInt(key);
                JSONArray tattooJSON = obj.getJSONArray(key);
                int tattoo_id = tattooJSON.getInt(0);
                int owner_id = tattooJSON.getInt(1);
                ((TattooRecyclerViewAdapter)recyclerView.getAdapter()).setTattoo(i, new Tattoo(tattoo_id, owner_id));
                Server.getTattooImage(context, tattoo_id, i, token,
                        new Server.ResponseCallback() {
                            @Override
                            public void run(int id, int i) {
                                try {
                                    String name = id + ".jpg";
                                    FileInputStream stream = context.openFileInput(name);
                                    Bitmap img = BitmapFactory.decodeStream(stream);
                                    ((TattooRecyclerViewAdapter)recyclerView.getAdapter()).setTattooImage(i, img);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        Context context = view.getContext();

        Server.getPopular(context, token,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        set_data(view, R.id.popular_list, response);
                    }
                });

        Server.getRecent(context, token,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        set_data(view, R.id.recent_list, response);
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
//            mListener = (OnListFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Tattoo item);
    }
}
