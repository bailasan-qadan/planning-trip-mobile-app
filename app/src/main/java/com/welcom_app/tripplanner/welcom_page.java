package com.welcom_app.tripplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class welcom_page extends AppCompatActivity {

    private ImageView logo;
    private boolean animationStarted = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcom_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logo = findViewById(R.id.logo);

        if (savedInstanceState != null) {
            animationStarted = savedInstanceState.getBoolean("animationStarted", false);
        }

        if (!animationStarted) {
            startLogoAnimation();
        }
    }

    private void startLogoAnimation() {
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(1500);
        anim.setFillAfter(true);
        logo.startAnimation(anim);
        animationStarted = true;

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
            boolean onboardingShown = prefs.getBoolean("onboardingShown", false);

            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(welcom_page.this, Main.class);
            } else if (!onboardingShown) {
                intent = new Intent(welcom_page.this, Onboarding.class);
                prefs.edit().putBoolean("onboardingShown", true).apply();
            } else {
                intent = new Intent(welcom_page.this, Login.class);
            }

            startActivity(intent);
            finish();
        }, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("animationStarted", animationStarted);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        animationStarted = savedInstanceState.getBoolean("animationStarted", false);
    }
}
