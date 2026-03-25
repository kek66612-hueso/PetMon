package com.pozornik.mypetmon;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowInsetsControllerCompat;

public class AnimationsActivity extends AppCompatActivity {

    private View rootLayout;
    private CardView cardSplashAnim;
    private Toolbar toolbar;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animations);

        prefs = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        rootLayout = findViewById(R.id.animations_root);
        cardSplashAnim = findViewById(R.id.cardSplashAnim);
        toolbar = findViewById(R.id.toolbar_animations);
        SeekBar seekBarSplash = findViewById(R.id.seekBarSplash);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Загружаем сохраненное состояние (0, 1 или 2)
        int savedState = prefs.getInt("splash_anim_state", 2);

        // Превращаем 0, 1, 2 в проценты для гладкого ползунка (0, 50, 100)
        seekBarSplash.setProgress(savedState * 50);

        // --- ФИЗИКА УМНОГО МАГНИТА ---
        seekBarSplash.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Ничего не делаем во время движения, просто позволяем ручке ехать за пальцем
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Палец отпущен! Смотрим, где сейчас ручка
                int currentProgress = seekBar.getProgress();
                int targetProgress;
                int stateToSave;

                // Решаем, к какой из 3 точек она ближе
                if (currentProgress < 25) {
                    targetProgress = 0;   // Магнитим к "Откл"
                    stateToSave = 0;
                } else if (currentProgress > 75) {
                    targetProgress = 100; // Магнитим к "Вкл"
                    stateToSave = 2;
                } else {
                    targetProgress = 50;  // Магнитим к центру ("Статичная")
                    stateToSave = 1;
                }

                // ПЛАВНАЯ АНИМАЦИЯ (доезжаем до нужной точки)
                ObjectAnimator animation = ObjectAnimator.ofInt(seekBar, "progress", currentProgress, targetProgress);
                animation.setDuration(250); // Скорость доводки
                animation.setInterpolator(new DecelerateInterpolator()); // Замедляется в конце
                animation.start();

                // Сохраняем значение (0, 1 или 2)
                prefs.edit().putInt("splash_anim_state", stateToSave).apply();
            }
        });

        applyCurrentTheme();
    }

    private void applyCurrentTheme() {
        // Мы в Activity, поэтому вызываем getSharedPreferences напрямую
        String theme = getSharedPreferences("AppConfig", Context.MODE_PRIVATE).getString("theme_mode", "day");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat insets = new WindowInsetsControllerCompat(window, window.getDecorView());
        toolbar.setTitleTextColor(Color.parseColor("#9E9E9E"));

        if (theme.equals("night")) {
            rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            cardSplashAnim.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            window.setStatusBarColor(Color.parseColor("#121212"));
            insets.setAppearanceLightStatusBars(false);
        } else {
            rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            cardSplashAnim.setCardBackgroundColor(Color.WHITE);
            window.setStatusBarColor(Color.parseColor("#F4F7F6"));
            insets.setAppearanceLightStatusBars(true);
        }
    }
}