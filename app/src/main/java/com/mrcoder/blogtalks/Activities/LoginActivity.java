package com.mrcoder.blogtalks.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mrcoder.blogtalks.R;

public class LoginActivity extends AppCompatActivity {

    RegisterActivity registerActivity;
    private EditText edtUserEmail, edtUserPassword;
    private Button btnLogin;
    private TextView tvNewRegistration;
    private ProgressBar loginProgressBar;
    private FirebaseAuth mAuth;
    private ImageView loginUserProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUserEmail = findViewById(R.id.login_username);
        edtUserPassword = findViewById(R.id.login_password);
        loginProgressBar = findViewById(R.id.login_progressBar);
        loginUserProfileImage = findViewById(R.id.login_user_image);
        tvNewRegistration = findViewById(R.id.login_register_textView);
        btnLogin = findViewById(R.id.login_btnLogin);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setVisibility(View.VISIBLE);
        loginProgressBar.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strUserEmail = edtUserEmail.getText().toString().trim();
                String strUserPassword = edtUserPassword.getText().toString().trim();

                btnLogin.setVisibility(View.INVISIBLE);
                loginProgressBar.setVisibility(View.VISIBLE);

                if (strUserEmail.isEmpty() || strUserPassword.isEmpty()) {
                    showMessage("Invalid creadentials");
                } else {
                    signIn(strUserEmail, strUserPassword);
                }
            }
        });

        tvNewRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerActivity();
            }
        });

        loginUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerActivity();
            }
        });
    }

    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void registerActivity() {
        Intent registerActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(registerActivityIntent);
    }

    private void signIn(String strUserEmail, String strUserPassword) {
        mAuth.signInWithEmailAndPassword(strUserEmail, strUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    updateUI();
                } else {
                    showMessage(task.getException().getMessage());
                }
            }
        });
    }

    private void updateUI() {

//        registerActivity.updateUI();
        Intent homeintent = new Intent(getApplicationContext(), Home.class);
        startActivity(homeintent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI();
        }
    }
}
