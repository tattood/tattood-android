package com.tattood.tattood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

import static com.android.volley.Response.ErrorListener;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String PREFS_NAME = "LoginPreferences";
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLogin();
        askForPermission();
        setContentView(R.layout.activity_login);
        ImageButton login_button = (ImageButton) findViewById(R.id.login_button);
        login_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Signing", "clicked method signIn");
                signIn();
            }
        });
    }

    private void askForPermission() {
        String[] permission = {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permission, 0x5);
    }

    private void initLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("876997578175-3mvn4447hcvdpr4nr7vml1otj8ottad6.apps.googleusercontent.com")
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d("Login", "HERE");
                        if (getIntent().hasExtra("logout")) {
                            Log.d("Login", "HERE--Logout");
                            mAuth.signOut();
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            Log.d("Login", "Finally log-out");
                                            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
                                            editor.putString("username", null);
                                            editor.apply();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {}
                    })
                .build();
        mGoogleApiClient.connect();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth == null)
            Log.d("Firebase", "NULL");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.d("Signing", "signed out:");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Signing", "on activityResult");
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Signing", "on activityResult");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d("Fail", "Google Sign-in Failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("Firebase", "firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Complete", "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w("Sign-in", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Server.signIn(LoginActivity.this, acct.getIdToken(), acct.getEmail(),
                                    new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    String username = response.optString("username");
                                    SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("username", username);
                                    editor.putString("email", acct.getEmail());
                                    editor.putString("token", acct.getIdToken());
                                    editor.apply();
                                    Intent myIntent = new Intent(getBaseContext(), DiscoveryActivity.class);
                                    myIntent.putExtra("token", acct.getIdToken());
                                    startActivity(myIntent);
                                    finish();
                                }
                            }, new ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    int response = error.networkResponse.statusCode;
                                    if (response == 464) {
                                        Intent myIntent = new Intent(getBaseContext(), RegisterActivity.class);
                                        myIntent.putExtra("email", acct.getEmail());
                                        myIntent.putExtra("token", acct.getIdToken());
                                        startActivity(myIntent);
                                        finish();
                                    } else {
                                        Log.d("Network-Error", "" + response);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void signIn() {
        if (Server.isOffline(this)) {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.d("Signing", "method signIn 2:");
        if (signInIntent == null)
            Log.d("Signing", "method signIn null:");
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d("Signing", "method signIn 3:");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("Fail", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}

