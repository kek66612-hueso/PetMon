package com.pozornik.mypetmon;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {

    private TextView tvHomePetAvatar, tvHomePetName, tvHomePetStatus, tvNextProcedure;
    private TextView tvHomeTitleMain, tvHomeTitleEvents;
    private FloatingActionButton fabAddRecord;
    private CardView cardPetMain;
    private View rootLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rootLayout = view; // Сохраняем весь экран, чтобы менять ему фон

        tvHomePetAvatar = view.findViewById(R.id.tvHomePetAvatar);
        tvHomePetName = view.findViewById(R.id.tvHomePetName);
        tvHomePetStatus = view.findViewById(R.id.tvHomePetStatus);
        tvNextProcedure = view.findViewById(R.id.tvNextProcedure);
        fabAddRecord = view.findViewById(R.id.fabAddRecord);
        cardPetMain = view.findViewById(R.id.cardPetMain);

        // Наши новые ID для заголовков
        tvHomeTitleMain = view.findViewById(R.id.tvHomeTitleMain);
        tvHomeTitleEvents = view.findViewById(R.id.tvHomeTitleEvents);

        updateUI();

        fabAddRecord.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Добавление новой записи здоровья...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void updateUI() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);

        // ИСПРАВЛЕНИЕ 1: Теперь ищем pet_name, а не user_name
        String petName = prefs.getString("pet_name", "Имя не задано");
        String avatar = prefs.getString("pet_avatar_emoji", "🐾");

        tvHomePetName.setText(petName);
        tvHomePetAvatar.setText(avatar);

        // ИСПРАВЛЕНИЕ 2: Проверяем тему и красим экран
        String theme = prefs.getString("theme_mode", "day");

        if (theme.equals("night")) {
            // Тёмная тема
            rootLayout.setBackgroundColor(Color.parseColor("#121212"));
            cardPetMain.setCardBackgroundColor(Color.parseColor("#1E1E1E"));

            tvHomeTitleMain.setTextColor(Color.parseColor("#E0E0E0"));
            tvHomeTitleEvents.setTextColor(Color.parseColor("#E0E0E0"));
            tvHomePetName.setTextColor(Color.WHITE);
            tvHomePetStatus.setTextColor(Color.parseColor("#BDBDBD"));
        } else {
            // Светлая тема
            rootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            cardPetMain.setCardBackgroundColor(Color.WHITE);

            tvHomeTitleMain.setTextColor(Color.parseColor("#2D3436"));
            tvHomeTitleEvents.setTextColor(Color.parseColor("#2D3436"));
            tvHomePetName.setTextColor(Color.parseColor("#2D3436"));
            tvHomePetStatus.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(); // Обновляем экран каждый раз, когда возвращаемся на него
    }
}