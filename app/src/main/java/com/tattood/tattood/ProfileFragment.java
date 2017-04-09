package com.tattood.tattod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;

import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;
import static com.tattood.tattod.R.id.signout_button;
import static com.tattood.tattod.SplashActivity.PREFS_NAME;

public class ProfileFragment extends Fragment {
    private static final int RESULT_LOAD_IMAGE = 200;
    private User user;
    private String token;
    private Context context;
    private View view;

    private RecyclerView user_liked;
    private RecyclerView user_private;
    private RecyclerView user_public;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String token, String username) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("token", token);
        args.putString("username", username);
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

    public void refresh_images(View view) {
        user_liked = (RecyclerView) view.findViewById(R.id.user_liked_list);
        user_liked.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        user_liked.setAdapter(new TattooRecyclerViewAdapter(null, context, user_liked, token));
        user.getLiked(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_liked.getAdapter()).set_data(token, response);
                    }
                });

        user_public = (RecyclerView) view.findViewById(R.id.user_public_list);
        OnListFragmentInteractionListener public_listener = new OnListFragmentInteractionListener(getContext(), token);
        user_public.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        user_public.setAdapter(new TattooRecyclerViewAdapter(public_listener, context, user_public, token));
        user.getPublic(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_public.getAdapter()).set_data(token, response);
                    }
                });

        user_private = (RecyclerView) view.findViewById(R.id.user_private_list);
        OnListFragmentInteractionListener private_listener = new OnListFragmentInteractionListener(getContext(), token);
        user_private.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        user_private.setAdapter(new TattooRecyclerViewAdapter(private_listener, context, user_private, token));
        user.getPrivate(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_private.getAdapter()).set_data(token, response);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = view.getContext();
        Bundle extras = getActivity().getIntent().getExtras();
        String username = extras.getString("username", null);
        user = new User(context, token, username);

        refresh_images(view);
        Button sign_out_button = (Button) view.findViewById(signout_button);

        sign_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        Button upload_button = (Button) view.findViewById(R.id.upload_button);

        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Upload", "Clicked");
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK  && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Log.d("Upload", String.valueOf(selectedImage));
            Log.d("Upload", filePathColumn[0]);
            Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            if (cursor == null || cursor.getCount() < 1) {
                return; // no cursor or no record. DO YOUR ERROR HANDLING
            }

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            if(columnIndex < 0) // no column index
                return; // DO YOUR ERROR HANDLING

            String picturePath = cursor.getString(columnIndex);

            cursor.close(); // close cursor
            Log.d("Upload", picturePath);
            Server.uploadImage(context, selectedImage, "1.png", token, false,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("UPLOAD", "Refresh");
                            refresh_images(view);
                        }
            });
        }
    }

}
