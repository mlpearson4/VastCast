package com.vastcast.vastcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient gsiClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsiClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = gsiClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    protected void onStart() {
        super.onStart();
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(code != ConnectionResult.SUCCESS) {
            Log.d("LoginActivity", "Google Play Services not available");
            GoogleApiAvailability.getInstance().showErrorNotification(this, code);
        }
        FirebaseUser account = mAuth.getCurrentUser();
        updateUI(account);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("LoginActivity", "signInResult:failed code=" + e.getStatusCode());
                Log.e("LoginActivity", Log.getStackTraceString(e));
                Snackbar.make(findViewById(R.id.clLogin), "Authentication Failed", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("LoginActivity", "signInWithCredential:success");
                    Snackbar.make(findViewById(R.id.clLogin), "Authentication Successful", Snackbar.LENGTH_SHORT).show();
                    FirebaseUser account = mAuth.getCurrentUser();
                    updateUI(account);
                }
                else {
                    Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                    Snackbar.make(findViewById(R.id.clLogin), "Authentication Failed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(FirebaseUser account) {
        if(account != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(i);
            finish();
        }
    }
}
