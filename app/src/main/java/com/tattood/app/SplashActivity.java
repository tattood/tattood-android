package com.tattood.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONObject;

public class SplashActivity extends Activity {
    public static final String PREFS_NAME = "LoginPreferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_splash);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final String user = settings.getString("username", null);
        if (user != null) {
            final String token = settings.getString("token", null);
            final String email = settings.getString("email", null);
            final String photo = settings.getString("photo-uri", null);
            Log.d("Login", "Already logged in with "+user);
            Log.d("Login", "token: "+token);
            Server.signIn(SplashActivity.this, token, email, new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            Intent myIntent = new Intent(SplashActivity.this, DiscoveryActivity.class);
                            myIntent.putExtra("token", token);
                            myIntent.putExtra("username", user);
                            myIntent.putExtra("photo-uri", photo);
                            SplashActivity.this.startActivity(myIntent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("LOGIN", "Cannot Connect to server");
                            Toast.makeText(SplashActivity.this, "Auto-login failed", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(myIntent);
                        }
                    }
            );
        }
        else {
            Log.d("Login", "user is null");
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
        }
    }
}
