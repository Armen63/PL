package com.example.armen.pl.util;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.armen.pl.R;
import com.example.armen.pl.ui.activity.MainActivity;

public class AppUtil {

    public static int booleanToInt(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    public static boolean intToBoolean(int b) {
        if (b == 1) {
            return true;
        } else {
            return false;
        }
    }

    public static void sendNotification(Context context,
                                        String title, String description, String data) {

        // Create an explicit content Intent that starts the main Activity.
        Intent intent = new Intent(context, MainActivity    .class);
        intent.putExtra(Constant.Extra.EXTRA_NOTIFICATION_DATA, data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(intent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        // small style
        builder.setSmallIcon(android.R.drawable.sym_action_chat)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.img_drawer_android))
                .setColor(Color.GRAY)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description))
                .setContentIntent(notificationPendingIntent);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

}
