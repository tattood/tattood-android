package com.tattood.tattod;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eksi on 08/02/17.
 */

public class Server {
    public static final String host = "http://10.0.2.2:5000";
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

    public static void signIn(Context context, String email, Response.Listener<JSONObject> callback) {
        signIn(context, email, callback, error_handler);
    }

    public static void signIn(Context context, String email,
                              Response.Listener<JSONObject> callback, Response.ErrorListener error_handler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = host + "/user?email="+email;
        Log.d("Login-email", email);
        JsonObjectRequest request = new JsonObjectRequest(url, null, callback, error_handler);
        queue.add(request);
    }

    public static void register(Context context, String email, String username,
                                Response.Listener<JSONObject> callback) {
        register(context, email, username, callback, error_handler);
    }

    public static void register(Context context, String email, String username,
                                Response.Listener<JSONObject> callback, Response.ErrorListener error_handler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = host + "/user";
        JSONObject data = new JSONObject();
        try {
            data.put("username", username);
            data.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("REGISTER-Body", data.toString());
        System.out.println(data.toString());
        JsonObjectRequest request = new JsonObjectRequest(url, data, callback, error_handler) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(request);
    }
}
