package com.pozornik.mypetmon;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class EventNotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "petmon_events_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String eventDescription = intent.getStringExtra("event_desc");
        if (eventDescription == null) {
            eventDescription = "У вас запланировано событие для питомца!";
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Календарь питомцев",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Заменить на подходящую иконку
                .setContentTitle("Напоминание от PetMon")
                .setContentText(eventDescription)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
