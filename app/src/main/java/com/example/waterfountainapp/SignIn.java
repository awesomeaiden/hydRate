package com.example.waterfountainapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    private EditText email;
    private EditText pass;
    private FirebaseAuth fireauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        fireauth = FirebaseAuth.getInstance();

        email = findViewById(R.id.loginEmail);
        pass = findViewById(R.id.loginPass);
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
                        }
                    }
                });
    }

    public void onClickSignup(View view) {
        String eaddress = email.getText().toString();
        String password = pass.getText().toString();

        fireauth.createUserWithEmailAndPassword(eaddress, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Login", "createWithEmail:success");
                            FirebaseUser user = fireauth.getCurrentUser();
                            Toast.makeText(SignIn.this, "Create Account success", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w("Login", "createWithEmail:failure");
                            Toast.makeText(SignIn.this, "Create Account failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
