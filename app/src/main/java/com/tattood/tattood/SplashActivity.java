package com.tattood.tattood;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

public class SplashActivity extends Activity {
    public static final String PREFS_NAME = "LoginPreferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final String user = settings.getString("username", null);
        if (user != null) {
            final String token = settings.getString("token", null);
            final String email = settings.getString("email", null);
            final String photo = settings.getString("photo-uri", null);
            Log.d("Login", "Already logged in with "+user);
            Log.d("LOGIN", email);
            Server.signIn(SplashActivity.this, token, email, new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            Intent myIntent = new Intent(SplashActivity.this, DiscoveryActivity.class);
                            myIntent.putExtra("token", token);
                            myIntent.putExtra("username", user);
                            myIntent.putExtra("photo-uri", photo);
                            SplashActivity.this.startActivity(myIntent);
                        }
                    }
            );
        }
        else {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
        }
    }
}
