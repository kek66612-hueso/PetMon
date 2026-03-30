package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Применяем тему сразу после загрузки разметки!
        applyTheme();

        TextInputLayout tlLogin = findViewById(R.id.tlLoginInput);
        TextInputLayout tlPass = findViewById(R.id.tlPasswordInput);
        TextInputEditText etLogin = findViewById(R.id.etLoginInput);
        TextInputEditText etPass = findViewById(R.id.etPasswordInput);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToReg = findViewById(R.id.tvGoToReg);

        btnLogin.setOnClickListener(v -> {
            tlPass.setError(null); // Сбрасываем ошибку

            String inputLogin = etLogin.getText().toString().trim();
            String inputPass = etPass.getText().toString();

            SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
            String savedLogin = prefs.getString("user_login", "");
            String savedPass = prefs.getString("user_password", "");

            if (inputLogin.equals(savedLogin) && inputPass.equals(savedPass)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                tlPass.setError("Неверный логин или пароль");
            }
        });

        tvGoToReg.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistrationActivity.class));
            finish();
        });
    }

    // --- МЕТОД ПОКРАСКИ ЭКРАНА ЛОГИНА ---
    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
        String theme = prefs.getString("theme_mode", "day");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat insets = new WindowInsetsControllerCompat(window, window.getDecorView());

        View root = findViewById(R.id.login_root);
        TextView tvTitle = findViewById(R.id.tvLoginTitle);
        TextView tvGoToReg = findViewById(R.id.tvGoToReg);

        TextInputLayout tlLogin = findViewById(R.id.tlLoginInput);
        TextInputLayout tlPass = findViewById(R.id.tlPasswordInput);
        TextInputEditText etLogin = findViewById(R.id.etLoginInput);
        TextInputEditText etPass = findViewById(R.id.etPasswordInput);
        Button btnLogin = findViewById(R.id.btnLogin);

        if (theme.equals("night")) {
            int white = Color.parseColor("#FFFFFF");
            int lightGray = Color.parseColor("#BDBDBD");
            int darkBg = Color.parseColor("#121212");
            int accentColor = Color.parseColor("#BB86FC");

            if (root != null) root.setBackgroundColor(darkBg);
            window.setStatusBarColor(darkBg);
            insets.setAppearanceLightStatusBars(false);

            if (tvTitle != null) tvTitle.setTextColor(white);
            if (tvGoToReg != null) tvGoToReg.setTextColor(accentColor);

            TextInputLayout[] layouts = {tlLogin, tlPass};
            for (TextInputLayout tl : layouts) {
                if (tl != null) {
                    tl.setHintTextColor(ColorStateList.valueOf(white));
                    tl.setDefaultHintTextColor(ColorStateList.valueOf(lightGray));
                    tl.setBoxStrokeColor(white);
                }
            }

            TextInputEditText[] edits = {etLogin, etPass};
            for (TextInputEditText et : edits) {
                if (et != null) et.setTextColor(white);
            }

            if (btnLogin != null) btnLogin.setTextColor(Color.BLACK);

        } else {
            int black = Color.parseColor("#212121");
            int darkGray = Color.parseColor("#757575");
            int lightBg = Color.parseColor("#F4F7F6");
            int accentColor = Color.parseColor("#6200EE");

            if (root != null) root.setBackgroundColor(lightBg);
            window.setStatusBarColor(lightBg);
            insets.setAppearanceLightStatusBars(true);

            if (tvTitle != null) tvTitle.setTextColor(black);
            if (tvGoToReg != null) tvGoToReg.setTextColor(accentColor);

            TextInputLayout[] layouts = {tlLogin, tlPass};
            for (TextInputLayout tl : layouts) {
                if (tl != null) {
                    tl.setHintTextColor(ColorStateList.valueOf(black));
                    tl.setDefaultHintTextColor(ColorStateList.valueOf(darkGray));
                    tl.setBoxStrokeColor(black);
                }
            }

            TextInputEditText[] edits = {etLogin, etPass};
            for (TextInputEditText et : edits) {
                if (et != null) et.setTextColor(black);
            }

            if (btnLogin != null) btnLogin.setTextColor(Color.WHITE);
        }
    }
}