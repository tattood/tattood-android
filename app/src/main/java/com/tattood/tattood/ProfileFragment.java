package com.tattood.tattood;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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

public class ProfileFragment extends Fragment implements View.OnClickListener {
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
    public void onClick(View v) {
        Intent myIntent = new Intent(this.getContext(), SeeMore.class);
        Log.d("SEE-MORE", "CLICKED");
        if (v.getId() == R.id.see_more_private) {
            myIntent.putExtra("TAG", "PRIVATE");
        } else if (v.getId() == R.id.see_more_public) {
            myIntent.putExtra("TAG", "PUBLIC");
        } else if (v.getId() == R.id.see_more_liked) {
            myIntent.putExtra("TAG", "LIKED");
        }
        myIntent.putExtra("token", token);
        myIntent.putExtra("username", user.username);
        getContext().startActivity(myIntent);
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

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_upload);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        Button see_more = (Button) view.findViewById(R.id.see_more_private);
        see_more.setOnClickListener(this);
        see_more = (Button) view.findViewById(R.id.see_more_public);
        see_more.setOnClickListener(this);
        see_more = (Button) view.findViewById(R.id.see_more_liked);
        see_more.setOnClickListener(this);
        return view;

    }

//        @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && view != null) {
//            refresh_images(view);
//        }
//    }

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
            if(columnIndex < 0)
                return;

            String picturePath = cursor.getString(columnIndex);
            cursor.close(); // close cursor
            Log.d("Upload", picturePath);
            Intent myIntent = new Intent(getContext(), UploadActivity.class);
            myIntent.putExtra("token", token);
            myIntent.putExtra("path", picturePath);
            startActivity(myIntent);
        }


    }

}
