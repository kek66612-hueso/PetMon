package com.pozornik.mypetmon;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    private CardView cardTheme, cardAnim, cardDangerZone;
    private TextView tvThemeDesc, tvAnimDesc, tvSettingsAvatar, tvSettingsName;
    private EditText etSearch;
    private LinearLayout profileHeader;

    // Кнопки опасной зоны
    private Button btnDeleteAccount, btnCancelDeletion, btnLogout;

    // Слушатель для синхронизации со шторкой
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        cardTheme = view.findViewById(R.id.cardTheme);
        cardAnim = view.findViewById(R.id.cardAnim);
        cardDangerZone = view.findViewById(R.id.cardDangerZone);
        tvThemeDesc = view.findViewById(R.id.tvThemeDesc);
        tvAnimDesc = view.findViewById(R.id.tvAnimDesc);
        etSearch = view.findViewById(R.id.etSearch);
        profileHeader = view.findViewById(R.id.profileHeader);
        tvSettingsAvatar = view.findViewById(R.id.tvSettingsAvatar);
        tvSettingsName = view.findViewById(R.id.tvSettingsName);
        ImageView btnSearch = view.findViewById(R.id.btnSearch);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        btnCancelDeletion = view.findViewById(R.id.btnCancelDeletion);

        // ЗАПРОС РАЗРЕШЕНИЯ НА УВЕДОМЛЕНИЯ (ДЛЯ ANDROID 13+)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(requireContext(), "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{"android.permission.POST_NOTIFICATIONS"}, 101);
            }
        }

        // Проверка таймера при открытии
        checkDeletionStatus();

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getActivity(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), RegistrationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnDeleteAccount.setOnClickListener(v -> showDeleteConfirmationDialog());

        btnCancelDeletion.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE).edit().remove("account_delete_time").apply();

                NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                if (nm != null) nm.cancel(1001);

                Toast.makeText(getActivity(), "Удаление отменено", Toast.LENGTH_SHORT).show();
                checkDeletionStatus();
            }
        });

        // Интерфейс: кнопки темы и переходы
        view.findViewById(R.id.btnDay).setOnClickListener(v -> changeTheme("day"));
        view.findViewById(R.id.btnNight).setOnClickListener(v -> changeTheme("night"));
        cardAnim.setOnClickListener(v -> startActivity(new Intent(getActivity(), AnimationsActivity.class)));

        // Поиск
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

        // --- СИНХРОНИЗАЦИЯ СО ШТОРКОЙ В РЕАЛЬНОМ ВРЕМЕНИ ---
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
            prefListener = (sharedPreferences, key) -> {
                if ("account_delete_time".equals(key)) {
                    // Если нажали в шторке "Я передумал", кнопки мгновенно обновятся
                    checkDeletionStatus();
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(prefListener);
        }

        applyCurrentTheme();

        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
            tvSettingsAvatar.setText(prefs.getString("user_avatar_emoji", "🐶"));
            tvSettingsName.setText(prefs.getString("user_name", "User"));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Мелкие обновления при возврате на экран (но без полной перекраски)
        checkDeletionStatus();

        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
            tvSettingsAvatar.setText(prefs.getString("user_avatar_emoji", "🐶"));
            tvSettingsName.setText(prefs.getString("user_name", "User"));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Отключаем слушателя шторки при закрытии окна, чтобы не забивать память (очень важно!)
        if (getActivity() != null && prefListener != null) {
            getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE)
                    .unregisterOnSharedPreferenceChangeListener(prefListener);
        }
    }

    // --- МЕТОДЫ ОПАСНОЙ ЗОНЫ ---

    private long getDeletionDelayMs() {
        return 60 * 1000; // 1 минута (заменишь потом на 7 дней)
    }

    private void checkDeletionStatus() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        long deleteTime = prefs.getLong("account_delete_time", 0);

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
                btnDeleteAccount.setVisibility(View.GONE);
                btnCancelDeletion.setVisibility(View.VISIBLE);
            }
        } else {
            btnDeleteAccount.setVisibility(View.VISIBLE);
            btnCancelDeletion.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmationDialog() {
        if (getActivity() == null) return;

        new AlertDialog.Builder(getActivity())
                .setTitle("Удаление аккаунта")
                .setMessage("Аккаунт будет удален через выбранное время. Если вы передумали, нажмите «Я передумал» в уведомлении или «Отменить удаление» в настройках.")
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
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // Строго системная прозрачная иконка
                .setContentTitle("Аккаунт готов к удалению")
                .setContentText("Процесс запущен. Вы можете отменить его.")
                .addAction(android.R.drawable.ic_menu_revert, "Я ПЕРЕДУМАЛ", pendingCancelIntent)
                .setOngoing(true);

        if (nm != null) nm.notify(1001, builder.build());
        Toast.makeText(getActivity(), "Таймер удаления запущен!", Toast.LENGTH_SHORT).show();
    }

    // --- МЕТОДЫ ТЕМЫ И ПОКРАСКИ ---

    private void changeTheme(String theme) {
        if (getActivity() == null) return;
        getActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE).edit().putString("theme_mode", theme).apply();
        applyCurrentTheme();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).applyTheme();
        }
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
            cardDangerZone.setCardBackgroundColor(Color.parseColor("#1E1E1E"));

            btnLogout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2C1E1E")));
            btnLogout.setTextColor(Color.parseColor("#EF5350"));

            btnDeleteAccount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2C1E1E")));
            btnDeleteAccount.setTextColor(Color.parseColor("#EF5350"));

            btnCancelDeletion.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1B3320")));
            btnCancelDeletion.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            cardTheme.setCardBackgroundColor(Color.WHITE);
            cardAnim.setCardBackgroundColor(Color.WHITE);
            etSearch.setBackgroundColor(Color.parseColor("#E0E0E0"));
            cardDangerZone.setCardBackgroundColor(Color.WHITE);

            btnLogout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEBEE")));
            btnLogout.setTextColor(Color.parseColor("#D32F2F"));

            btnDeleteAccount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEBEE")));
            btnDeleteAccount.setTextColor(Color.parseColor("#D32F2F"));

            btnCancelDeletion.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
            btnCancelDeletion.setTextColor(Color.parseColor("#2E7D32"));
        }
    }
}