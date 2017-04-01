package com.tattood.tattod;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

import static com.android.volley.Response.*;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String PREFS_NAME = "LoginPreferences";
    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Server mServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLogin();
        askForPermission();
//        askForPermission(android.Manifest.permission.CAMERA, 0x5);
//        askForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 0x6);
//        askForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, 0x7);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String user = settings.getString("username", null);
        if (user != null) {
            Log.d("Login", "Already logged in with "+user);
            Intent myIntent = new Intent(this, MainActivity.class);
            myIntent.putExtra("token", settings.getString("token", null));
            this.startActivity(myIntent);
        }
        setContentView(R.layout.activity_login);
        Button login_button = (Button) findViewById(R.id.login_button);
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
//        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("Permission", permission + " " + requestCode);
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//                //This is called if user has denied the permission before
//                //In this case I am just asking the permission again
//                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
//
//            } else {
        ActivityCompat.requestPermissions(this, permission, 0x5);
//            }
//        }
    }

    private void initLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
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

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
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
                                    editor.putString("token", acct.getIdToken());
                                    editor.apply();
                                    Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                                    myIntent.putExtra("token", acct.getIdToken());
                                    startActivity(myIntent);
                                }
                            }, new ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Intent myIntent = new Intent(getBaseContext(), RegisterActivity.class);
                                    myIntent.putExtra("email", acct.getEmail());
                                    myIntent.putExtra("token", acct.getIdToken());
                                    startActivity(myIntent);
                                }
                            });
                        }
                    }
                });
    }

    private void signIn() {
        if (!Server.isOnline(this)) {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.d("Signing", "method signIn:");
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("Fail", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}

