package com.tattood.tattod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import static com.tattood.tattod.SplashActivity.PREFS_NAME;

public class ProfileFragment extends Fragment {
    private User user;
    private String token;
    private Context context;

    private RecyclerView user_liked;
    private RecyclerView user_private;
    private RecyclerView user_public;

    private OnListFragmentInteractionListener liked_listener;
    private OnListFragmentInteractionListener public_listener;
    private OnListFragmentInteractionListener private_listener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String token) {
        ProfileFragment fragment = new ProfileFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = view.getContext();
        user = new User(context, token);

        user_liked = (RecyclerView) view.findViewById(R.id.user_liked_list);
        liked_listener = new OnListFragmentInteractionListener();
        user_liked.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        user_liked.setAdapter(new TattooRecyclerViewAdapter(liked_listener, context, user_liked, 25));
        user.getLiked(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_liked.getAdapter()).set_data(token, response);
                    }
                });


        user_public = (RecyclerView) view.findViewById(R.id.user_public_list);
        public_listener = new OnListFragmentInteractionListener();
        user_public.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        user_public.setAdapter(new TattooRecyclerViewAdapter(public_listener, context, user_public, 25));
        user.getPublic(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_public.getAdapter()).set_data(token, response);
                    }
                });

        user_private = (RecyclerView) view.findViewById(R.id.user_private_list);
        private_listener = new OnListFragmentInteractionListener();
        user_private.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        user_private.setAdapter(new TattooRecyclerViewAdapter(private_listener, context, user_private, 25));
        user.getPrivate(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_private.getAdapter()).set_data(token, response);
                    }
                });


        Button signout_button = (Button) view.findViewById(R.id.signout_button);

        signout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Context context, String token, Response.Listener<JSONObject> callback)
                Server.logout(context, token, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, 0).edit();
                        editor.remove("username");
                        editor.apply();
                        Log.d("Logout", "|HERE");
                        Intent myIntent = new Intent(context, LoginActivity.class);
                        startActivity(myIntent);
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
