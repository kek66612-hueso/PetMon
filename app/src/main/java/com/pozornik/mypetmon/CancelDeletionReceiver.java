package com.pozornik.mypetmon;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class CancelDeletionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 1. Удаляем таймер из памяти (отменяем удаление)
        SharedPreferences prefs = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        prefs.edit().remove("account_delete_time").apply();

        // 2. Смахиваем само уведомление из шторки (1001 - это ID нашего уведомления)
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.cancel(1001);

        // 3. Показываем пользователю, что всё хорошо
        Toast.makeText(context, "Удаление аккаунта отменено!", Toast.LENGTH_LONG).show();
    }
}