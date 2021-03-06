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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Created by eksi on 08/02/17.
 */

public class Server {
    public static final String host = "http://46.101.120.15:5000";
//    public static final String host = "http://192.168.1.26:5000";
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
        JsonObjectRequest request = new JsonObjectRequest(host + url, data, callback, error_handler);
        RetryPolicy policy = new DefaultRetryPolicy(timeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
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

    public static void signIn(Context context, String token, String email,
                              Response.Listener<JSONObject> callback,
                              Response.ErrorListener error_handler) {
        JSONObject data = create_json(token, email);
        request(context, "/login", data, callback, error_handler);
    }

    public static void register(Context context, String email, String username, Uri photo, String token,
                                Response.Listener<JSONObject> callback, Response.ErrorListener error_handler) {
        JSONObject data = create_json(token, email, username);
        try {
            data.put("photo", photo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request(context, "/register", data, callback, error_handler);
    }

    public static void getPopular(Context context, Response.Listener<JSONObject> callback,
                                  int limit, String latest) {
        if (latest == null)
            getPopular(context, callback, limit);
        else
            request(context, "/popular?limit=" + limit + "&latest=" + latest, null, callback);
    }

    public static void getPopular(Context context, Response.Listener<JSONObject> callback, int limit) {
        request(context, "/popular?limit=" + limit, null, callback);
    }

    public static void getPopular(Context context, Response.Listener<JSONObject> callback) {
        getPopular(context, callback, 20);
    }

    public static void getRecent(Context context, Response.Listener<JSONObject> callback,
                                 int limit, String latest) {
        if (latest == null)
            getRecent(context, callback, limit);
        else
            request(context, "/recent?limit=" + limit + "&latest=" + latest, null, callback);
    }

    public static void getRecent(Context context, Response.Listener<JSONObject> callback, int limit) {
        request(context, "/recent?limit=" + limit, null, callback);
    }

    public static void getRecent(Context context, Response.Listener<JSONObject> callback) {
        getRecent(context, callback, 20);
    }

    public static void getTattooData(final Context context, final String id,
                                     Response.Listener<JSONObject> callback) {
        final String url = "/tattoo-data?id="+id+"&token="+User.getInstance().token;
        request(context, url, null, callback);
    }

    public static void getTattooImage2(final Tattoo tattoo, SimpleDraweeView view) {
        final String url = host + "/tattoo?id="+tattoo.tattoo_id+"&token="+User.getInstance().token;
        view.setImageURI(url);
    }

    public static void getTattooList(Context context, TattooRequest r, String username,
                                     Response.Listener<JSONObject> callback, int limit) {
        getTattooList(context, r, username, callback, limit, null);
    }
    public static void getTattooList(Context context, TattooRequest r, String username,
                                     Response.Listener<JSONObject> callback, int limit, String latest) {
        String url;
        if (r == TattooRequest.Liked) {
            url = "/user-likes?";
        } else {
            if (r == TattooRequest.Public)
                url = "/user-tattoo?private=0";
            else
                url = "/user-tattoo?private=1";
        }
        url += "&token=" + User.getInstance().token + "&user=" + username + "&limit=" + limit;
        if (latest != null)
            url += "&latest=" + latest;
        Log.d("User-tattoo", url);
        request(context, url, null, callback);
    }

    public static String getStringImage(Bitmap bmp){
        ByteArrayOutputStream byte_stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byte_stream);
        byte[] imageBytes = byte_stream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static void search(Context context, String query, Response.Listener<JSONObject> callback,
                              int limit, String latest) {
        if (latest == null)
            search(context, query, callback, limit);
        else {
            String url = "/search?query=" + query + "&token=" + User.getInstance().token
                    + "&limit=" + limit + "&latest=" + latest;
            request(context, url, null, callback);
        }
    }

    public static void search(Context context, String query, Response.Listener<JSONObject> callback,
                              int limit) {
        String url = "/search?query=" + query + "&token=" + User.getInstance().token + "&limit=" + limit;
        request(context, url, null, callback);
    }

    public static void search(Context context, String query, Response.Listener<JSONObject> callback) {
        search(context, query, callback, 20);
    }

    public static void updateTattoo(Context context, Tattoo tattoo,
                                    final Response.Listener<JSONObject> callback) {
        final String url = "/tattoo-update";
        JSONObject data = new JSONObject();
        try {
            data.put("token", User.getInstance().token);
            data.put("id", tattoo.tattoo_id);
            ArrayList<String> tags = new ArrayList<>();
            for (TattooTag t : tattoo.tags) {
                tags.add(t.text);
            }
            tags = new ArrayList<>(new LinkedHashSet<>(tags));
            data.put("tags", new JSONArray(tags));
            Log.d("tags", (new JSONArray(tags).toString()));
            data.put("private", tattoo.is_private);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request(context, url, data, callback);
    }

    private static Bitmap loadImage(Context context, final Uri path) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), path);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void uploadImage(Context context, final Uri path, final Tattoo tattoo,
                                   ArrayList<float[]> x, ArrayList<float[]> y,
                                   final Response.Listener<JSONObject> callback) {
        final Bitmap bitmap = loadImage(context, path);
        final String url = "/tattoo-upload";
        JSONObject data = new JSONObject();
        try {
            data.put("token", User.getInstance().token);
            data.put("id", tattoo.tattoo_id);
            ArrayList<String> tags = new ArrayList<>();
            for (TattooTag t : tattoo.tags) {
                tags.add(t.text);
            }
            data.put("tags", new JSONArray(tags));
            data.put("private", tattoo.is_private);
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
        request(context, url, data, callback);
    }

    public static void delete(Context context, final Tattoo tattoo,
                              Response.Listener<JSONObject> callback) {
        JSONObject data = create_json(User.getInstance().token, tattoo.tattoo_id);
        request(context, "/tattoo-delete", data, callback);
    }

    public static void extractTags(Context context, final Uri path,
                                   ArrayList<float[]> x, ArrayList<float[]> y,
                                   final Response.Listener<JSONObject> callback) {
        final Bitmap bitmap = loadImage(context, path);
        JSONObject data = new JSONObject();
        try {
            data.put("token", User.getInstance().token);
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
        int timeout = 60000;
        request(context, "/extract-tags", timeout, data, callback);
    }

    public static void like(Context context, final String id, boolean likes,
                            Response.Listener<JSONObject> callback) {
        JSONObject data = create_json(User.getInstance().token, id);
        request(context, likes ? "/like" : "/unlike", data, callback);
    }

    public interface ResponseCallback {
        void run();
    }
}
