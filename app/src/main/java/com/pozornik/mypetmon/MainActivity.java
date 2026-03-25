package com.pozornik.mypetmon;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
<<<<<<< Updated upstream
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import android.view.Window;
import android.view.WindowManager;
import androidx.core.view.WindowInsetsControllerCompat;

=======
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
>>>>>>> Stashed changes
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppConfig";
    private static final String KEY_THEME = "theme_mode";

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;

<<<<<<< Updated upstream
    private CardView cardStatus, cardHealth, cardActivity;
    private TextView tvStatusTitle, tvHealthText, tvActivityText;

    private GestureDetector gestureDetector;
    private boolean useSwipe = true;
    private boolean useTransitions = true;

=======
>>>>>>> Stashed changes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNav = findViewById(R.id.bottom_navigation);

<<<<<<< Updated upstream
        cardStatus = findViewById(R.id.cardStatus);
        cardHealth = findViewById(R.id.cardHealth);
        cardActivity = findViewById(R.id.cardActivity);
        tvStatusTitle = findViewById(R.id.tvStatusTitle);
        tvHealthText = findViewById(R.id.tvHealthText);
        tvActivityText = findViewById(R.id.tvActivityText);

        getAndLogFirebaseToken();

        // --- НАСТРОЙКА СВАЙПОВ ---
        gestureDetector = new GestureDetector(this, new SwipeHelper(new SwipeHelper.SwipeListener() {
            @Override
            public void onSwipeLeft() {
                // Свайп влево (палец идет влево) -> Переходим в Профиль (он справа)
                if (useSwipe) navigateTo(ProfileActivity.class, R.id.nav_profile, true);
            }

            @Override
            public void onSwipeRight() {
                // Свайп вправо (палец идет вправо) -> Переходим в Настройки (они слева)
                if (useSwipe) navigateTo(SettingsActivity.class, R.id.nav_settings, false);
            }
        }));
=======
        // Настраиваем карусель
        MainPagerAdapter adapter = new MainPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Отключаем лишнюю чувствительность свайпа, чтобы было плавно
        viewPager.setOffscreenPageLimit(3);

        // Стартуем с Главного экрана (индекс 1)
        viewPager.setCurrentItem(1, false);
        bottomNav.setSelectedItemId(R.id.nav_home);
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_settings) {
                navigateTo(SettingsActivity.class, R.id.nav_settings, false);
                return true;
            } else if (id == R.id.nav_profile) {
                navigateTo(ProfileActivity.class, R.id.nav_profile, true);
                return true;
            }
            return false;
        });

        updateUI();
    }

    // Перехватываем касания экрана для свайпов
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useSwipe = prefs.getBoolean("enable_swipe", true);
        useTransitions = prefs.getBoolean("enable_transitions", true);
        if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_home);
    }

    // Умный метод для переходов с анимацией
    private void navigateTo(Class<?> targetActivity, int navId, boolean movingRightward) {
        startActivity(new Intent(this, targetActivity));
        if (useTransitions) {
            if (movingRightward) {
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Едем вправо
            } else {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // Едем влево
            }
        } else {
            overridePendingTransition(0, 0); // Без анимации
        }
        finish();
    }

    private void getAndLogFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d("FCM_TOKEN", task.getResult());
                });
    }

    private void updateUI() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        tvGreeting.setText("Привет, " + prefs.getString(KEY_USER_NAME, "User") + " 👋");

        int lightGray = Color.parseColor("#9E9E9E");
        tvGreeting.setTextColor(lightGray);
        tvStatusTitle.setTextColor(lightGray);
        tvHealthText.setTextColor(lightGray);
        tvActivityText.setTextColor(lightGray);

        String theme = prefs.getString(KEY_THEME, "day");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(window, window.getDecorView());
=======
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
>>>>>>> Stashed changes

        if (theme.equals("night")) {
            findViewById(R.id.main_root).setBackgroundColor(Color.parseColor("#121212"));
            bottomNav.setBackgroundColor(Color.parseColor("#121212"));
<<<<<<< Updated upstream
            cardStatus.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardHealth.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardActivity.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            window.setStatusBarColor(Color.parseColor("#121212"));
            windowInsetsController.setAppearanceLightStatusBars(false);
=======
            window.setStatusBarColor(Color.parseColor("#121212"));
            insets.setAppearanceLightStatusBars(false);
>>>>>>> Stashed changes
        } else {
            findViewById(R.id.main_root).setBackgroundColor(Color.parseColor("#F4F7F6"));
            bottomNav.setBackgroundColor(Color.WHITE);
<<<<<<< Updated upstream
            cardStatus.setCardBackgroundColor(Color.WHITE);
            cardHealth.setCardBackgroundColor(Color.WHITE);
            cardActivity.setCardBackgroundColor(Color.WHITE);
            window.setStatusBarColor(Color.parseColor("#F4F7F6"));
            windowInsetsController.setAppearanceLightStatusBars(true);
=======
            window.setStatusBarColor(Color.parseColor("#F4F7F6"));
            insets.setAppearanceLightStatusBars(true);
>>>>>>> Stashed changes
        }
    }
}