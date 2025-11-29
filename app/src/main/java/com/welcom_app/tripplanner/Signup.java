package com.welcom_app.tripplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Signup extends AppCompatActivity {

    private EditText fullNameInput, emailInput, passwordInput;
    private Button signUpBtn;
    private TextView loginLink;
    private ImageView togglePassword;
    private boolean isPasswordVisible = false;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            navigateToMain();
            return;
        }

        initViews();
        setupLoginLink();
        setupPasswordToggle();
        setupSignUpButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPreferences("trip_prefs", MODE_PRIVATE)
                .edit()
                .putString("last_screen", "Signup")
                .apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSharedPreferences("trip_prefs", MODE_PRIVATE)
                .edit()
                .putString("draft_signup_email", emailInput.getText().toString())
                .putString("draft_signup_fullname", fullNameInput.getText().toString())
                .apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signUpBtn = findViewById(R.id.signUpBtn);
        loginLink = findViewById(R.id.loginLink);
        togglePassword = findViewById(R.id.togglePassword);

        fullNameInput.setText(sharedPreferences.getString("draft_signup_fullname", ""));
        emailInput.setText(sharedPreferences.getString("draft_signup_email", ""));
    }

    private void setupLoginLink() {
        String text = "Already have an account? Login Here";
        SpannableString span = new SpannableString(text);

        span.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#1B5E47")), 25, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                navigateToLogin();
            }
        }, 25, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginLink.setText(span);
        loginLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupPasswordToggle() {
        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordInput.setInputType(
                        android.text.InputType.TYPE_CLASS_TEXT |
                                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                togglePassword.setImageResource(R.drawable.pass);
                isPasswordVisible = false;
            } else {
                passwordInput.setInputType(
                        android.text.InputType.TYPE_CLASS_TEXT |
                                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                togglePassword.setImageResource(R.drawable.pass);
                isPasswordVisible = true;
            }
            passwordInput.setSelection(passwordInput.getText().length());
        });
    }

    private void setupSignUpButton() {
        signUpBtn.setOnClickListener(v -> handleSignUp());
    }

    private void handleSignUp() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            fullNameInput.setError("Full name is required");
            fullNameInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return;
        }

        String existingEmail = sharedPreferences.getString(KEY_EMAIL, null);
        if (existingEmail != null && existingEmail.equals(email)) {
            Toast.makeText(this, "Account already exists. Please login.", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();

        Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
        navigateToMain();
    }

    private void navigateToLogin() {
        startActivity(new Intent(Signup.this, Login.class));
        finish();
    }

    private void navigateToMain() {
        startActivity(new Intent(Signup.this, Main.class));
        finish();
    }
}
