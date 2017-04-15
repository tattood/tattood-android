package com.tattood.tattood;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_IMAGE = 200;
    private User user;
    private String token;

    private RecyclerView user_liked;
    private RecyclerView user_private;
    private RecyclerView user_public;
//
//    public ProfileActivity() {
//    }
//
//    public static ProfileActivity newInstance(String token, String username) {
//        ProfileActivity fragment = new ProfileActivity();
//        Bundle args = new Bundle();
//        args.putString("token", token);
//        args.putString("username", username);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        if (getArguments() != null) {
////            token = getArguments().getString("token");
////        }
//    }

    public void refresh_images() {
        user_liked = (RecyclerView) findViewById(R.id.user_liked_list);
        user_liked.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_liked.setAdapter(new TattooRecyclerViewAdapter(null, this, user_liked, token));
        user.getLiked(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_liked.getAdapter()).set_data(token, response);
                    }
                });

        user_public = (RecyclerView) findViewById(R.id.user_public_list);
        OnListFragmentInteractionListener public_listener = new OnListFragmentInteractionListener(this, token);
        user_public.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_public.setAdapter(new TattooRecyclerViewAdapter(public_listener, this, user_public, token));
        user.getPublic(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_public.getAdapter()).set_data(token, response);
                    }
                });

        user_private = (RecyclerView) findViewById(R.id.user_private_list);
        OnListFragmentInteractionListener private_listener = new OnListFragmentInteractionListener(this, token);
        user_private.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_private.setAdapter(new TattooRecyclerViewAdapter(private_listener, this, user_private, token));
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
        Intent myIntent = new Intent(this, SeeMore.class);
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
        startActivity(myIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
//        view = inflater.inflate(R.layout.fragment_profile, container, false);
        setContentView(R.layout.activity_profile);
//        this = view.getthis();
//        Bundle extras = getActivity().getIntent().getExtras();
//        String username = extras.getString("username", null);
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username", null);
        token = extras.getString("token");
        user = new User(this, token, username);

        refresh_images();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_upload);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        Button see_more = (Button) findViewById(R.id.see_more_private);
        see_more.setOnClickListener(this);
        see_more = (Button) findViewById(R.id.see_more_public);
        see_more.setOnClickListener(this);
        see_more = (Button) findViewById(R.id.see_more_liked);
        see_more.setOnClickListener(this);
//        return view;
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
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);

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
            Intent myIntent = new Intent(this, UploadActivity.class);
            myIntent.putExtra("token", token);
            myIntent.putExtra("path", picturePath);
            startActivity(myIntent);
        }


    }

}
