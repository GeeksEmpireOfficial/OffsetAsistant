package net.geekstools.offsetassistant

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.text.Html
import net.geekstools.offsetassistant.Util.Functions.FunctionsClass

class BindServices : Service() {

    lateinit var functionsClass: FunctionsClass

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        functionsClass = FunctionsClass(applicationContext)
        startForeground(333, bindService())
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    protected fun bindService(): Notification {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = Notification.Builder(this)

        notificationBuilder.setContentTitle(Html.fromHtml("<b><font color='" + getColor(R.color.default_color_darker) + "'>" + resources.getString(R.string.bindTitle) + "</font></b>", Html.FROM_HTML_MODE_LEGACY))
        notificationBuilder.setContentText(Html.fromHtml("<font color='" + getColor(R.color.default_color) + "'>" + resources.getString(R.string.bindDesc) + "</font>", Html.FROM_HTML_MODE_LEGACY))
        notificationBuilder.setTicker(resources.getString(R.string.app_name))
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        notificationBuilder.setAutoCancel(false)
        notificationBuilder.setColor(getColor(R.color.default_color))
        notificationBuilder.setPriority(Notification.PRIORITY_MIN)

        val ListGrid = Intent(this, Configurations::class.java)
        val ListGridPendingIntent = PendingIntent.getActivity(this, 5, ListGrid, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(ListGridPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(packageName, getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationBuilder.setChannelId(packageName)
        }


        return notificationBuilder.build()
    }
}
