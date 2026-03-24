package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import android.view.Window;
import android.view.WindowManager;
import androidx.core.view.WindowInsetsControllerCompat;

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

    private GestureDetector gestureDetector;
    private boolean useSwipe = true;
    private boolean useTransitions = true;

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

        // --- НАСТРОЙКА СВАЙПОВ ---
        gestureDetector = new GestureDetector(this, new SwipeHelper(new SwipeHelper.SwipeListener() {
            @Override
            public void onSwipeLeft() {
                // Свайп влево (палец идет влево) -> Переходим в Профиль (он справа)
                if (useSwipe) navigateTo(ProfileActivity.class, R.id.nav_profile, true);
            }

            @Override
            public void onSwipeRight() {
                // Свайп вправо (палец идет вправо) -> Переходим в Настройки (они слева)
                if (useSwipe) navigateTo(SettingsActivity.class, R.id.nav_settings, false);
            }
        }));

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_settings) {
                navigateTo(SettingsActivity.class, R.id.nav_settings, false);
                return true;
            } else if (id == R.id.nav_profile) {
                navigateTo(ProfileActivity.class, R.id.nav_profile, true);
                return true;
            }
            return false;
        });

        updateUI();
    }

    // Перехватываем касания экрана для свайпов
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useSwipe = prefs.getBoolean("enable_swipe", true);
        useTransitions = prefs.getBoolean("enable_transitions", true);
        if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_home);
    }

    // Умный метод для переходов с анимацией
    private void navigateTo(Class<?> targetActivity, int navId, boolean movingRightward) {
        startActivity(new Intent(this, targetActivity));
        if (useTransitions) {
            if (movingRightward) {
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Едем вправо
            } else {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // Едем влево
            }
        } else {
            overridePendingTransition(0, 0); // Без анимации
        }
        finish();
    }

    private void getAndLogFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d("FCM_TOKEN", task.getResult());
                });
    }

    private void updateUI() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        tvGreeting.setText("Привет, " + prefs.getString(KEY_USER_NAME, "User") + " 👋");

        int lightGray = Color.parseColor("#9E9E9E");
        tvGreeting.setTextColor(lightGray);
        tvStatusTitle.setTextColor(lightGray);
        tvHealthText.setTextColor(lightGray);
        tvActivityText.setTextColor(lightGray);

        String theme = prefs.getString(KEY_THEME, "day");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(window, window.getDecorView());

        if (theme.equals("night")) {
            rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            bottomNav.setBackgroundColor(Color.parseColor("#121212"));
            cardStatus.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardHealth.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardActivity.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            window.setStatusBarColor(Color.parseColor("#121212"));
            windowInsetsController.setAppearanceLightStatusBars(false);
        } else {
            rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            bottomNav.setBackgroundColor(Color.WHITE);
            cardStatus.setCardBackgroundColor(Color.WHITE);
            cardHealth.setCardBackgroundColor(Color.WHITE);
            cardActivity.setCardBackgroundColor(Color.WHITE);
            window.setStatusBarColor(Color.parseColor("#F4F7F6"));
            windowInsetsController.setAppearanceLightStatusBars(true);
        }
    }
}