package com.example.sehatin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "sehatin_default_channel";
    private static final String CHANNEL_NAME = "Sehatin Notifications";
    private static final String CHANNEL_DESC = "Channel for Sehatin app notifications";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannelIfNeeded();
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM token: " + token);
        // TODO: kirim token ke server jika perlu
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData() != null && !remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
        }

        showAppropriateNotification(remoteMessage);
    }

    private void showAppropriateNotification(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        String title = getString(R.string.app_name);
        String body = "";

        if (data != null && data.containsKey("title")) {
            title = data.get("title");
        } else if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getTitle() != null) {
            title = remoteMessage.getNotification().getTitle();
        }

        if (data != null && data.containsKey("body")) {
            body = data.get("body");
        } else if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getBody() != null) {
            body = remoteMessage.getNotification().getBody();
        }

        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // Jika server mengirim extras via data, tambahkan ke intent jika diperlukan:
        // if (data != null) { intent.putExtra("some_key", data.get("some_key")); }

        int flags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                new Random().nextInt(),
                intent,
                flags
        );

        android.net.Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // pastikan resource ini ada
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        int notificationId = new Random().nextInt(Integer.MAX_VALUE - 1000) + 1000;
        managerCompat.notify(notificationId, builder.build());
    }

    private void createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null) return;

            NotificationChannel existing = manager.getNotificationChannel(CHANNEL_ID);
            if (existing == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription(CHANNEL_DESC);
                channel.enableLights(true);
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created: " + CHANNEL_ID);
            }
        }
    }
}
