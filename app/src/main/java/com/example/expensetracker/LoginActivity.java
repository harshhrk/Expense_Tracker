package com.example.expensetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,20}" +               //at least 8 characters and less than 20
                    "$");

    private TextInputLayout email, password;
    private Button loginBtn;
    private TextView signup;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);
        signup = findViewById(R.id.login_signup);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);



        signup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistartionActivity.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(view -> {
            String eEmail = email.getEditText().getText().toString();
            String ePassword = password.getEditText().getText().toString();

            if (isValidEmail(eEmail) && isValidPassword(ePassword)) {
                progressDialog.setMessage("Login in progress");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(eEmail, ePassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().toString() + "", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                });
            }
        });
    }

    public boolean isValidEmail(String emailInput) {
        if (emailInput.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Please enter a valid e-mail address");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    public boolean isValidPassword(String passwordInput) {
        if (passwordInput.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            password.setError("Password too weak");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }
}