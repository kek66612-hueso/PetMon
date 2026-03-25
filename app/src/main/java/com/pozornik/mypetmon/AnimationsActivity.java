package com.pozornik.mypetmon;

<<<<<<< Updated upstream
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
=======
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
>>>>>>> Stashed changes
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
<<<<<<< Updated upstream
=======
import android.view.animation.DecelerateInterpolator;
>>>>>>> Stashed changes
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowInsetsControllerCompat;

<<<<<<< Updated upstream
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AnimationsActivity extends AppCompatActivity {

    private View rootLayout;
    private CardView cardSplashAnim, cardTransitionAnim;
    private Toolbar toolbar;
    private SwitchMaterial switchTransitions;
    private boolean useTransitions = true;
=======
public class AnimationsActivity extends AppCompatActivity {

    private View rootLayout;
    private CardView cardSplashAnim;
    private Toolbar toolbar;
    private SharedPreferences prefs;
>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animations);

<<<<<<< Updated upstream
        rootLayout = findViewById(R.id.animations_root);
        cardSplashAnim = findViewById(R.id.cardSplashAnim);
        cardTransitionAnim = findViewById(R.id.cardTransitionAnim);
        toolbar = findViewById(R.id.toolbar_animations);
        SeekBar seekBarSplash = findViewById(R.id.seekBarSplash);
        switchTransitions = findViewById(R.id.switchTransitions);
=======
        prefs = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        rootLayout = findViewById(R.id.animations_root);
        cardSplashAnim = findViewById(R.id.cardSplashAnim);
        toolbar = findViewById(R.id.toolbar_animations);
        SeekBar seekBarSplash = findViewById(R.id.seekBarSplash);
>>>>>>> Stashed changes

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

<<<<<<< Updated upstream
        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
        useTransitions = prefs.getBoolean("enable_transitions", true);

        // Ползунок стартовой анимации
        seekBarSplash.setProgress(prefs.getInt("splash_anim_state", 2));
        seekBarSplash.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt("splash_anim_state", progress).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Выключатель анимаций переходов
        switchTransitions.setChecked(useTransitions);
        switchTransitions.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("enable_transitions", isChecked).apply();
            useTransitions = isChecked;
=======


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
                animation.setDuration(250); // Скорость доводки (четверть секунды)
                animation.setInterpolator(new DecelerateInterpolator()); // Замедляется в конце (эффект тяжелой ручки)
                animation.start();

                // Сохраняем наше привычное значение (0, 1 или 2)
                prefs.edit().putInt("splash_anim_state", stateToSave).apply();
            }
>>>>>>> Stashed changes
        });

        applyCurrentTheme();
    }

    private void applyCurrentTheme() {
<<<<<<< Updated upstream
        String theme = getSharedPreferences("AppConfig", MODE_PRIVATE).getString("theme_mode", "day");
=======
        // Мы в Activity, поэтому просто вызываем getSharedPreferences напрямую
        String theme = getSharedPreferences("AppConfig", Context.MODE_PRIVATE).getString("theme_mode", "day");
>>>>>>> Stashed changes

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat insets = new WindowInsetsControllerCompat(window, window.getDecorView());
        toolbar.setTitleTextColor(Color.parseColor("#9E9E9E"));

<<<<<<< Updated upstream
        // Кастомный ползунок
        int thumbColor = theme.equals("night") ? Color.WHITE : Color.BLACK;
        int trackColor = theme.equals("night") ? Color.parseColor("#555555") : Color.parseColor("#CCCCCC");
        ColorStateList thumbStates = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{thumbColor, Color.parseColor("#9E9E9E")});
        ColorStateList trackStates = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{trackColor, Color.parseColor("#E0E0E0")});

        switchTransitions.setThumbTintList(thumbStates);
        switchTransitions.setTrackTintList(trackStates);

        if (theme.equals("night")) {
            rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            cardSplashAnim.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardTransitionAnim.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
=======
        if (theme.equals("night")) {
            rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            cardSplashAnim.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
>>>>>>> Stashed changes
            window.setStatusBarColor(Color.parseColor("#121212"));
            insets.setAppearanceLightStatusBars(false);
        } else {
            rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            cardSplashAnim.setCardBackgroundColor(Color.WHITE);
<<<<<<< Updated upstream
            cardTransitionAnim.setCardBackgroundColor(Color.WHITE);
=======
>>>>>>> Stashed changes
            window.setStatusBarColor(Color.parseColor("#F4F7F6"));
            insets.setAppearanceLightStatusBars(true);
        }
    }
<<<<<<< Updated upstream

    @Override
    public void finish() {
        super.finish();
        if (useTransitions) overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        else overridePendingTransition(0, 0);
    }
=======
>>>>>>> Stashed changes
}