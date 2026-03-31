package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppConfig";
    private static final String KEY_THEME = "theme_mode";

    private View rootLayout;
    private Toolbar toolbar;
    private CardView cardProfile;
    private TextView tvAvatarPreview;
    private TextInputEditText etUserName;
    private BottomNavigationView bottomNav;

    private String selectedEmoji = "🐶";
    private final String[] emojis = {"🐶", "🐱", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵"};

    private GestureDetector gestureDetector;
    private boolean useSwipe = true;
    private boolean useTransitions = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        rootLayout = findViewById(R.id.profile_root);
        toolbar = findViewById(R.id.toolbar_profile);
        cardProfile = findViewById(R.id.cardProfile);
        tvAvatarPreview = findViewById(R.id.tvAvatarPreview);
        etUserName = findViewById(R.id.etUserName);
        bottomNav = findViewById(R.id.bottom_navigation);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useSwipe = prefs.getBoolean("enable_swipe", true);
        useTransitions = prefs.getBoolean("enable_transitions", true);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Свайпы (из Профиля можно свайпнуть только на Главную)
        gestureDetector = new GestureDetector(this, new SwipeHelper(new SwipeHelper.SwipeListener() {
            @Override
            public void onSwipeLeft() {} // Дальше профиля ехать некуда
            @Override
            public void onSwipeRight() {
                if (useSwipe) navigateTo(MainActivity.class, R.id.nav_home, false);
            }
        }));

        loadProfileData();
        setupBottomNavigation();
        applyCurrentTheme();

        findViewById(R.id.btnChangePhoto).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Выберите аватара")
                    .setItems(emojis, (dialog, which) -> {
                        selectedEmoji = emojis[which];
                        tvAvatarPreview.setText(selectedEmoji);
                    }).show();
        });

        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> saveProfileData());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) return true;
            if (id == R.id.nav_home) {
                navigateTo(MainActivity.class, R.id.nav_home, false);
                return true;
            } else if (id == R.id.nav_settings) {
                navigateTo(SettingsActivity.class, R.id.nav_settings, false);
                return true;
            }
            return false;
        });
    }

    private void navigateTo(Class<?> targetActivity, int navId, boolean movingRightward) {
        startActivity(new Intent(this, targetActivity));
        if (useTransitions) {
            if (movingRightward) overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            else overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else overridePendingTransition(0, 0);
        super.finish();
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        etUserName.setText(prefs.getString("user_name", ""));
        selectedEmoji = prefs.getString("user_avatar_emoji", "🐶");
        tvAvatarPreview.setText(selectedEmoji);
    }

    private void saveProfileData() {
        String newName = etUserName.getText().toString().trim();
        if (newName.length() < 2) {
            Toast.makeText(this, "Имя должно содержать хотя бы 2 символа", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("user_name", newName);
        editor.putString("user_avatar_emoji", selectedEmoji);
        editor.apply();

        Toast.makeText(this, "Профиль сохранен!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void applyCurrentTheme() {
        int lightGray = Color.parseColor("#9E9E9E");
        toolbar.setTitleTextColor(lightGray);
        etUserName.setTextColor(lightGray);

        String theme = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_THEME, "day");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat insets = new WindowInsetsControllerCompat(window, window.getDecorView());

        if (theme.equals("night")) {
            rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            bottomNav.setBackgroundColor(Color.parseColor("#121212"));
            cardProfile.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            window.setStatusBarColor(Color.parseColor("#121212"));
            insets.setAppearanceLightStatusBars(false);
        } else {
            rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            bottomNav.setBackgroundColor(Color.WHITE);
            cardProfile.setCardBackgroundColor(Color.WHITE);
            window.setStatusBarColor(Color.parseColor("#F4F7F6"));
            insets.setAppearanceLightStatusBars(true);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (useTransitions) overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        else overridePendingTransition(0, 0);
    }
}