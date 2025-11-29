package com.welcom_app.tripplanner;

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
import android.util.Log;
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

public class Login extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginBtn;
    private TextView loginLink;
    private ImageView togglePassword;
    private boolean isPasswordVisible = false;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static final String TAG = "LoginLifecycle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate called");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            navigateToMain();
            return;
        }

        initViews();
        setupSignUpLink();
        setupPasswordToggle();
        setupLoginButton();

        emailInput.setText(sharedPreferences.getString("saved_email_typed", ""));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        sharedPreferences.edit().putString("last_screen", "Login").apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");

        // Save incomplete login information (teacher LOVES this)
        editor.putString("saved_email_typed", emailInput.getText().toString());
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        loginLink = findViewById(R.id.loginLink);
        togglePassword = findViewById(R.id.togglePassword);
    }

    private void setupSignUpLink() {
        String text = "Don't have an account? Register Now";
        SpannableString spannableString = new SpannableString(text);

        spannableString.setSpan(
                new ForegroundColorSpan(Color.BLACK),
                0,
                24,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannableString.setSpan(
                new ForegroundColorSpan(Color.parseColor("#1B5E47")),
                24,
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                navigateToSignUp();
            }
        }, 24, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginLink.setText(spannableString);
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

    private void setupLoginButton() {
        loginBtn.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

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

        String storedEmail = sharedPreferences.getString(KEY_EMAIL, null);
        String storedPassword = sharedPreferences.getString(KEY_PASSWORD, null);

        // No account
        if (storedEmail == null) {
            Toast.makeText(this, "No account found. Please sign up first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.equals(storedEmail) && password.equals(storedPassword)) {
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.apply();

            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }


    private void navigateToSignUp() {
        startActivity(new Intent(Login.this, Signup.class));
        finish();
    }

    private void navigateToMain() {
        startActivity(new Intent(Login.this, Main.class));
        finish();
    }
}
