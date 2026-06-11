package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private String selectedEmoji = "🐶";
    private TextView tvSelectedEmoji;
    private final String[] emojis = {"🐶", "🐱", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
        if (!prefs.contains("theme_mode")) {
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            String defaultTheme = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) ? "night" : "day";
            prefs.edit().putString("theme_mode", defaultTheme).apply();
        }

        setContentView(R.layout.activity_registration);
        applyTheme();

        tvSelectedEmoji = findViewById(R.id.tvSelectedEmoji);
        Button btnSelectEmoji = findViewById(R.id.btnSelectEmoji);
        Button btnStart = findViewById(R.id.btnStart);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        TextInputLayout tlLoginInput = findViewById(R.id.tlLoginInput);
        TextInputLayout tlNameInput = findViewById(R.id.tlNameInput);
        TextInputLayout tlPasswordInput = findViewById(R.id.tlPasswordInput);
        TextInputLayout tlPasswordConfirmInput = findViewById(R.id.tlPasswordConfirmInput);

        TextInputEditText etLoginInput = findViewById(R.id.etLoginInput);
        TextInputEditText etNameInput = findViewById(R.id.etNameInput);
        TextInputEditText etPasswordInput = findViewById(R.id.etPasswordInput);
        TextInputEditText etPasswordConfirmInput = findViewById(R.id.etPasswordConfirmInput);
        CheckBox cbRememberMe = findViewById(R.id.cbRememberMe);

        btnSelectEmoji.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Выберите аватара")
                    .setItems(emojis, (dialog, which) -> {
                        selectedEmoji = emojis[which];
                        tvSelectedEmoji.setText(selectedEmoji);
                    })
                    .show();
        });

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        });

        btnStart.setOnClickListener(v -> {
            tlLoginInput.setError(null);
            tlNameInput.setError(null);
            tlPasswordInput.setError(null);
            tlPasswordConfirmInput.setError(null);

            String login = etLoginInput.getText().toString().trim();
            String name = etNameInput.getText().toString().trim();
            String password = etPasswordInput.getText().toString();
            String passwordConfirm = etPasswordConfirmInput.getText().toString();
            boolean rememberMe = cbRememberMe.isChecked();

            boolean hasErrors = false;

            if (login.isEmpty()) { tlLoginInput.setError("Введите логин"); hasErrors = true; }
            else if (login.length() < 3) { tlLoginInput.setError("Логин от 3 символов"); hasErrors = true; }
            if (name.isEmpty()) { tlNameInput.setError("Как к вам обращаться?"); hasErrors = true; }
            if (password.isEmpty()) { tlPasswordInput.setError("Придумайте пароль"); hasErrors = true; }
            else if (password.length() < 6) { tlPasswordInput.setError("Минимум 6 символов"); hasErrors = true; }
            else if (!password.matches(".*[A-ZА-Я].*") || !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                tlPasswordInput.setError("Нужна заглавная буква и спецсимвол"); hasErrors = true;
            }
            if (passwordConfirm.isEmpty()) { tlPasswordConfirmInput.setError("Повторите пароль"); hasErrors = true; }
            else if (!password.equals(passwordConfirm)) { tlPasswordConfirmInput.setError("Пароли не совпадают!"); hasErrors = true; }

            if (hasErrors) return;

            btnStart.setEnabled(false);
            btnStart.setText("Загрузка...");

            // --- ЗАПИСЬ ДАННЫХ В ОБЛАКО ---
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("login", login);
            userMap.put("name", name);
            userMap.put("password", password);
            userMap.put("avatar", selectedEmoji);

            // Берем текущую тему телефона и добавляем дефолтные настройки
            String currentTheme = prefs.getString("theme_mode", "day");
            userMap.put("theme_mode", currentTheme);
            userMap.put("notifications", true);
            userMap.put("pet_volume", 100);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(login)
                    .set(userMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RegistrationActivity.this, "Аккаунт успешно создан!", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("user_login", login);
                        editor.putString("user_name", name);
                        editor.putString("user_password", password);
                        editor.putString("user_avatar_emoji", selectedEmoji);
                        editor.putBoolean("remember_me", rememberMe);
                        editor.putBoolean("is_registered", true);
                        editor.apply();

                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnStart.setEnabled(true);
                        btnStart.setText("Создать аккаунт");
                        Toast.makeText(RegistrationActivity.this, "Ошибка сервера: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
        String theme = prefs.getString("theme_mode", "day");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat insets = new WindowInsetsControllerCompat(window, window.getDecorView());

        View root = findViewById(R.id.reg_root);
        TextView tvTitle = findViewById(R.id.tvRegTitle);
        TextView tvEmoji = findViewById(R.id.tvSelectedEmoji);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);
        TextInputLayout tlLogin = findViewById(R.id.tlLoginInput);
        TextInputLayout tlName = findViewById(R.id.tlNameInput);
        TextInputLayout tlPass = findViewById(R.id.tlPasswordInput);
        TextInputLayout tlPassConfirm = findViewById(R.id.tlPasswordConfirmInput);
        TextInputEditText etLogin = findViewById(R.id.etLoginInput);
        TextInputEditText etName = findViewById(R.id.etNameInput);
        TextInputEditText etPass = findViewById(R.id.etPasswordInput);
        TextInputEditText etPassConfirm = findViewById(R.id.etPasswordConfirmInput);
        CheckBox cbRemember = findViewById(R.id.cbRememberMe);
        Button btnSelect = findViewById(R.id.btnSelectEmoji);
        Button btnStart = findViewById(R.id.btnStart);

        if (theme.equals("night")) {
            int white = Color.parseColor("#FFFFFF");
            int lightGray = Color.parseColor("#BDBDBD");
            int darkBg = Color.parseColor("#121212");
            int accentColor = Color.parseColor("#BB86FC");

            if (root != null) root.setBackgroundColor(darkBg);
            window.setStatusBarColor(darkBg);
            insets.setAppearanceLightStatusBars(false);

            if (tvTitle != null) tvTitle.setTextColor(white);
            if (tvEmoji != null) tvEmoji.setTextColor(white);
            if (tvGoToLogin != null) tvGoToLogin.setTextColor(accentColor);

            TextInputLayout[] layouts = {tlLogin, tlName, tlPass, tlPassConfirm};
            for (TextInputLayout tl : layouts) {
                if (tl != null) {
                    tl.setHintTextColor(ColorStateList.valueOf(white));
                    tl.setDefaultHintTextColor(ColorStateList.valueOf(lightGray));
                    tl.setBoxStrokeColor(white);
                }
            }

            TextInputEditText[] edits = {etLogin, etName, etPass, etPassConfirm};
            for (TextInputEditText et : edits) {
                if (et != null) et.setTextColor(white);
            }

            if (cbRemember != null) { cbRemember.setTextColor(white); cbRemember.setButtonTintList(ColorStateList.valueOf(accentColor)); }
            if (btnSelect != null) btnSelect.setTextColor(white);
            if (btnStart != null) btnStart.setTextColor(Color.BLACK);
        } else {
            int black = Color.parseColor("#212121");
            int darkGray = Color.parseColor("#757575");
            int lightBg = Color.parseColor("#F4F7F6");
            int accentColor = Color.parseColor("#6200EE");

            if (root != null) root.setBackgroundColor(lightBg);
            window.setStatusBarColor(lightBg);
            insets.setAppearanceLightStatusBars(true);

            if (tvTitle != null) tvTitle.setTextColor(black);
            if (tvEmoji != null) tvEmoji.setTextColor(black);
            if (tvGoToLogin != null) tvGoToLogin.setTextColor(accentColor);

            TextInputLayout[] layouts = {tlLogin, tlName, tlPass, tlPassConfirm};
            for (TextInputLayout tl : layouts) {
                if (tl != null) {
                    tl.setHintTextColor(ColorStateList.valueOf(black));
                    tl.setDefaultHintTextColor(ColorStateList.valueOf(darkGray));
                    tl.setBoxStrokeColor(black);
                }
            }

            TextInputEditText[] edits = {etLogin, etName, etPass, etPassConfirm};
            for (TextInputEditText et : edits) {
                if (et != null) et.setTextColor(black);
            }

            if (cbRemember != null) { cbRemember.setTextColor(black); cbRemember.setButtonTintList(ColorStateList.valueOf(accentColor)); }
            if (btnSelect != null) btnSelect.setTextColor(black);
            if (btnStart != null) btnStart.setTextColor(Color.WHITE);
        }
    }
}