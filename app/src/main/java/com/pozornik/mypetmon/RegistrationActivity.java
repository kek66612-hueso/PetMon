package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import android.view.Window;
import android.view.WindowManager;
import androidx.core.view.WindowInsetsControllerCompat;

public class RegistrationActivity extends AppCompatActivity {

    private String selectedEmoji = "🐶"; // По умолчанию
    private TextView tvSelectedEmoji;

    // Список животных на выбор
    private final String[] emojis = {"🐶", "🐱", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

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

            // Проверка на длину имени
            if (name.length() < 2) {
                Toast.makeText(this, "Имя должно содержать хотя бы 2 символа", Toast.LENGTH_SHORT).show();
                return;
            }


            // Сохраняем данные
            SharedPreferences.Editor editor = getSharedPreferences("AppConfig", MODE_PRIVATE).edit();
            editor.putString("user_name", name);
            editor.putString("user_avatar_emoji", selectedEmoji);
            editor.putBoolean("is_registered", true); // Флаг, что рега пройдена
            editor.apply();

            // Идем на главную
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
        });
    }
}