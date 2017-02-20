package com.tattood.tattod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "LoginPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button register_button = (Button) findViewById(R.id.button_register);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.editText_username);
                final String username = tv.getText().toString();
                final String email = getIntent().getStringExtra("email");
                final String token = getIntent().getStringExtra("token");
                Server.register(RegisterActivity.this, email, username, token,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("username", username);
                                editor.apply();
                                Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                                myIntent.putExtra("token", getIntent().getStringExtra("token"));
                                startActivity(myIntent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null && networkResponse.statusCode == 400) {
                                    Toast.makeText(RegisterActivity.this,
                                            "This username is available please try again",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}
