package com.tattood.tattod;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eksi on 08/02/17.
 */

public class Server {
//    public static final String host = "http://localhost:5000";
//    Uncomment below line when running in virtual device
    public static final String host = "http://139.179.211.192:5000";
    public enum TattooRequest {Liked, Public, Private};
    public enum UserRequest {Followed, Followers};

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }
    private static final Response.ErrorListener error_handler =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("Connection", "ERROR");
            Log.d("Connection", String.valueOf(isInternetAvailable()));
            Log.d("Connection", error.toString());
            Log.d("Connection", String.valueOf(error.networkResponse.statusCode));
        }
    };
    private Server() {
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void signIn(Context context, String token, String email, Response.Listener<JSONObject> callback) {
        signIn(context, token, email, callback, error_handler);
    }

    public static JSONObject create_json(String token, String email, String username) {
        JSONObject data = new JSONObject();
        try {
            if (username != null)
                data.put("username", username);
            data.put("token", token);
            data.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject create_json(String token, String email) {
        return create_json(token, email, null);
    }

    public static JSONObject create_json(String token) {
        return create_json(token, null, null);
    }

    public static void signIn(Context context, String token, String email,
                              Response.Listener<JSONObject> callback, Response.ErrorListener error_handler) {
        if (!Server.isOnline(context)) {
            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject data = create_json(token, email);
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = host + "/login";
        JsonObjectRequest request = new JsonObjectRequest(url, data, callback, error_handler);
        queue.add(request);
    }

    public static void register(Context context, String email, String username, String token,
                                Response.Listener<JSONObject> callback) {
        register(context, email, username, token, callback, error_handler);
    }

    public static void register(Context context, String email, String username, String token,
                                Response.Listener<JSONObject> callback, Response.ErrorListener error_handler) {
        if (!Server.isOnline(context)) {
            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = host + "/register";
        JSONObject data = create_json(token, email, username);
        Log.d("REGISTER-Body", data.toString());
        JsonObjectRequest request = new JsonObjectRequest(url, data, callback, error_handler) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(request);
    }

    public static void getUserList(Context context, String token, UserRequest r,
                                     Response.Listener<JSONObject> callback) {
    }

    public static void getTattooList(Context context, String token, TattooRequest r,
                                     Response.Listener<JSONObject> callback) {
        if (!Server.isOnline(context)) {
            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject data = create_json(token);
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url;
        if (r == TattooRequest.Liked) {
            url = host + "/user-likes";
        } else {
            if (r == TattooRequest.Public)
                url = host + "/user-tattoo?private=0";
            else
                url = host + "/user-tattoo?private=1";
        }
        JsonObjectRequest request = new JsonObjectRequest(url, data, callback, error_handler);
        queue.add(request);
    }

//    public static getTattoo(int id) {
//
//    }
}
