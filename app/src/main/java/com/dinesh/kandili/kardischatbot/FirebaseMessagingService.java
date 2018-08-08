package com.dinesh.kandili.kardischatbot;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_body = remoteMessage.getNotification().getBody();
        String clickAction = remoteMessage.getNotification().getClickAction();
        String fromUserId = remoteMessage.getData().get("fromUserId");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.crop_image_menu_flip)
                .setContentTitle(notification_title)
                .setContentText(notification_body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(clickAction);
        intent.putExtra("userId",fromUserId);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = (int)System.currentTimeMillis();
        notificationManager.notify(notificationId, mBuilder.build());


    }
}
