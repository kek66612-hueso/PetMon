package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextInputEditText etLoginInput = findViewById(R.id.etLoginInput);
        TextInputEditText etPasswordInput = findViewById(R.id.etPasswordInput);
        TextInputLayout tlLoginInput = findViewById(R.id.tlLoginInput);
        TextInputLayout tlPasswordInput = findViewById(R.id.tlPasswordInput);

        CheckBox cbRememberMe = findViewById(R.id.cbRememberMe);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegistration = findViewById(R.id.tvGoToRegistration);

        // Переход обратно на регистрацию
        tvGoToRegistration.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            finish();
        });

        btnLogin.setOnClickListener(v -> {
            tlLoginInput.setError(null);
            tlPasswordInput.setError(null);

            String login = etLoginInput.getText().toString().trim();
            String password = etPasswordInput.getText().toString();

            if (login.isEmpty()) {
                tlLoginInput.setError("Введите логин");
                return;
            }
            if (password.isEmpty()) {
                tlPasswordInput.setError("Введите пароль");
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setText("Проверка в облаке...");

            // --- МАГИЯ СИНХРОНИЗАЦИИ ---
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Users").document(login)
                    .get()
                    .addOnCompleteListener(task -> {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Войти");

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            // 1. Проверяем, существует ли аккаунт
                            if (document.exists()) {
                                String dbPassword = document.getString("password");

                                // 2. Сверяем пароль
                                if (password.equals(dbPassword)) {

                                    // 3. ДОСТАЕМ НАСТРОЙКИ ИЗ ОБЛАКА
                                    String cloudTheme = document.getString("theme_mode");
                                    String cloudName = document.getString("name");
                                    String cloudAvatar = document.getString("avatar");

                                    // Защита от пустых значений, если вдруг в базе их нет
                                    if (cloudTheme == null) cloudTheme = "day";
                                    if (cloudName == null) cloudName = "Пользователь";
                                    if (cloudAvatar == null) cloudAvatar = "🐶";

                                    // 4. ЖЕСТКО ЗАПИСЫВАЕМ ИХ В ПАМЯТЬ ТЕЛЕФОНА
                                    SharedPreferences.Editor editor = getSharedPreferences("AppConfig", MODE_PRIVATE).edit();
                                    editor.putString("theme_mode", cloudTheme); // Синхронизация темы!
                                    editor.putString("user_name", cloudName);
                                    editor.putString("user_avatar_emoji", cloudAvatar);
                                    editor.putString("user_login", login);
                                    editor.putString("user_password", password);
                                    editor.putBoolean("remember_me", cbRememberMe != null && cbRememberMe.isChecked());
                                    editor.putBoolean("is_registered", true);
                                    editor.apply();

                                    Toast.makeText(LoginActivity.this, "С возвращением, " + cloudName + "!", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    tlPasswordInput.setError("Неверный пароль!");
                                }
                            } else {
                                tlLoginInput.setError("Такого аккаунта не существует!");
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Ошибка сети: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}