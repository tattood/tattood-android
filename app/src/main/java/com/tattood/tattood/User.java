package com.tattood.tattood;

import android.content.Context;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by eksi on 13/02/17.
 */

public class User {

//    private String email;
    private final String username;
    private final String token;
    private ArrayList<Tattoo> liked = null;
    private ArrayList<Tattoo> upload_public = null;
    private ArrayList<Tattoo> upload_private = null;
//    private ArrayList<User> followed = null;
//    private ArrayList<User> followers = null;
    private final Context context;

    public User(Context context, String t, String u) {
        token = t;
        username = u;
        this.context = context;
    }

    public void getLiked(Response.Listener<JSONObject> callback) {
        Server.getTattooList(context, token, Server.TattooRequest.Liked, username, callback);
    }

    public void getPublic(Response.Listener<JSONObject> callback) {
        Server.getTattooList(context, token, Server.TattooRequest.Public, username, callback);
    }

    public void getPrivate(Response.Listener<JSONObject> callback) {
        Server.getTattooList(context, token, Server.TattooRequest.Private, username, callback);
    }

//    public ArrayList<User> getFollowers(Response.Listener<JSONObject> callback) {
//        if (followers == null)
//            Server.getUserList(context, token, Server.UserRequest.Followers, callback);
//        return followers;
//    }
//
//    public ArrayList<User> getFollowed(Response.Listener<JSONObject> callback) {
//        if (followed == null)
//            Server.getUserList(context, token, Server.UserRequest.Followed, callback);
//        return followed;
//    }
}
