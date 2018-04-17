package net.mjorn.cuteweather.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import net.mjorn.cuteweather.MainActivity;
import net.mjorn.cuteweather.R;

public class NotificationUtils {

    private static final int RAIN_PENDING_INTENT_ID = 34545;
    private static final String RAIN_NOTIFICATION_CHANNEL_ID = "356";
    private static final int RAIN_NOTIFICATION_ID = 289349;

    public static void notifyUserAboutRain(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    RAIN_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context, RAIN_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorDarkPink))
                .setSmallIcon(R.drawable.rain)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.rain_notification_title))
                .setContentText(context.getString(R.string.rain_notification_text))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(rainIntent(context))
                .setAutoCancel(true);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(RAIN_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent rainIntent(Context context) {
        Intent startMainActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                RAIN_PENDING_INTENT_ID,
                startMainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.rain);
        return largeIcon;
    }

}
