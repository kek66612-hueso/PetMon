package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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

        Button btnChangePhoto = findViewById(R.id.btnChangePhoto);
        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        loadProfileData();
        setupBottomNavigation();
        applyCurrentTheme();

        btnChangePhoto.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Выберите аватара")
                    .setItems(emojis, (dialog, which) -> {
                        selectedEmoji = emojis[which];
                        tvAvatarPreview.setText(selectedEmoji);
                    })
                    .show();
        });

        btnSaveProfile.setOnClickListener(v -> saveProfileData());
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) return true;

            if (id == R.id.nav_home) {
                finish(); // Закрываем профиль, под ним Главная
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish(); // Закрываем профиль
                return true;
            }
            return false;
        });
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        etUserName.setText(prefs.getString("user_name", ""));
        selectedEmoji = prefs.getString("user_avatar_emoji", "🐶");
        tvAvatarPreview.setText(selectedEmoji);
    }

    private void saveProfileData() {
        String newName = etUserName.getText().toString().trim();

        // Проверка на длину имени
        if (newName.length() < 2) {
            Toast.makeText(this, "Имя должно содержать хотя бы 2 символа", Toast.LENGTH_SHORT).show();
            return; // Прерываем сохранение
        }

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("user_name", newName);
        editor.putString("user_avatar_emoji", selectedEmoji);
        editor.apply();

        Toast.makeText(this, "Профиль сохранен!", Toast.LENGTH_SHORT).show();
        finish();
        overridePendingTransition(0, 0);
    }

    private void applyCurrentTheme() {
        // === ВЕСЬ ТЕКСТ СВЕТЛО-СЕРЫЙ ===
        int lightGray = Color.parseColor("#9E9E9E");
        toolbar.setTitleTextColor(lightGray);
        etUserName.setTextColor(lightGray);

        String theme = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_THEME, "day");
        if (theme.equals("night")) {
            rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            bottomNav.setBackgroundColor(Color.parseColor("#121212"));
            cardProfile.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
        } else {
            rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            bottomNav.setBackgroundColor(Color.WHITE);
            cardProfile.setCardBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}