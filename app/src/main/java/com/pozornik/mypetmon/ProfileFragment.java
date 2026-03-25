package com.pozornik.mypetmon;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileFragment extends Fragment {

    private TextView tvProfileTitle, tvProfileAvatar, tvProfileName;
    private View rootLayout;

    // Временные переменные для диалогового окна
    private String tempEmoji = "🐶";
    private final String[] emojis = {"🐶", "🐱", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        rootLayout = view.findViewById(R.id.profile_root);
        tvProfileTitle = view.findViewById(R.id.tvProfileTitle);
        tvProfileAvatar = view.findViewById(R.id.tvProfileAvatar);
        tvProfileName = view.findViewById(R.id.tvProfileName);

        Button btnEditProfile = view.findViewById(R.id.btnEditProfile);
        Button btnGoToSettings = view.findViewById(R.id.btnGoToSettings);


        // КНОПКА 1: Открываем красивое окно редактирования
        btnEditProfile.setOnClickListener(v -> showEditDialog());

        // КНОПКА 2: Шорткат в настройки
        btnGoToSettings.setOnClickListener(v -> {
            if (getActivity() != null) {
                // Находим нижнее меню в главной активности и программно "нажимаем" на кнопку настроек
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_settings);
                }
// ЗАПРОС РАЗРЕШЕНИЯ НА УВЕДОМЛЕНИЯ (ДЛЯ ANDROID 13+)
                if (Build.VERSION.SDK_INT >= 33) {
                    if (ContextCompat.checkSelfPermission(requireContext(), "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{"android.permission.POST_NOTIFICATIONS"}, 101);
                    }
                }

            }
        });

// Загружаем тему и профиль до того, как экран покажется пользователю
        applyCurrentTheme();
        loadProfileData();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
        applyCurrentTheme();
    }

    // Загружаем данные для "Визитки"
    private void loadProfileData() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        tvProfileName.setText(prefs.getString("user_name", "User"));
        tvProfileAvatar.setText(prefs.getString("user_avatar_emoji", "🐶"));
    }

    // Создаем и показываем наше всплывающее окно
    private void showEditDialog() {
        if (getActivity() == null) return;

        SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        tempEmoji = prefs.getString("user_avatar_emoji", "🐶");
        String currentName = prefs.getString("user_name", "");

        // Надуваем дизайн нашего окна
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);

        TextView tvDialogAvatar = dialogView.findViewById(R.id.tvDialogAvatar);
        EditText etDialogName = dialogView.findViewById(R.id.etDialogName);
        Button btnDialogSave = dialogView.findViewById(R.id.btnDialogSave);
        RelativeLayout avatarContainer = dialogView.findViewById(R.id.avatarContainer);

        // Устанавливаем текущие данные
        tvDialogAvatar.setText(tempEmoji);
        etDialogName.setText(currentName);

        // Создаем само окно с прозрачным фоном (чтобы работали скругленные края из XML)
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Клик по аватарке внутри окна открывает выбор эмодзи
        avatarContainer.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Выберите аватара")
                    .setItems(emojis, (d, which) -> {
                        tempEmoji = emojis[which];
                        tvDialogAvatar.setText(tempEmoji);
                    }).show();
        });

        // Клик по кнопке "Сохранить" внутри окна
        btnDialogSave.setOnClickListener(v -> {
            String newName = etDialogName.getText() != null ? etDialogName.getText().toString().trim() : "";
            if (newName.length() < 2) {
                Toast.makeText(getActivity(), "Имя должно содержать хотя бы 2 символа", Toast.LENGTH_SHORT).show();
                return;
            }

            // Сохраняем новые данные
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_name", newName);
            editor.putString("user_avatar_emoji", tempEmoji);
            editor.apply();

            Toast.makeText(getActivity(), "Профиль обновлен!", Toast.LENGTH_SHORT).show();

            // Обновляем визитку на экране и закрываем окно
            loadProfileData();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void applyCurrentTheme() {
        if (getActivity() == null) return;
        String theme = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE).getString("theme_mode", "day");

        if (theme.equals("night")) {
            tvProfileTitle.setTextColor(Color.parseColor("#9E9E9E"));
            tvProfileName.setTextColor(Color.WHITE);
        } else {
            tvProfileTitle.setTextColor(Color.parseColor("#9E9E9E"));
            tvProfileName.setTextColor(Color.parseColor("#333333"));
        }
    }
}