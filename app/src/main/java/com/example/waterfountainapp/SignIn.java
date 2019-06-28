package com.example.waterfountainapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {
    private EditText email;
    private EditText pass;
    private EditText user;
    private FirebaseAuth fireauth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        fireauth = FirebaseAuth.getInstance();

        email = findViewById(R.id.loginEmail);
        pass = findViewById(R.id.loginPass);
        user = findViewById(R.id.loginUser);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check to see if user is signed in
        FirebaseUser currentUser = fireauth.getCurrentUser();
        //FirebaseUser currentUser = null;

        if (currentUser != null) {
            Intent goHome = new Intent(this, MainActivity.class);
            startActivity(goHome);
            Toast.makeText(SignIn.this, "Authentication success", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickLogin(View view) {
        String eaddress = email.getText().toString();
        String password = pass.getText().toString();
        if ((eaddress.length() != 0) && (password.length() != 0)) {
            fireauth.signInWithEmailAndPassword(eaddress, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("Login", "signInWithEmail:success");
                                FirebaseUser user = fireauth.getCurrentUser();
                                Toast.makeText(SignIn.this, "Authentication success", Toast.LENGTH_SHORT).show();
                                Intent goHome = new Intent(SignIn.this, MainActivity.class);
                                startActivity(goHome);
                            } else {
                                Log.w("Login", "signInWithEmail:failure");
                                Toast.makeText(SignIn.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                FirebaseException e = (FirebaseAuthException)task.getException();
                                Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    public void onClickSignup(View view) {
        final String eaddress = email.getText().toString();
        String password = pass.getText().toString();
        final String username = user.getText().toString();
        if ((eaddress.length() != 0) && (password.length() != 0)) {
            fireauth.createUserWithEmailAndPassword(eaddress, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("Login", "createWithEmail:success");
                                FirebaseUser currentUser = fireauth.getCurrentUser();
                                Toast.makeText(SignIn.this, "Create Account success", Toast.LENGTH_SHORT).show();
                                database = FirebaseDatabase.getInstance().getReference();
                                String userid = currentUser.getUid();
                                database.child("users").child(currentUser.getUid()).child("email").setValue(eaddress);
                                database.child("users").child(currentUser.getUid()).child("username").setValue(username);
                                // Start activity intent to go to main
                                Intent goHome = new Intent(SignIn.this, MainActivity.class);
                                startActivity(goHome);
                            } else {
                                Log.w("Login", "createWithEmail:failure");
                                Toast.makeText(SignIn.this, "Create Account failed", Toast.LENGTH_SHORT).show();
                                FirebaseException e = (FirebaseAuthException)task.getException();
                                Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

}
