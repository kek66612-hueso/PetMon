package com.pozornik.mypetmon;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppConfig";
    private static final String KEY_THEME = "theme_mode";

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNav = findViewById(R.id.bottom_navigation);

        // Настраиваем карусель
        MainPagerAdapter adapter = new MainPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Отключаем лишнюю чувствительность свайпа, чтобы было плавно
        viewPager.setOffscreenPageLimit(3);

        // Стартуем с Главного экрана (индекс 1)
        viewPager.setCurrentItem(1, false);
        bottomNav.setSelectedItemId(R.id.nav_home);

        // Связываем свайпы пальцем с выделением кнопок в нижнем меню
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: bottomNav.getMenu().findItem(R.id.nav_settings).setChecked(true); break;
                    case 1: bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true); break;
                    case 2: bottomNav.getMenu().findItem(R.id.nav_profile).setChecked(true); break;
                }
            }
        });

        // Связываем клики по нижнему меню с пролистыванием карусели
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean useSwipe = prefs.getBoolean("enable_swipe", true);

            if (id == R.id.nav_settings) viewPager.setCurrentItem(0, useSwipe);
            else if (id == R.id.nav_home) viewPager.setCurrentItem(1, useSwipe);
            else if (id == R.id.nav_profile) viewPager.setCurrentItem(2, useSwipe);
            return true;
        });

        applyTheme();
    }

    // Метод перекраски шторки и фона (вызывается в том числе из фрагментов при смене темы)
    public void applyTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String theme = prefs.getString(KEY_THEME, "day");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat insets = new WindowInsetsControllerCompat(window, window.getDecorView());

        if (theme.equals("night")) {
            findViewById(R.id.main_root).setBackgroundColor(Color.parseColor("#121212"));
            bottomNav.setBackgroundColor(Color.parseColor("#121212"));
            window.setStatusBarColor(Color.parseColor("#121212"));
            insets.setAppearanceLightStatusBars(false);
        } else {
            findViewById(R.id.main_root).setBackgroundColor(Color.parseColor("#F4F7F6"));
            bottomNav.setBackgroundColor(Color.WHITE);
            window.setStatusBarColor(Color.parseColor("#F4F7F6"));
            insets.setAppearanceLightStatusBars(true);
        }
    }
}