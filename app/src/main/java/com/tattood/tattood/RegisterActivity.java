package com.tattood.tattood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

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
                final Uri photo = Uri.parse(getIntent().getStringExtra("photo-uri"));
                Server.register(RegisterActivity.this, email, username, photo, token,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("username", username);
                                Intent myIntent = new Intent(getBaseContext(), DiscoveryActivity.class);
                                String token = getIntent().getStringExtra("token");
                                myIntent.putExtra("token", token);
                                myIntent.putExtra("photo-uri", photo.toString());
                                editor.putString("token", token);
                                editor.putString("email", email);
                                editor.putString("photo-uri", photo.toString());
                                editor.apply();
                                startActivity(myIntent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null && networkResponse.statusCode == 400) {
                                    Toast.makeText(RegisterActivity.this,
                                            "This username is not available please try again",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}
