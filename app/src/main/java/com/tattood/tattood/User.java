package com.tattood.tattood;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by eksi on 13/02/17.
 */

public class User {

    private static User user = null;
    public String email;
    public Uri photo;
    public final String username;
    public final String token;
    public RecyclerView liked_view;
    public RecyclerView public_view;
    public RecyclerView private_view;
    private User(String t, String u, Uri p) {
        username = u;
        token = t;
        photo = p;
        public_view = null;
        private_view = null;
        liked_view = null;
    }

    public static User getInstance() {
        return user;
    }

    public static void setInstance(String t, String u, Uri p) {
        if (user == null)
            user = new User(t, u, p);
    }

    public void setLikedView(Context context, final RecyclerView list_view) {
        setLikedView(context, list_view, 20);
    }

    public void setLikedView(Context context, final RecyclerView list_view, int limit) {
        setLikedView(context, list_view, limit, false);
    }

    public void setLikedView(Context context, final RecyclerView list_view, int limit, boolean refresh) {
        if (liked_view == null || refresh) {
            liked_view = list_view;
            Server.getTattooList(context, Server.TattooRequest.Liked, username,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(response);
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
        setPublicView(context, list_view, limit, false);
    }

    public void setPublicView(Context context, final RecyclerView list_view, int limit, boolean refresh) {
        if (public_view == null || refresh) {
            public_view = list_view;
            Log.d("NULL", "PUBLIC");
            Server.getTattooList(context, Server.TattooRequest.Public, username,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(response);
                        }
                    }, limit);
        } else {
            Log.d("NOT-NULL", "PUBLIC");
            changeView(public_view, list_view);
        }
    }

    public void setPrivateView(Context context, final RecyclerView list_view) {
        setPrivateView(context, list_view, 20);
    }

    public void setPrivateView(Context context, final RecyclerView list_view, int limit) {
        setPrivateView(context, list_view, limit, false);
    }

    public void setPrivateView(Context context, final RecyclerView list_view, int limit, boolean refresh) {
        if (private_view == null || refresh) {
            private_view = list_view;
            Server.getTattooList(context, Server.TattooRequest.Private, username,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ((TattooRecyclerViewAdapter) list_view.getAdapter()).set_data(response);
                        }
                    }, limit);
        } else {
            changeView(private_view, list_view);
        }
    }

    private void changeView(RecyclerView curr, RecyclerView _new) {
        TattooRecyclerViewAdapter adapter = (TattooRecyclerViewAdapter) curr.getAdapter();
        curr = _new;
        ((TattooRecyclerViewAdapter)curr.getAdapter()).setData(adapter.getData());
    }

    public void changeTattooVisibility(Tattoo t) {
        RecyclerView source = private_view;
        RecyclerView dest = public_view;
        if (t.is_private) {
            source = public_view;
            dest = private_view;
        }
        t = ((TattooRecyclerViewAdapter)source.getAdapter()).getTattoo(t.tattoo_id);
        ((TattooRecyclerViewAdapter)source.getAdapter()).removeTattoo(t);
        ((TattooRecyclerViewAdapter)dest.getAdapter()).addTattoo(t);
    }

    public void addLike(Tattoo t) {
        Log.d("USER", "LIKE");
        if (liked_view != null)
            ((TattooRecyclerViewAdapter)liked_view.getAdapter()).addTattoo(t);
    }

    public void removeLike(Tattoo t) {
        Log.d("USER", "UNLIKE");
        if (liked_view != null)
            ((TattooRecyclerViewAdapter)liked_view.getAdapter()).removeTattoo(t);
    }

    public Tattoo getTattoo(String tid) {
        Tattoo t = ((TattooRecyclerViewAdapter)public_view.getAdapter()).getTattoo(tid);
        if (t != null)
            return t;
        t = ((TattooRecyclerViewAdapter)private_view.getAdapter()).getTattoo(tid);
        if (t != null)
            return t;
        t = ((TattooRecyclerViewAdapter)public_view.getAdapter()).getTattoo(tid);
        return t;
    }

    public void addPublic(Tattoo t) {
        ((TattooRecyclerViewAdapter)public_view.getAdapter()).addTattoo(t);
    }

    public void addPrivate(Tattoo t) {
        ((TattooRecyclerViewAdapter)private_view.getAdapter()).addTattoo(t);
    }

    public void removePublic(Tattoo t) {
        ((TattooRecyclerViewAdapter)public_view.getAdapter()).removeTattoo(t);
    }

    public void removePrivate(Tattoo t) {
        ((TattooRecyclerViewAdapter)private_view.getAdapter()).removeTattoo(t);
    }

    public int like_count() {
        return liked_view.getAdapter().getItemCount();
    }
}
