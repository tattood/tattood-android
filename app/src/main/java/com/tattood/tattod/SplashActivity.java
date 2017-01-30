package com.tattood.tattod;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "LoginPreferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String user = settings.getString("username", null);
        if (user == null) {
            Intent myIntent = new Intent(this, LoginActivity.class);
        } else {

        }
    }
}
