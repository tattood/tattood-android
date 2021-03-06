package com.tattood.tattood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shaishavgandhi.loginbuttons.GooglePlusButton;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "LoginPreferences";
    private AnimationDrawable anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        GooglePlusButton register_button = (GooglePlusButton) findViewById(R.id.button_register);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.editText_username);
                final String username = tv.getText().toString();
                if (username.isEmpty()) {
                    tv.setError("Username cannot be empty");
                    return;
                }
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
                                editor.apply();
                                Intent myIntent = new Intent(getBaseContext(), DiscoveryActivity.class);
                                myIntent.putExtra("token", token);
                                myIntent.putExtra("username", username);
                                myIntent.putExtra("photo-uri", photo.toString());
                                startActivity(myIntent);
                                finish();
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

        EditText editText = (EditText) findViewById(R.id.editText_username);
        editText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        /*RelativeLayout container = (RelativeLayout) findViewById(R.id.activity_register);

        ObjectAnimator animator = ObjectAnimator.ofInt(container, "backgroundColor",
                ResourcesCompat.getColor(getResources(), R.color.colorStarfallPurple1, null),
                ResourcesCompat.getColor(getResources(), R.color.colorStarfallPurple2, null),
                ResourcesCompat.getColor(getResources(), R.color.colorStarfallPink, null),
                ResourcesCompat.getColor(getResources(), R.color.colorStarfallSomon, null),
                ResourcesCompat.getColor(getResources(), R.color.colorStarfallBeige, null)
        ).setDuration(30);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(REVERSE);
        animator.start();


        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(5000);
        anim.setExitFadeDuration(2000);
 */
    }

    // Starting animation:- start the animation on onResume.
    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
           anim.start();

    }

    // Stopping animation:- stop the animation on onPause.
    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }


}
