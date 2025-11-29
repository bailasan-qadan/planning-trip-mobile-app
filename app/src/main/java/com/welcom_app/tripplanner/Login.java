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
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            navigateToMain();
            return;
        }

        // Initialize views
        initViews();

        // Setup sign up link with colored text
        setupSignUpLink();

        // Setup password toggle
        setupPasswordToggle();

        // Setup login button
        setupLoginButton();
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

        // Make "Don't have an account?" black
        spannableString.setSpan(
                new ForegroundColorSpan(Color.BLACK),
                0,
                24,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Make "Register Now" green
        spannableString.setSpan(
                new ForegroundColorSpan(Color.parseColor("#1B5E47")),
                24,
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Make "Register Now" clickable
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                navigateToSignUp();
            }
        };
        spannableString.setSpan(
                clickableSpan,
                24,
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        loginLink.setText(spannableString);
        loginLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupPasswordToggle() {
        togglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    passwordInput.setInputType(
                            android.text.InputType.TYPE_CLASS_TEXT |
                                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                    togglePassword.setImageResource(R.drawable.pass);
                    isPasswordVisible = false;
                } else {
                    // Show password
                    passwordInput.setInputType(
                            android.text.InputType.TYPE_CLASS_TEXT |
                                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    );
                    togglePassword.setImageResource(R.drawable.pass);
                    isPasswordVisible = true;
                }
                // Move cursor to end
                passwordInput.setSelection(passwordInput.getText().length());
            }
        });
    }

    private void setupLoginButton() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }

        // Get stored credentials
        String storedEmail = sharedPreferences.getString(KEY_EMAIL, null);
        String storedPassword = sharedPreferences.getString(KEY_PASSWORD, null);

        // Check if account exists
        if (storedEmail == null) {
            Toast.makeText(this, "No account found. Please sign up first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify credentials
        if (email.equals(storedEmail) && password.equals(storedPassword)) {
            // Login successful
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.apply();

            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            // Invalid credentials
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(Login.this, Signup.class);
        startActivity(intent);
        finish();
    }

    private void navigateToMain() {
        Intent intent = new Intent(Login.this, Main.class);
        startActivity(intent);
        finish();
    }
}