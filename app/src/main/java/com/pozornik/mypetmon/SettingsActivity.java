package com.pozornik.mypetmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppConfig";
    private static final String KEY_THEME = "theme_mode";

    private View rootLayout;
    private Toolbar toolbar;
    private CardView cardTheme, cardAnim, cardSwipe;
    private TextView tvThemeDesc, tvAnimDesc;
    private BottomNavigationView bottomNav;
    private EditText etSearch;
    private LinearLayout profileHeader;
    private SwitchMaterial switchSwipe;

    private GestureDetector gestureDetector;
    private boolean useSwipe = true;
    private boolean useTransitions = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rootLayout = findViewById(R.id.settings_root);
        toolbar = findViewById(R.id.toolbar_settings);
        cardTheme = findViewById(R.id.cardTheme);
        cardAnim = findViewById(R.id.cardAnim);
        cardSwipe = findViewById(R.id.cardSwipe);
        tvThemeDesc = findViewById(R.id.tvThemeDesc);
        tvAnimDesc = findViewById(R.id.tvAnimDesc);
        bottomNav = findViewById(R.id.bottom_navigation);
        etSearch = findViewById(R.id.etSearch);
        profileHeader = findViewById(R.id.profileHeader);
        switchSwipe = findViewById(R.id.switchSwipe);
        ImageView btnSearch = findViewById(R.id.btnSearch);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useSwipe = prefs.getBoolean("enable_swipe", true);
        useTransitions = prefs.getBoolean("enable_transitions", true);

        TextView tvSettingsAvatar = findViewById(R.id.tvSettingsAvatar);
        TextView tvSettingsName = findViewById(R.id.tvSettingsName);
        tvSettingsAvatar.setText(prefs.getString("user_avatar_emoji", "🐶"));
        tvSettingsName.setText(prefs.getString("user_name", "User"));

        // Логика нового переключателя
        switchSwipe.setChecked(useSwipe);
        switchSwipe.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("enable_swipe", isChecked).apply();
            useSwipe = isChecked;
        });

        // Свайпы (из Настроек можно свайпнуть только на Главную)
        gestureDetector = new GestureDetector(this, new SwipeHelper(new SwipeHelper.SwipeListener() {
            @Override
            public void onSwipeLeft() {
                if (useSwipe) navigateTo(MainActivity.class, R.id.nav_home, true);
            }
            @Override
            public void onSwipeRight() {} // Дальше настроек ехать некуда
        }));

        findViewById(R.id.btnDay).setOnClickListener(v -> changeTheme("day"));
        findViewById(R.id.btnNight).setOnClickListener(v -> changeTheme("night"));

        cardAnim.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, AnimationsActivity.class));
            if (useTransitions) overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            else overridePendingTransition(0, 0);
        });

        btnSearch.setOnClickListener(v -> {
            if (etSearch.getVisibility() == View.GONE) {
                etSearch.setVisibility(View.VISIBLE);
                profileHeader.setVisibility(View.GONE);
                etSearch.requestFocus();
            } else {
                etSearch.setVisibility(View.GONE);
                profileHeader.setVisibility(View.VISIBLE);
                etSearch.setText("");
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                if (query.isEmpty()) {
                    cardTheme.setVisibility(View.VISIBLE);
                    cardAnim.setVisibility(View.VISIBLE);
                    cardSwipe.setVisibility(View.VISIBLE);
                    return;
                }
                cardTheme.setVisibility("тема внешний вид день ночь".contains(query) ? View.VISIBLE : View.GONE);
                cardAnim.setVisibility("анимация анимации запуск эффекты".contains(query) ? View.VISIBLE : View.GONE);
                cardSwipe.setVisibility("свайп перемещение перелистывание".contains(query) ? View.VISIBLE : View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        bottomNav.setSelectedItemId(R.id.nav_settings);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_settings) return true;
            if (id == R.id.nav_home) {
                navigateTo(MainActivity.class, R.id.nav_home, true);
                return true;
            } else if (id == R.id.nav_profile) {
                navigateTo(ProfileActivity.class, R.id.nav_profile, true);
                return true;
            }
            return false;
        });

        applyCurrentTheme();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void navigateTo(Class<?> targetActivity, int navId, boolean movingRightward) {
        startActivity(new Intent(this, targetActivity));
        if (useTransitions) {
            if (movingRightward) overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            else overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else overridePendingTransition(0, 0);
        super.finish(); // Используем super, чтобы не зациклить наш переопределенный finish()
    }

    private void changeTheme(String theme) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(KEY_THEME, theme).apply();
        applyCurrentTheme();
        Toast.makeText(this, "Тема обновлена", Toast.LENGTH_SHORT).show();
    }

    private void applyCurrentTheme() {
        int lightGray = Color.parseColor("#9E9E9E");
        toolbar.setTitleTextColor(lightGray);
        tvThemeDesc.setTextColor(lightGray);
        tvAnimDesc.setTextColor(lightGray);
        etSearch.setTextColor(lightGray);
        etSearch.setHintTextColor(lightGray);

        String theme = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_THEME, "day");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        WindowInsetsControllerCompat insets = new WindowInsetsControllerCompat(window, window.getDecorView());

<<<<<<< Updated upstream
        // Красим кастомный ползунок (Черный на светлом, Белый на темном)
=======
// Красим кастомный ползунок (Черный на светлом, Белый на темном)
>>>>>>> Stashed changes
        int thumbColor = theme.equals("night") ? Color.WHITE : Color.BLACK;
        int trackColor = theme.equals("night") ? Color.parseColor("#555555") : Color.parseColor("#CCCCCC");
        ColorStateList thumbStates = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{thumbColor, Color.parseColor("#9E9E9E")});
        ColorStateList trackStates = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{trackColor, Color.parseColor("#E0E0E0")});

        switchSwipe.setThumbTintList(thumbStates);
        switchSwipe.setTrackTintList(trackStates);

        if (theme.equals("night")) {
            rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            bottomNav.setBackgroundColor(Color.parseColor("#121212"));
            cardTheme.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardAnim.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardSwipe.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            etSearch.setBackgroundColor(Color.parseColor("#1E1E1E"));
            window.setStatusBarColor(Color.parseColor("#121212"));
            insets.setAppearanceLightStatusBars(false);
        } else {
            rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            bottomNav.setBackgroundColor(Color.WHITE);
            cardTheme.setCardBackgroundColor(Color.WHITE);
            cardAnim.setCardBackgroundColor(Color.WHITE);
            cardSwipe.setCardBackgroundColor(Color.WHITE);
            etSearch.setBackgroundColor(Color.parseColor("#E0E0E0"));
            window.setStatusBarColor(Color.parseColor("#F4F7F6"));
            insets.setAppearanceLightStatusBars(true);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (useTransitions) overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        else overridePendingTransition(0, 0);
    }
}