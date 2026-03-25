package com.pozornik.mypetmon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    private CardView cardTheme, cardAnim;
    private TextView tvThemeDesc, tvAnimDesc, tvSettingsAvatar, tvSettingsName;
    private EditText etSearch;
    private LinearLayout profileHeader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        cardTheme = view.findViewById(R.id.cardTheme);
        cardAnim = view.findViewById(R.id.cardAnim);
        tvThemeDesc = view.findViewById(R.id.tvThemeDesc);
        tvAnimDesc = view.findViewById(R.id.tvAnimDesc);
        etSearch = view.findViewById(R.id.etSearch);
        profileHeader = view.findViewById(R.id.profileHeader);
        tvSettingsAvatar = view.findViewById(R.id.tvSettingsAvatar);
        tvSettingsName = view.findViewById(R.id.tvSettingsName);
        ImageView btnSearch = view.findViewById(R.id.btnSearch);

        view.findViewById(R.id.btnDay).setOnClickListener(v -> changeTheme("day"));
        view.findViewById(R.id.btnNight).setOnClickListener(v -> changeTheme("night"));

        cardAnim.setOnClickListener(v -> startActivity(new Intent(getActivity(), AnimationsActivity.class)));

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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                if (query.isEmpty()) {
                    cardTheme.setVisibility(View.VISIBLE);
                    cardAnim.setVisibility(View.VISIBLE);
                    return;
                }
                cardTheme.setVisibility("тема внешний вид день ночь".contains(query) ? View.VISIBLE : View.GONE);
                cardAnim.setVisibility("анимация анимации запуск эффекты".contains(query) ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        applyCurrentTheme();
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
            tvSettingsAvatar.setText(prefs.getString("user_avatar_emoji", "🐶"));
            tvSettingsName.setText(prefs.getString("user_name", "User"));
        }
    }

    private void changeTheme(String theme) {
        if (getActivity() == null) return;
        getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE).edit().putString("theme_mode", theme).apply();
        applyCurrentTheme();
        ((MainActivity) getActivity()).applyTheme(); // Даем команду главной активности перекрасить фон
        Toast.makeText(getActivity(), "Тема обновлена", Toast.LENGTH_SHORT).show();
    }

    private void applyCurrentTheme() {
        if (getActivity() == null) return;
        int lightGray = Color.parseColor("#9E9E9E");
        tvThemeDesc.setTextColor(lightGray);
        tvAnimDesc.setTextColor(lightGray);
        etSearch.setTextColor(lightGray);
        etSearch.setHintTextColor(lightGray);
        tvSettingsName.setTextColor(lightGray);

        String theme = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE).getString("theme_mode", "day");
        if (theme.equals("night")) {
            cardTheme.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardAnim.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            etSearch.setBackgroundColor(Color.parseColor("#1E1E1E"));
        } else {
            cardTheme.setCardBackgroundColor(Color.WHITE);
            cardAnim.setCardBackgroundColor(Color.WHITE);
            etSearch.setBackgroundColor(Color.parseColor("#E0E0E0"));
        }
    }
}