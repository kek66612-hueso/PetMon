package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

// --- ИМПОРТЫ ДЛЯ СТАТУС-БАРА ---
import android.view.Window;
import android.view.WindowManager;
import androidx.core.view.WindowInsetsControllerCompat;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
        // Получаем стейт: 0 (Откл), 1 (Статичная), 2 (Вкл)
        int animState = prefs.getInt("splash_anim_state", 2);
        boolean isRegistered = prefs.getBoolean("is_registered", false);

        // Состояние 0: Полностью отключена
        if (animState == 0 || !isRegistered) {
            goToNextScreen(isRegistered);
            return;
        }

        setContentView(R.layout.activity_splash);

        // --- ПОДГОТОВКА СТАТУС-БАРА ---
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(window, window.getDecorView());

        // Применяем тему к фону и шторке
        if (prefs.getString("theme_mode", "day").equals("night")) {
            findViewById(R.id.splashRoot).setBackgroundColor(Color.parseColor("#121212"));
            window.setStatusBarColor(Color.parseColor("#121212")); // Темная шторка
            windowInsetsController.setAppearanceLightStatusBars(false);
        } else {
            findViewById(R.id.splashRoot).setBackgroundColor(Color.parseColor("#F4F7F6"));
            window.setStatusBarColor(Color.parseColor("#F4F7F6")); // Светлая шторка
            windowInsetsController.setAppearanceLightStatusBars(true);
        }

        TextView tvSplashAvatar = findViewById(R.id.tvSplashAvatar);
        TextView tvHandWave = findViewById(R.id.tvHandWave);
        CurvedTextView tvCurvedGreeting = findViewById(R.id.tvCurvedGreeting);

        tvSplashAvatar.setText(prefs.getString("user_avatar_emoji", "🐶"));
        tvCurvedGreeting.setText("Привет, " + prefs.getString("user_name", "User") + "!");

        // Состояние 1: СТАТИЧНАЯ (показываем сразу финальный результат без движения)
        if (animState == 1) {
            tvSplashAvatar.setTranslationY(0f);
            tvHandWave.setAlpha(1f);
            tvCurvedGreeting.setAlpha(1f);

            // Ждем 1.5 секунды
            new Handler(Looper.getMainLooper()).postDelayed(() -> goToNextScreen(true), 1500);
            return;
        }

        // Состояние 2: ВКЛЮЧЕНА (выполняем анимацию)
        tvSplashAvatar.post(() -> {
            tvSplashAvatar.setTranslationY(1500f);

            android.animation.ObjectAnimator moveUp = android.animation.ObjectAnimator.ofFloat(tvSplashAvatar, "translationY", 1500f, 0f);
            moveUp.setDuration(1000);
            moveUp.setInterpolator(new android.view.animation.OvershootInterpolator(1.2f));

            android.animation.ObjectAnimator handAlpha = android.animation.ObjectAnimator.ofFloat(tvHandWave, "alpha", 0f, 1f);
            android.animation.ObjectAnimator textAlpha = android.animation.ObjectAnimator.ofFloat(tvCurvedGreeting, "alpha", 0f, 1f);
            handAlpha.setDuration(400);
            textAlpha.setDuration(400);

            tvHandWave.setPivotX(tvHandWave.getWidth() * 0.8f);
            tvHandWave.setPivotY(tvHandWave.getHeight() * 1.0f);

            android.animation.ObjectAnimator handWave = android.animation.ObjectAnimator.ofFloat(tvHandWave, "rotation",
                    0f, 35f, -15f, 25f, -5f, 10f, 0f);
            handWave.setDuration(1400);

            android.animation.AnimatorSet animatorSet = new android.animation.AnimatorSet();
            animatorSet.play(moveUp);
            animatorSet.play(handAlpha).with(textAlpha).with(handWave).after(moveUp);
            animatorSet.start();
        });

        // При полной анимации ждем 3 секунды
        new Handler(Looper.getMainLooper()).postDelayed(() -> goToNextScreen(true), 3000);
    }

    private void goToNextScreen(boolean isRegistered) {
        Intent intent = new Intent(SplashActivity.this, isRegistered ? MainActivity.class : RegistrationActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}