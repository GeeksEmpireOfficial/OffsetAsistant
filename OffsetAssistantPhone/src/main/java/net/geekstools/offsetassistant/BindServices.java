package net.geekstools.offsetassistant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.Html;

import net.geekstools.offsetassistant.Util.Functions.FunctionsClass;

public class BindServices extends Service {

    FunctionsClass functionsClass;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionsClass = new FunctionsClass(getApplicationContext());
        startForeground(333, bindService());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected Notification bindService() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder notificationBuilder = new Notification.Builder(this);

        notificationBuilder.setContentTitle(Html.fromHtml("<b><font color='" + getColor(R.color.default_color_darker) + "'>" + getResources().getString(R.string.bindTitle) + "</font></b>"));
        notificationBuilder.setContentText(Html.fromHtml("<font color='" + getColor(R.color.default_color) + "'>" + getResources().getString(R.string.bindDesc) + "</font>"));
        notificationBuilder.setTicker(getResources().getString(R.string.app_name));
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setColor(getColor(R.color.default_color));
        notificationBuilder.setPriority(Notification.PRIORITY_MIN);

        Intent ListGrid = new Intent(this, Configurations.class);
        PendingIntent ListGridPendingIntent = PendingIntent.getActivity(this, 5, ListGrid, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(ListGridPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getPackageName(), getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationBuilder.setChannelId(getPackageName());
        }


        Notification notification = notificationBuilder.build();
        return notification;
    }
}
