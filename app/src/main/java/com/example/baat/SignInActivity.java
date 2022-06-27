package com.example.baat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.baat.Models.Users;
import com.example.baat.databinding.ActivityMainBinding;
import com.example.baat.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    ProgressDialog progressDialog;  // for showing a progress dialog on clicking sign in buttton
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        getSupportActionBar().hide();    //hiding action bar from top of the page
        progressDialog = new ProgressDialog(SignInActivity.this);   // initiating progress dialog
        progressDialog.setTitle("Loging in!");
        progressDialog.setMessage("Loging in your account!");


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);  //gso stands for google signin option


        //onclclicking google button
        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



        binding.btnSignIn.setOnClickListener(new View.OnClickListener() { // using setOnclicklistener on sign in button
            @Override
            public void onClick(View view) {
                  progressDialog.show();
                  //  using function signInWithEmailAndPassword to sign in and giving email and password as arguments
                mAuth.signInWithEmailAndPassword(binding.etEmail.getText().toString(),binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) { // Task<AuthResult> task ->this is checking sign in succesfull or not
                                progressDialog.dismiss(); // hiding progress bar just after sign in
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(SignInActivity.this , MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(SignInActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                 }
                            }
                        });
            }
        });


        // if user is already signed in past show them mainActivty like whatsaapp , no need to sign in again
        if(mAuth.getCurrentUser()!=null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class); // using intent to change activity
            startActivity(intent);
        }

        // if create an account button is pressed using intent send the user to SignUp Acivity
        binding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this , SignUpActivity.class);
                startActivity(intent);
            }
        });

    }



    int RC_SIGN_IN=95;
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setProfilePic(user.getPhotoUrl().toString());

                            database.getReference().child("Users").child(user.getUid()).setValue(users);

                            Intent intent = new Intent(SignInActivity.this, MainActivity.class); // using intent to change activity
                            startActivity(intent);
                            Toast.makeText(SignInActivity.this, "signing in with GOOGLE", Toast.LENGTH_SHORT).show();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                           Log.w("TAG", "signInWithCredential:failure", task.getException());
                           Toast.makeText(SignInActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            Snackbar.make(binding.getRoot(),"Authentication Failed!",Snackbar.LENGTH_SHORT);
                          //  updateUI(null);
                        }
                    }
                });
    }

}