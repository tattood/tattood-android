package com.tattood.tattood;

import android.content.Context;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by eksi on 13/02/17.
 */

public class User {

//    private String email;
    public final String username;
    private final String token;
    private final Context context;

    public User(Context context, String t, String u) {
        token = t;
        username = u;
        this.context = context;
    }

    public void getLiked(Response.Listener<JSONObject> callback) {
        getLiked(callback, 20);
    }

    public void getLiked(Response.Listener<JSONObject> callback, int limit) {
        Server.getTattooList(context, token, Server.TattooRequest.Liked, username, callback, limit);
    }

    public void getPublic(Response.Listener<JSONObject> callback) {
        getPublic(callback, 20);
    }
    public void getPublic(Response.Listener<JSONObject> callback, int limit) {
        Server.getTattooList(context, token, Server.TattooRequest.Public, username, callback, limit);
    }

    public void getPrivate(Response.Listener<JSONObject> callback) {
        getPrivate(callback, 20);
    }

    public void getPrivate(Response.Listener<JSONObject> callback, int limit) {
        Server.getTattooList(context, token, Server.TattooRequest.Private, username, callback, limit);
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
