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
import android.widget.ImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_IMAGE = 200;
    private User user;

    public void refresh_images() {
        RecyclerView user_liked = (RecyclerView) findViewById(R.id.user_liked_list);
        user_liked.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_liked.setAdapter(new TattooRecyclerViewAdapter(this, user_liked));
        user.setLikedView(this, user_liked);

        RecyclerView user_public = (RecyclerView) findViewById(R.id.user_public_list);
        OnListFragmentInteractionListener public_listener = new OnListFragmentInteractionListener(this);
        user_public.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_public.setAdapter(new TattooRecyclerViewAdapter(this, user_public, public_listener));
        user.setPublicView(this, user_public);

        RecyclerView user_private = (RecyclerView) findViewById(R.id.user_private_list);
        OnListFragmentInteractionListener private_listener = new OnListFragmentInteractionListener(this);
        user_private.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_private.setAdapter(new TattooRecyclerViewAdapter(this, user_private, private_listener));
        user.setPrivateView(this, user_private);
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
        startActivity(myIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = User.getInstance();
        final ImageView img = (ImageView) findViewById(R.id.user_image);
        BasicImageDownloader dl = new BasicImageDownloader(img);
        String url = String.valueOf(user.photo);
        Log.d("PROFILE1", String.valueOf(url));
        dl.execute(String.valueOf(url));
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
    }

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
                return;
            }
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            if(columnIndex < 0)
                return;
            String picturePath = cursor.getString(columnIndex);
            cursor.close(); // close cursor
            Log.d("Upload", picturePath);
            Intent myIntent = new Intent(this, UploadActivity.class);
            myIntent.putExtra("path", picturePath);
            startActivity(myIntent);
            User.getInstance().private_view.getAdapter().notifyDataSetChanged();
            User.getInstance().public_view.getAdapter().notifyDataSetChanged();
        }
    }
}
