package com.pozornik.mypetmon;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class AnimationsActivity extends AppCompatActivity {

    private View rootLayout;
    private CardView cardSplashAnim, cardTransitionAnim;
    private Toolbar toolbar;
    private SwitchMaterial switchTransitions;
    private boolean useTransitions = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animations);

        rootLayout = findViewById(R.id.animations_root);
        cardSplashAnim = findViewById(R.id.cardSplashAnim);
        cardTransitionAnim = findViewById(R.id.cardTransitionAnim);
        toolbar = findViewById(R.id.toolbar_animations);
        SeekBar seekBarSplash = findViewById(R.id.seekBarSplash);
        switchTransitions = findViewById(R.id.switchTransitions);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

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
        });

        applyCurrentTheme();
    }

    private void applyCurrentTheme() {
        String theme = getSharedPreferences("AppConfig", MODE_PRIVATE).getString("theme_mode", "day");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat insets = new WindowInsetsControllerCompat(window, window.getDecorView());
        toolbar.setTitleTextColor(Color.parseColor("#9E9E9E"));

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
            window.setStatusBarColor(Color.parseColor("#121212"));
            insets.setAppearanceLightStatusBars(false);
        } else {
            rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            cardSplashAnim.setCardBackgroundColor(Color.WHITE);
            cardTransitionAnim.setCardBackgroundColor(Color.WHITE);
            window.setStatusBarColor(Color.parseColor("#F4F7F6"));
            insets.setAppearanceLightStatusBars(true);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (useTransitions) overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        else overridePendingTransition(0, 0);
    }
}