package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import android.content.res.ColorStateList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.textfield.TextInputEditText;

public class RegistrationActivity extends AppCompatActivity {

    private String selectedEmoji = "🐶"; // По умолчанию
    private TextView tvSelectedEmoji;

    // Список животных на выбор
    private final String[] emojis = {"🐶", "🐱", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- 1. ПРОВЕРКА ПЕРВОГО ЗАПУСКА И СИСТЕМНОЙ ТЕМЫ ---
        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
        if (!prefs.contains("theme_mode")) {
            // Узнаем системную тему, если запускаем вообще в первый раз
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            String defaultTheme = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) ? "night" : "day";
            prefs.edit().putString("theme_mode", defaultTheme).apply();
        }

        // --- 2. СНАЧАЛА ЗАГРУЖАЕМ РАЗМЕТКУ ---
        setContentView(R.layout.activity_registration);

        // --- 3. ПОТОМ КРАСИМ ЕЁ В НУЖНУЮ ТЕМУ ---
        applyTheme();

        tvSelectedEmoji = findViewById(R.id.tvSelectedEmoji);
        Button btnSelectEmoji = findViewById(R.id.btnSelectEmoji);
        Button btnStart = findViewById(R.id.btnStart);
        TextInputEditText etNameInput = findViewById(R.id.etNameInput);

        // Диалог выбора смайлика
        btnSelectEmoji.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Выберите аватара")
                    .setItems(emojis, (dialog, which) -> {
                        selectedEmoji = emojis[which];
                        tvSelectedEmoji.setText(selectedEmoji);
                    })
                    .show();
        });

        // Сохранение и переход на главный экран
        btnStart.setOnClickListener(v -> {
            String name = etNameInput.getText().toString().trim();

            if (name.length() < 2) {
                Toast.makeText(this, "Имя должно содержать хотя бы 2 символа", Toast.LENGTH_SHORT).show();
                return;
            }

            // Сохраняем данные
            SharedPreferences.Editor editor = getSharedPreferences("AppConfig", MODE_PRIVATE).edit();
            editor.putString("user_name", name);
            editor.putString("user_avatar_emoji", selectedEmoji);
            editor.putBoolean("is_registered", true);
            editor.apply();

            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
        });
    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
        String theme = prefs.getString("theme_mode", "day");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat insets = new WindowInsetsControllerCompat(window, window.getDecorView());

        // 1. Корневой фон
        View root = findViewById(R.id.reg_root);

        // 2. Все текстовые элементы
        TextView tvTitle = findViewById(R.id.tvRegTitle);
        TextView tvEmojiLabel = findViewById(R.id.tvEmojiLabel);
        TextView tvEmoji = findViewById(R.id.tvSelectedEmoji);

        // 3. Поле ввода и его обертка
        TextInputLayout tlInput = findViewById(R.id.tlNameInput);
        TextInputEditText etInput = findViewById(R.id.etNameInput);

        // 4. Кнопки
        Button btnSelect = findViewById(R.id.btnSelectEmoji);
        Button btnStart = findViewById(R.id.btnStart);

        if (theme.equals("night")) {
            // ================= ТЕМНАЯ ТЕМА =================
            int white = Color.parseColor("#FFFFFF");
            int lightGray = Color.parseColor("#BDBDBD");
            int darkBg = Color.parseColor("#121212");

            if (root != null) root.setBackgroundColor(darkBg);
            window.setStatusBarColor(darkBg);
            insets.setAppearanceLightStatusBars(false);

            // Красим обычные тексты
            if (tvTitle != null) tvTitle.setTextColor(white);
            if (tvEmojiLabel != null) tvEmojiLabel.setTextColor(lightGray);
            if (tvEmoji != null) tvEmoji.setTextColor(white);

            // Красим поле ввода
            if (tlInput != null) {
                tlInput.setHintTextColor(ColorStateList.valueOf(white));
                tlInput.setDefaultHintTextColor(ColorStateList.valueOf(lightGray));
                tlInput.setBoxStrokeColor(white);
                tlInput.setPlaceholderTextColor(ColorStateList.valueOf(lightGray));
            }
            if (etInput != null) etInput.setTextColor(white);

            // Красим кнопки
            if (btnSelect != null) btnSelect.setTextColor(white);
            if (btnStart != null) btnStart.setTextColor(Color.BLACK); // На кнопке старта лучше черный для контраста

        } else {
            // ================= СВЕТЛАЯ ТЕМА =================
            int black = Color.parseColor("#212121");
            int darkGray = Color.parseColor("#757575");
            int lightBg = Color.parseColor("#F4F7F6");

            if (root != null) root.setBackgroundColor(lightBg);
            window.setStatusBarColor(lightBg);
            insets.setAppearanceLightStatusBars(true);

            // Красим обычные тексты
            if (tvTitle != null) tvTitle.setTextColor(black);
            if (tvEmojiLabel != null) tvEmojiLabel.setTextColor(darkGray);
            if (tvEmoji != null) tvEmoji.setTextColor(black);

            // Красим поле ввода
            if (tlInput != null) {
                tlInput.setHintTextColor(ColorStateList.valueOf(black));
                tlInput.setDefaultHintTextColor(ColorStateList.valueOf(darkGray));
                tlInput.setBoxStrokeColor(black);
            }
            if (etInput != null) etInput.setTextColor(black);

            // Красим кнопки
            if (btnSelect != null) btnSelect.setTextColor(black);
            if (btnStart != null) btnStart.setTextColor(Color.WHITE);
        }
    }
}