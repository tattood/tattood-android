package com.tattood.tattod;

import android.content.Context;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by eksi on 13/02/17.
 */

public class User {

//    private String email;
    private String username;
    private String token;
    private ArrayList<Tattoo> liked = null;
    private ArrayList<Tattoo> upload_public = null;
    private ArrayList<Tattoo> upload_private = null;
//    private ArrayList<User> followed = null;
//    private ArrayList<User> followers = null;
    private Context context;

    public User(Context context, String t, String u) {
        token = t;
        username = u;
        this.context = context;
    }

    public ArrayList<Tattoo> getLiked(Response.Listener<JSONObject> callback) {
        if (liked == null)
            Server.getTattooList(context, token, Server.TattooRequest.Liked, username, callback);
        return liked;
    }

    public ArrayList<Tattoo> getPublic(Response.Listener<JSONObject> callback) {
        if (upload_public == null)
            Server.getTattooList(context, token, Server.TattooRequest.Public, username, callback);
        return upload_public;
    }

    public ArrayList<Tattoo> getPrivate(Response.Listener<JSONObject> callback) {
        if (upload_private == null)
            Server.getTattooList(context, token, Server.TattooRequest.Private, username, callback);
        return upload_private;
    }

    //
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
