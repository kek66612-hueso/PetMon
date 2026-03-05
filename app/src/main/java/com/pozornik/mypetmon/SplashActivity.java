package com.pozornik.mypetmon;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
        boolean showSplash = prefs.getBoolean("show_splash_anim", true); // По умолчанию вкл
        boolean isRegistered = prefs.getBoolean("is_registered", false);

        // Если анимация выключена ИЛИ мы еще не зарегистрировались — пропускаем анимацию
        if (!showSplash || !isRegistered) {
            goToNextScreen(isRegistered);
            return;
        }

        setContentView(R.layout.activity_splash);

        // Применяем темную тему, если она включена
        if (prefs.getString("theme_mode", "day").equals("night")) {
            findViewById(R.id.splashRoot).setBackgroundColor(Color.parseColor("#121212"));
        }

        TextView tvSplashAvatar = findViewById(R.id.tvSplashAvatar);
        TextView tvHandWave = findViewById(R.id.tvHandWave);
        CurvedTextView tvCurvedGreeting = findViewById(R.id.tvCurvedGreeting);

        // Оборачиваем анимацию в .post(), чтобы получить точные размеры элементов после их создания
        tvSplashAvatar.post(() -> {

            // Прячем аватара вниз за экран
            tvSplashAvatar.setTranslationY(1500f);

            // 1. Анимация: Аватар вылетает снизу с эффектом "пружинки"
            ObjectAnimator moveUp = ObjectAnimator.ofFloat(tvSplashAvatar, "translationY", 1500f, 0f);
            moveUp.setDuration(1000);
            moveUp.setInterpolator(new OvershootInterpolator(1.2f));

            // 2. Анимация: Плавное появление руки и текста
            ObjectAnimator handAlpha = ObjectAnimator.ofFloat(tvHandWave, "alpha", 0f, 1f);
            ObjectAnimator textAlpha = ObjectAnimator.ofFloat(tvCurvedGreeting, "alpha", 0f, 1f);
            handAlpha.setDuration(400);
            textAlpha.setDuration(400);

            // 3. Исправленный взмах руки
            // Устанавливаем ось вращения точно в "запястье" (80% ширины, 100% высоты)
            tvHandWave.setPivotX(tvHandWave.getWidth() * 0.8f);
            tvHandWave.setPivotY(tvHandWave.getHeight() * 1.0f);

            // Естественная затухающая амплитуда
            ObjectAnimator handWave = ObjectAnimator.ofFloat(tvHandWave, "rotation",
                    0f, 35f, -15f, 25f, -5f, 10f, 0f);
            handWave.setDuration(1400); // Чуть медленнее и плавнее

            // Запускаем всё по очереди
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(moveUp);
            // Рука появляется и начинает махать СРАЗУ после того, как аватар приземлился
            animatorSet.play(handAlpha).with(textAlpha).with(handWave).after(moveUp);
            animatorSet.start();
        });

        // Через 3 секунды переходим на главный экран
        new Handler(Looper.getMainLooper()).postDelayed(() -> goToNextScreen(true), 3000);
    }

    private void goToNextScreen(boolean isRegistered) {
        Intent intent = new Intent(SplashActivity.this, isRegistered ? MainActivity.class : RegistrationActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}