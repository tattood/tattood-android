package com.tattood.tattood;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by eksi on 08/02/17.
 */

public class Server {
//    public static final String host = "http://139.179.168.150:5000";
    public static final String host = "http://192.168.1.26:5000";
    public enum TattooRequest {Liked, Public, Private}
//    public enum UserRequest {Followed, Followers}
    public static boolean isInternetAvailable() {
        try {
            InetAddress ip = InetAddress.getByName(host); //You can replace it with your name
            return !ip.toString().equals("");
        } catch (Exception e) {
            return false;
        }

    }
    private static final Response.Listener<JSONObject> default_json_callback =  new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
        }
    };

    public static final Response.ErrorListener default_error_handler =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("Connection", "ERROR");
            Log.d("Connection", String.valueOf(isInternetAvailable()));
            Log.d("Connection", error.toString());
        }
    };
    private Server() {
    }

    public static boolean isOffline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo == null || !netInfo.isConnectedOrConnecting();
    }

    private static void request(Context context, String url, JSONObject data,
                                Response.Listener<JSONObject> callback) {
        request(context, url, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, data, callback, default_error_handler);
    }

    private static void request(Context context, String url, JSONObject data,
                                Response.Listener<JSONObject> callback,
                                Response.ErrorListener error_handler) {
        request(context, url, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, data, callback, error_handler);
    }

    private static void request(Context context, String url, int timeout, JSONObject data,
                                Response.Listener<JSONObject> callback) {
        request(context, url, timeout, data, callback, default_error_handler);
    }

    private static void request(Context context, String url, int timeout, JSONObject data,
                                Response.Listener<JSONObject> callback,
                                Response.ErrorListener error_handler) {
        if (Server.isOffline(context)) {
            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(url, data, callback, error_handler);
        RetryPolicy policy = new DefaultRetryPolicy(timeout, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    public static JSONObject create_json(String token, String email, String username) {
        JSONObject data = new JSONObject();
        try {
            if (username != null)
                data.put("username", username);
            if (email != null)
                data.put("email", email);
            data.put("token", token);
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
        if (Server.isOffline(context)) {
            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject data = create_json(token, email);
        String url = host + "/login";
        request(context, url, data, callback, error_handler);
    }

//    public static void register(Context context, String email, String username, String token,
//                                Response.Listener<JSONObject> callback) {
//        register(context, email, username, token, callback, error_handler);
//    }

    public static void register(Context context, String email, String username, Uri photo, String token,
                                Response.Listener<JSONObject> callback, Response.ErrorListener error_handler) {
        if (Server.isOffline(context)) {
            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }
        String url = host + "/register";
        JSONObject data = create_json(token, email, username);
        try {
            data.put("photo", photo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request(context, url, data, callback, error_handler);
    }

//    public static void getUserList(Context context, String token, UserRequest r,
//                                     Response.Listener<JSONObject> callback) {
//    }

    public static void getPopular(Context context, Response.Listener<JSONObject> callback, int limit) {
        final String url = host + "/popular?limit=" + limit;
        Log.d("POPULAR", "" + limit);
        request(context, url, null, callback);
    }

    public static void getPopular(Context context, Response.Listener<JSONObject> callback) {
        getPopular(context, callback, 20);
    }

    public static void getRecent(Context context, Response.Listener<JSONObject> callback, int limit) {
        final String url = host + "/recent?limit=" + limit;
        request(context, url, null, callback);
    }

    public static void getRecent(Context context, Response.Listener<JSONObject> callback) {
        getRecent(context, callback, 20);
    }

    public static void getTattooData(final Context context, final String id,
                                     final String token, Response.Listener<JSONObject> callback) {
        final String url = host + "/tattoo-data?id="+id+"&token="+token;
        request(context, url, null, callback);
    }

    public static void getTattooImage(final Context context, final String id, final int item_id,
                                      final String token, final ResponseCallback callback) {
        final String url = host + "/tattoo?id="+id+"&token="+token;
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(url,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        if (response != null) {
                            FileOutputStream outputStream;
                            String name = id + ".png";
                            try {
                                outputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
                                outputStream.write(response);
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            callback.run(id, item_id);
                        }
                    }
                });
        RequestQueue mRequestQueue = Volley.newRequestQueue(context, new HurlStack());
        mRequestQueue.add(request);
    }

    public static void getTattooList(Context context, String token, TattooRequest r, String username,
                                     Response.Listener<JSONObject> callback, int limit) {
        String url;
        if (r == TattooRequest.Liked) {
            url = host + "/user-likes?";
        } else {
            if (r == TattooRequest.Public)
                url = host + "/user-tattoo?private=0";
            else
                url = host + "/user-tattoo?private=1";
        }
        url += "&token=" + token + "&user=" + username + "&limit=" + limit;
        Log.d("User-tattoo", url);
        request(context, url, null, callback);
    }

    public static void getTattooList(Context context, String token, TattooRequest r, String username,
                                     Response.Listener<JSONObject> callback) {
        getTattooList(context, token, r, username, callback, 20);
    }

    public static String getStringImage(Bitmap bmp){
        ByteArrayOutputStream byte_stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byte_stream);
        byte[] imageBytes = byte_stream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static void search(Context context, String token, String query, int limit,
                              Response.Listener<JSONObject> callback) {
        String url = host + "/search?query=" + query + "&token=" + token + "&limit=" + limit;
        request(context, url, null, callback);
    }

    public static void search(Context context, String token, String query,
                              Response.Listener<JSONObject> callback) {
        search(context, token, query, 20, callback);
    }

    public static void updateTattoo(Context context, String token, Tattoo tattoo) {
        String url = host + "/tattoo-update";
        JSONObject data = new JSONObject();
        try {
            data.put("token", token);
            data.put("id", tattoo.tattoo_id);
            data.put("tags", new JSONArray(tattoo.tags));
            Log.d("Update", String.valueOf(tattoo.tags.size()));
            for (int i =0 ; i < tattoo.tags.size(); i++)
                Log.d("Update", tattoo.tags.get(i));
            data.put("private", tattoo.is_private);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request(context, url, data, default_json_callback);
    }

    public static void uploadImage(Context context, final Uri path, final String token, final Tattoo tattoo,
                                   final Response.Listener<JSONObject> callback) {
        final Bitmap bitmap;
        final String url = host + "/tattoo-upload";
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), path);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        JSONObject data = new JSONObject();
        try {
            data.put("token", token);
            data.put("private", String.valueOf(tattoo.is_private));
            data.put("tag_count", String.valueOf(tattoo.tags.size()));
            for (int i = 0; i < tattoo.tags.size(); i++) {
                data.put("tag"+i, tattoo.tags.get(i));
            }
            data.put("image", getStringImage(bitmap));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request(context, url, data, callback);
    }

    public static void extractTags(Context context, final Uri path, final String token,
                                   ArrayList<float[]> x, ArrayList<float[]> y,
                                   final Response.Listener<JSONObject> callback) {
        final Bitmap bitmap;
        final String url = host + "/extract-tags";
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), path);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        JSONObject data = new JSONObject();
        try {
            data.put("token", token);
            data.put("image", getStringImage(bitmap));
            JSONArray px = new JSONArray();
            JSONArray py = new JSONArray();
            for (int i = 0; i < x.size(); i++)
                px.put(new JSONArray(x.get(i)));
            for (int i = 0; i < y.size(); i++)
                py.put(new JSONArray(y.get(i)));
            data.put("x", px);
            data.put("y", py);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int timeout = 600000;
        request(context, url, timeout, data, callback);
    }

    public static void like(Context context, final String token, final String id, int like,
                            Response.Listener<JSONObject> callback) {
        String url = host;
        if (like == 1) url += "/like";
        else url += "/unlike";
        JSONObject data = create_json(token, id);
        request(context, url, data, callback);
    }

    public interface ResponseCallback {
        void run(String id, int item_id);
    }
}
