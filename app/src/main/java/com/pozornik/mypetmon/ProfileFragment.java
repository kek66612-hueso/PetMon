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
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {
    private CardView cardProfile;
    private TextView tvAvatarPreview, tvProfileTitle;
    private TextInputEditText etUserName;
    private String selectedEmoji = "🐶";
    private final String[] emojis = {"🐶", "🐱", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        cardProfile = view.findViewById(R.id.cardProfile);
        tvAvatarPreview = view.findViewById(R.id.tvAvatarPreview);
        etUserName = view.findViewById(R.id.etUserName);
        tvProfileTitle = view.findViewById(R.id.tvProfileTitle);

        view.findViewById(R.id.btnChangePhoto).setOnClickListener(v -> {
            if (getActivity() == null) return;
            new AlertDialog.Builder(getActivity())
                    .setTitle("Выберите аватара")
                    .setItems(emojis, (dialog, which) -> {
                        selectedEmoji = emojis[which];
                        tvAvatarPreview.setText(selectedEmoji);
                    }).show();
        });

        view.findViewById(R.id.btnSaveProfile).setOnClickListener(v -> saveProfileData());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
        applyCurrentTheme();
    }

    private void loadProfileData() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        etUserName.setText(prefs.getString("user_name", ""));
        selectedEmoji = prefs.getString("user_avatar_emoji", "🐶");
        tvAvatarPreview.setText(selectedEmoji);
    }

    private void saveProfileData() {
        if (getActivity() == null) return;
        String newName = etUserName.getText() != null ? etUserName.getText().toString().trim() : "";
        if (newName.length() < 2) {
            Toast.makeText(getActivity(), "Имя должно содержать хотя бы 2 символа", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE).edit();
        editor.putString("user_name", newName);
        editor.putString("user_avatar_emoji", selectedEmoji);
        editor.apply();
        Toast.makeText(getActivity(), "Профиль сохранен!", Toast.LENGTH_SHORT).show();
    }

    private void applyCurrentTheme() {
        if (getActivity() == null) return;
        int lightGray = Color.parseColor("#9E9E9E");
        etUserName.setTextColor(lightGray);
        tvProfileTitle.setTextColor(lightGray);

        String theme = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE).getString("theme_mode", "day");
        if (theme.equals("night")) {
            cardProfile.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
        } else {
            cardProfile.setCardBackgroundColor(Color.WHITE);
        }
    }
}