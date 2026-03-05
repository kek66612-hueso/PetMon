package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppConfig";
    private static final String KEY_THEME = "theme_mode";
    private static final String KEY_USER_NAME = "user_name";

    private View rootLayout;
    private TextView tvGreeting;
    private BottomNavigationView bottomNav;

    private CardView cardStatus, cardHealth, cardActivity;
    private TextView tvStatusTitle, tvHealthText, tvActivityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (!prefs.getBoolean("is_registered", false)) {
            startActivity(new Intent(this, RegistrationActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        rootLayout = findViewById(R.id.main_root);
        tvGreeting = findViewById(R.id.tvGreeting);
        bottomNav = findViewById(R.id.bottom_navigation);

        cardStatus = findViewById(R.id.cardStatus);
        cardHealth = findViewById(R.id.cardHealth);
        cardActivity = findViewById(R.id.cardActivity);

        tvStatusTitle = findViewById(R.id.tvStatusTitle);
        tvHealthText = findViewById(R.id.tvHealthText);
        tvActivityText = findViewById(R.id.tvActivityText);

        getAndLogFirebaseToken();
        updateUI();

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            }
            else if (id == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                // МГНОВЕННЫЙ ПЕРЕХОД: отключаем анимацию
                overridePendingTransition(0, 0);
                return true;
            }
            else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                // МГНОВЕННЫЙ ПЕРЕХОД: отключаем анимацию
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();

        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void getAndLogFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Не удалось получить токен", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM_TOKEN", token);
                    System.out.println("TOKEN: " + token);
                });
    }

    private void updateUI() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString(KEY_USER_NAME, "User");
        tvGreeting.setText("Привет, " + name + " 👋");

        // === ВЕСЬ ТЕКСТ СВЕТЛО-СЕРЫЙ ===
        int lightGray = Color.parseColor("#9E9E9E");
        tvGreeting.setTextColor(lightGray);
        tvStatusTitle.setTextColor(lightGray);
        tvHealthText.setTextColor(lightGray);
        tvActivityText.setTextColor(lightGray);

        String theme = prefs.getString(KEY_THEME, "day");
        if (theme.equals("night")) {
            rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            bottomNav.setBackgroundColor(Color.parseColor("#121212"));
            cardStatus.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardHealth.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardActivity.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
        } else {
            rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            bottomNav.setBackgroundColor(Color.WHITE);
            cardStatus.setCardBackgroundColor(Color.WHITE);
            cardHealth.setCardBackgroundColor(Color.WHITE);
            cardActivity.setCardBackgroundColor(Color.WHITE);
        }
    }
}