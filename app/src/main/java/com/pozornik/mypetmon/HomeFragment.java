package com.pozornik.mypetmon;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private TextView tvGreeting, tvStatusTitle, tvHealthText, tvActivityText, tvMainPetEmoji;
    private CardView cardStatus, cardHealth, cardActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvGreeting = view.findViewById(R.id.tvGreeting);
        cardStatus = view.findViewById(R.id.cardStatus);
        cardHealth = view.findViewById(R.id.cardHealth);
        cardActivity = view.findViewById(R.id.cardActivity);
        tvStatusTitle = view.findViewById(R.id.tvStatusTitle);
        tvHealthText = view.findViewById(R.id.tvHealthText);
        tvActivityText = view.findViewById(R.id.tvActivityText);


        // Наш новый большой эмодзи
        tvMainPetEmoji = view.findViewById(R.id.tvMainPetEmoji);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);

        // Обновляем текст и аватарку
        tvGreeting.setText("Привет, " + prefs.getString("user_name", "User") + " 👋");
        tvMainPetEmoji.setText(prefs.getString("user_avatar_emoji", "🐶"));

        int lightGray = Color.parseColor("#9E9E9E");
        tvGreeting.setTextColor(lightGray);
        tvStatusTitle.setTextColor(lightGray);
        tvHealthText.setTextColor(lightGray);
        tvActivityText.setTextColor(lightGray);

        String theme = prefs.getString("theme_mode", "day");
        if (theme.equals("night")) {
            cardStatus.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardHealth.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardActivity.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
        } else {
            cardStatus.setCardBackgroundColor(Color.WHITE);
            cardHealth.setCardBackgroundColor(Color.WHITE);
            cardActivity.setCardBackgroundColor(Color.WHITE);


        }

    }

}