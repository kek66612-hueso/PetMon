package com.pozornik.mypetmon;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class AppearanceFragment extends Fragment {

    private View appearanceRootLayout;
    private TextView tvAppearanceTitle, tvThemeLabel;
    private CardView cardThemeSettings;
    private SwitchCompat switchTheme;
    private ImageButton btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appearance, container, false);

        appearanceRootLayout = view.findViewById(R.id.appearanceRootLayout);
        tvAppearanceTitle = view.findViewById(R.id.tvAppearanceTitle);
        tvThemeLabel = view.findViewById(R.id.tvThemeLabel);
        cardThemeSettings = view.findViewById(R.id.cardThemeSettings);
        switchTheme = view.findViewById(R.id.switchTheme);
        btnBack = view.findViewById(R.id.btnBack);

        // 1. Загружаем текущую тему
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String currentTheme = prefs.getString("theme_mode", "day");

        switchTheme.setChecked(currentTheme.equals("night"));
        applyTheme(currentTheme);

        // 2. Кнопка "Назад" - возвращает на предыдущий экран
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 3. Логика переключателя темы
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Блокируем кнопку, чтобы не нажимали дважды
            switchTheme.setEnabled(false);

            String newTheme = isChecked ? "night" : "day";
            prefs.edit().putString("theme_mode", newTheme).apply();

            String userLogin = prefs.getString("user_login", "");
            if (!userLogin.isEmpty()) {
                FirebaseFirestore.getInstance().collection("Users").document(userLogin)
                        .update("theme_mode", newTheme)
                        .addOnSuccessListener(aVoid -> {
                            switchTheme.setEnabled(true);
                            Toast.makeText(getContext(), "Тема синхронизирована! ☁️", Toast.LENGTH_SHORT).show();
                            // Перезагружаем приложение для применения темы
                            requireActivity().recreate();
                        })
                        .addOnFailureListener(e -> {
                            switchTheme.setEnabled(true);
                            switchTheme.setChecked(!isChecked); // Возвращаем ползунок назад при ошибке
                            Toast.makeText(getContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
                        });
            } else {
                switchTheme.setEnabled(true);
                requireActivity().recreate();
            }
        });

        return view;
    }

    private void applyTheme(String theme) {
        if (theme.equals("night")) {
            appearanceRootLayout.setBackgroundColor(Color.parseColor("#121212"));
            tvAppearanceTitle.setTextColor(Color.parseColor("#E0E0E0"));
            cardThemeSettings.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            tvThemeLabel.setTextColor(Color.WHITE);
            btnBack.setColorFilter(Color.parseColor("#E0E0E0"));
        } else {
            appearanceRootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            tvAppearanceTitle.setTextColor(Color.parseColor("#2D3436"));
            cardThemeSettings.setCardBackgroundColor(Color.WHITE);
            tvThemeLabel.setTextColor(Color.parseColor("#2D3436"));
            btnBack.setColorFilter(Color.parseColor("#9E9E9E"));
        }
    }
}