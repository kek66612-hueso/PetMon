package com.pozornik.mypetmon;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private View settingsRootLayout;
    private TextView tvSettingsTitle, tvSettingsName, tvSettingsAvatar;
    private ImageButton btnSearchSettings;
    private LinearLayout profileHeader;

    private CardView cardGeneralSettings, cardAppSettings, cardDangerZone;
    private TextView menuPetInfo, menuAppearance, menuNotifications, menuLogout, menuDeleteData;

    // Слушатель для синхронизации со шторкой (из твоего старого кода)
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Инициализация View
        settingsRootLayout = view.findViewById(R.id.settingsRootLayout);
        tvSettingsTitle = view.findViewById(R.id.tvSettingsTitle);
        tvSettingsName = view.findViewById(R.id.tvSettingsName);
        tvSettingsAvatar = view.findViewById(R.id.tvSettingsAvatar);
        profileHeader = view.findViewById(R.id.profileHeader);
        btnSearchSettings = view.findViewById(R.id.btnSearchSettings);

        cardGeneralSettings = view.findViewById(R.id.cardGeneralSettings);
        cardAppSettings = view.findViewById(R.id.cardAppSettings);
        cardDangerZone = view.findViewById(R.id.cardDangerZone);

        menuPetInfo = view.findViewById(R.id.menuPetInfo);
        menuAppearance = view.findViewById(R.id.menuAppearance);
        menuNotifications = view.findViewById(R.id.menuNotifications);
        menuLogout = view.findViewById(R.id.menuLogout);
        menuDeleteData = view.findViewById(R.id.menuDeleteData);

        // ЗАПРОС РАЗРЕШЕНИЯ НА УВЕДОМЛЕНИЯ (Твой код для Android 13+)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(requireContext(), "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{"android.permission.POST_NOTIFICATIONS"}, 101);
            }
        }

        // Загрузка данных владельца и покраска
        loadOwnerData();
        applyTheme();

        // Проверка таймера при открытии (Твоя логика)
        checkDeletionStatus();

        // --- СИНХРОНИЗАЦИЯ СО ШТОРКОЙ (Твоя логика) ---
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
            prefListener = (sharedPreferences, key) -> {
                if ("account_delete_time".equals(key)) {
                    checkDeletionStatus();
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(prefListener);
        }

        // --- НАВИГАЦИЯ И КЛИКИ ---

        btnSearchSettings.setOnClickListener(v -> {
            // Оставил заглушкой пока не сделаем отдельный UI для поиска в новом дизайне
            Toast.makeText(getContext(), "Поиск скоро вернется...", Toast.LENGTH_SHORT).show();
        });

        menuAppearance.setOnClickListener(v -> {
            // Переход на наш новый экран с тумблером поверх всего приложения
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_root, new AppearanceFragment()) // <-- ТВОЙ НАСТОЯЩИЙ ID
                    .addToBackStack(null)
                    .commit();
        });

        // Заглушки для экранов, которых пока физически не существует
        menuPetInfo.setOnClickListener(v -> Toast.makeText(getContext(), "Открываем данные питомца...", Toast.LENGTH_SHORT).show());
        menuNotifications.setOnClickListener(v -> Toast.makeText(getContext(), "Открываем настройки уведомлений...", Toast.LENGTH_SHORT).show());

        // --- ТВОЯ ЛОГИКА ОПАСНОЙ ЗОНЫ ---

        menuLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Выход из аккаунта")
                    .setMessage("Вы уверены, что хотите выйти?")
                    .setPositiveButton("Выйти", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getActivity(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), RegistrationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });

        menuDeleteData.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
            long deleteTime = prefs.getLong("account_delete_time", 0);

            if (deleteTime > 0) {
                // Если таймер уже запущен, даем возможность отменить прямо отсюда
                new AlertDialog.Builder(getActivity())
                        .setTitle("Отмена удаления")
                        .setMessage("Процесс удаления уже запущен. Хотите отменить его?")
                        .setPositiveButton("Отменить удаление", (dialog, which) -> {
                            prefs.edit().remove("account_delete_time").apply();
                            NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                            if (nm != null) nm.cancel(1001);
                            Toast.makeText(getActivity(), "Удаление отменено", Toast.LENGTH_SHORT).show();
                            checkDeletionStatus();
                        })
                        .setNegativeButton("Закрыть", null)
                        .show();
            } else {
                showDeleteConfirmationDialog();
            }
        });

        return view;
    }

    // --- МЕТОДЫ ОПАСНОЙ ЗОНЫ (ТВОЙ ПОЛНОЦЕННЫЙ КОД) ---

    private long getDeletionDelayMs() {
        return 60 * 1000; // 1 минута
    }

    private void checkDeletionStatus() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        long deleteTime = prefs.getLong("account_delete_time", 0);

        String theme = prefs.getString("theme_mode", "day");
        if (deleteTime > 0) {
            if (System.currentTimeMillis() >= deleteTime) {
                // ВРЕМЯ ПРИШЛО: РЕАЛЬНОЕ УДАЛЕНИЕ ИЗ FIREBASE
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            prefs.edit()
                                    .remove("user_name")
                                    .remove("user_avatar_emoji")
                                    .remove("is_registered")
                                    .remove("account_delete_time")
                                    .apply();
                            Toast.makeText(getActivity(), "Аккаунт навсегда удален", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), RegistrationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "Ошибка удаления. Требуется недавняя авторизация.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                // Таймер запущен - меняем текст кнопки
                menuDeleteData.setText("⏱ Отменить удаление аккаунта");
                menuDeleteData.setTextColor(theme.equals("night") ? Color.parseColor("#E0E0E0") : Color.parseColor("#FF9800"));
            }
        } else {
            // Обычное состояние
            menuDeleteData.setText("Удалить все данные питомца");
            menuDeleteData.setTextColor(theme.equals("night") ? Color.parseColor("#E0E0E0") : Color.parseColor("#D32F2F"));
        }
    }

    private void showDeleteConfirmationDialog() {
        if (getActivity() == null) return;

        new AlertDialog.Builder(getActivity())
                .setTitle("Удаление аккаунта")
                .setMessage("Аккаунт будет удален через 1 минуту. Если вы передумали, нажмите «Я передумал» в уведомлении или «Отменить удаление» в настройках.")
                .setPositiveButton("Удалить", (dialog, which) -> startDeletionProcess())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void startDeletionProcess() {
        if (getActivity() == null) return;

        long timeToDie = System.currentTimeMillis() + getDeletionDelayMs();
        getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE).edit().putLong("account_delete_time", timeToDie).apply();

        checkDeletionStatus();

        NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("PETMON_CHANNEL", "Системные", NotificationManager.IMPORTANCE_HIGH);
            if (nm != null) nm.createNotificationChannel(channel);
        }

        Intent cancelIntent = new Intent(getActivity(), CancelDeletionReceiver.class);
        PendingIntent pendingCancelIntent = PendingIntent.getBroadcast(
                getActivity(), 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "PETMON_CHANNEL")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Аккаунт готов к удалению")
                .setContentText("Процесс запущен. Вы можете отменить его.")
                .addAction(android.R.drawable.ic_menu_revert, "Я ПЕРЕДУМАЛ", pendingCancelIntent)
                .setOngoing(true);

        if (nm != null) nm.notify(1001, builder.build());
        Toast.makeText(getActivity(), "Таймер удаления запущен!", Toast.LENGTH_SHORT).show();
    }

    // --- СТАНДАРТНЫЕ МЕТОДЫ ---

    @Override
    public void onResume() {
        super.onResume();
        checkDeletionStatus();
        loadOwnerData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null && prefListener != null) {
            getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE)
                    .unregisterOnSharedPreferenceChangeListener(prefListener);
        }
    }

    private void loadOwnerData() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        tvSettingsName.setText(prefs.getString("user_name", "Владелец"));
        tvSettingsAvatar.setText(prefs.getString("user_avatar_emoji", "🐶"));
    }

    private void applyTheme() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String theme = prefs.getString("theme_mode", "day");

        if (theme.equals("night")) {
            settingsRootLayout.setBackgroundColor(Color.parseColor("#121212"));
            tvSettingsTitle.setTextColor(Color.parseColor("#E0E0E0"));
            tvSettingsName.setTextColor(Color.WHITE);
            btnSearchSettings.setColorFilter(Color.parseColor("#E0E0E0"));

            cardGeneralSettings.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardAppSettings.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            cardDangerZone.setCardBackgroundColor(Color.parseColor("#1E1E1E"));

            menuPetInfo.setTextColor(Color.WHITE);
            menuAppearance.setTextColor(Color.WHITE);
            menuNotifications.setTextColor(Color.WHITE);
            menuLogout.setTextColor(Color.WHITE);
        } else {
            settingsRootLayout.setBackgroundColor(Color.parseColor("#F4F7F6"));
            tvSettingsTitle.setTextColor(Color.parseColor("#2D3436"));
            tvSettingsName.setTextColor(Color.parseColor("#9E9E9E"));
            btnSearchSettings.setColorFilter(Color.parseColor("#9E9E9E"));

            cardGeneralSettings.setCardBackgroundColor(Color.WHITE);
            cardAppSettings.setCardBackgroundColor(Color.WHITE);
            cardDangerZone.setCardBackgroundColor(Color.WHITE);

            menuPetInfo.setTextColor(Color.parseColor("#2D3436"));
            menuAppearance.setTextColor(Color.parseColor("#2D3436"));
            menuNotifications.setTextColor(Color.parseColor("#2D3436"));
            menuLogout.setTextColor(Color.parseColor("#D32F2F"));
        }
    }
}