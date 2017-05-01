package com.tattood.tattood;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;

import org.json.JSONObject;

public class ProfileFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private TattooRecyclerViewAdapter adapter;
    private String source;
    private String user_name;

    public static ProfileFragment getInstance(String title, String user_name) {
        ProfileFragment fra = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source", title);
        bundle.putString("user_name", user_name);
        fra.setArguments(bundle);
        return fra;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        source = bundle.getString("source");
        user_name = bundle.getString("user_name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), 3));
        OnListFragmentInteractionListener listener = null;
        if (user_name == null && (source.equals("Public") || source.equals("Private"))) {
            listener = new OnListFragmentInteractionListener(getContext());
        }
        adapter = new TattooRecyclerViewAdapter(mRecyclerView.getContext(), listener, true);
        mRecyclerView.setAdapter(adapter);
        refresh();

        return v;
    }

    public void refresh(){
        if(user_name == null) {
            if (source.equals("Public")) {
                User.getInstance().setPublicView(getContext(), mRecyclerView);
            } else if (source.equals("Private")) {
                User.getInstance().setPrivateView(getContext(), mRecyclerView);
            }
            else if(source.equals("Liked")) {
                User.getInstance().setLikedView(getContext(), mRecyclerView);
            }
        } else{
            if (source.equals("Public")) {
                Server.getTattooList(getContext(), Server.TattooRequest.Public, user_name,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                ((TattooRecyclerViewAdapter) mRecyclerView.getAdapter()).set_data(response);
                            }
                        }, 20);
            } else if(source.equals("Liked")) {
                Server.getTattooList(getContext(), Server.TattooRequest.Liked, user_name,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                ((TattooRecyclerViewAdapter) mRecyclerView.getAdapter()).set_data(response);
                            }
                        }, 20);
            }
        }
    }

}
