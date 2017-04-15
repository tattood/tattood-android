package com.tattood.tattood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by eksi on 13/02/17.
 */

public class User {

    private static User user = null;
    private String email;
    public final String username;
    private final String token;

    public RecyclerView liked_view;
    public RecyclerView public_view;
    public RecyclerView private_view;

    private User(String t, String u) {
        username = u;
        token = t;
        public_view = null;
        private_view = null;
        liked_view = null;
    }

    public static User getInstance() {
        return user;
    }

    public static void setInstance(String t, String u) {
        if (user == null)
            user = new User(t, u);
    }

    public void setLikedView(Context context, final RecyclerView list_view) {
        setLikedView(context, list_view, 20);
    }

    public void setLikedView(Context context, final RecyclerView list_view, int limit) {
        if (liked_view == null) {
            liked_view = list_view;
            Server.getTattooList(context, token, Server.TattooRequest.Liked, username,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(token, response);
                        }
                    }, limit);
        } else {
            changeView(liked_view, list_view);
        }
    }

    public void setPublicView(Context context, final RecyclerView list_view) {
        setPublicView(context, list_view, 20);
    }

    public void setPublicView(Context context, final RecyclerView list_view, int limit) {
        if (public_view == null) {
            public_view = list_view;
            Server.getTattooList(context, token, Server.TattooRequest.Public, username,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(token, response);
                        }
                    }, limit);
        } else {
            changeView(public_view, list_view);
        }
    }

    public void setPrivateView(Context context, final RecyclerView list_view) {
        setPrivateView(context, list_view, 20);
    }

    public void setPrivateView(Context context, final RecyclerView list_view, int limit) {
        if (private_view == null) {
            private_view = list_view;
            Server.getTattooList(context, token, Server.TattooRequest.Private, username,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(token, response);
                        }
                    }, limit);
        } else {
            changeView(private_view, list_view);
        }
    }

    private void changeView(RecyclerView curr, RecyclerView _new) {
        Log.d("Profile", "CHANGE");
        TattooRecyclerViewAdapter adapter = (TattooRecyclerViewAdapter) curr.getAdapter();
        curr = _new;
        Log.d("Profile", String.valueOf(adapter.getData().size()));
        ((TattooRecyclerViewAdapter)curr.getAdapter()).setData(adapter.getData());
    }

    public void addLike(Tattoo t) {
        ((TattooRecyclerViewAdapter)liked_view.getAdapter()).addTattoo(t);
    }

    public void addPublic(Tattoo t) {
        ((TattooRecyclerViewAdapter)public_view.getAdapter()).addTattoo(t);
    }

    public void addPrivate(Tattoo t) {
        ((TattooRecyclerViewAdapter)private_view.getAdapter()).addTattoo(t);
    }

    public int like_count() {
        return liked_view.getAdapter().getItemCount();
    }
}
