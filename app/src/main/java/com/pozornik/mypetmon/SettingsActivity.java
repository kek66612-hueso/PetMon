package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppConfig";
    private static final String KEY_THEME = "theme_mode";

    private View rootLayout;
    private Toolbar toolbar;
    private CardView cardTheme;
    private CardView cardAnim;
    private TextView tvThemeDesc, tvSettingsTitle;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rootLayout = findViewById(R.id.settings_root);
        toolbar = findViewById(R.id.toolbar_settings);
        cardTheme = findViewById(R.id.cardTheme);
        cardAnim = findViewById(R.id.cardAnim);
        tvThemeDesc = findViewById(R.id.tvThemeDesc);
        tvSettingsTitle = findViewById(R.id.tvSettingsTitle);
        bottomNav = findViewById(R.id.bottom_navigation);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        Button btnDay = findViewById(R.id.btnDay);
        Button btnNight = findViewById(R.id.btnNight);

        btnDay.setOnClickListener(v -> changeTheme("day"));
        btnNight.setOnClickListener(v -> changeTheme("night"));

        setupBottomNavigation();
        applyCurrentTheme();
        // Настройка выключателя анимации
        com.google.android.material.switchmaterial.SwitchMaterial switchSplash = findViewById(R.id.switchSplash);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Устанавливаем текущее состояние ползунка
        switchSplash.setChecked(prefs.getBoolean("show_splash_anim", true));

        // Слушаем изменения
        switchSplash.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("show_splash_anim", isChecked).apply();
            Toast.makeText(this, isChecked ? "Анимация включена" : "Анимация выключена", Toast.LENGTH_SHORT).show();
        });
    }


    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_settings);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_settings) return true;

            if (id == R.id.nav_home) {
                finish(); // Закрываем настройки, под ними уже открыта Главная
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish(); // Закрываем настройки, чтобы не плодить окна
                return true;
            }
            return false;
        });
    }

    private void changeTheme(String theme) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(KEY_THEME, theme).apply();
        applyCurrentTheme();
        Toast.makeText(this, "Тема обновлена", Toast.LENGTH_SHORT).show();
    }

    private void applyCurrentTheme() {
        // === ВЕСЬ ТЕКСТ СВЕТЛО-СЕРЫЙ ===
        int lightGray = Color.parseColor("#9E9E9E");
        toolbar.setTitleTextColor(lightGray);
        tvThemeDesc.setTextColor(lightGray);
        tvSettingsTitle.setTextColor(lightGray);

        String theme = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_THEME, "day");
        if (theme.equals("night")) {
            rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            bottomNav.setBackgroundColor(Color.parseColor("#121212"));
            cardTheme.setCardBackgroundColor(Color.parseColor("#1E1E1E"));

            // Красим новую карточку анимации в темный
            if (cardAnim != null) cardAnim.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
        } else {
            rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            bottomNav.setBackgroundColor(Color.WHITE);
            cardTheme.setCardBackgroundColor(Color.WHITE);

            // Красим новую карточку анимации в белый
            if (cardAnim != null) cardAnim.setCardBackgroundColor(Color.WHITE);
        }
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}