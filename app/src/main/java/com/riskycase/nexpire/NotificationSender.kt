package com.riskycase.nexpire

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

class NotificationSender: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReceive(context: Context?, intent: Intent?) {
        val dbh = DatabaseHelper(context)
        val item: Item? = intent?.getLongExtra("id", 0)?.let { dbh.getItem(it) }
        val nmanager: NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val channel = NotificationChannel("reminder", "Expiry reminders", NotificationManager.IMPORTANCE_DEFAULT)
                    .apply {
                        description = "Channel where reminders will be posted"
                    }
            nmanager.createNotificationChannel(channel)
        }
        val start = Calendar.getInstance()
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)
        val end = Calendar.getInstance()
        end.timeInMillis = item!!.expiry
        val days = (end.timeInMillis - start.timeInMillis) / (1000 * 3600 * 24)
        val startIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pintent = PendingIntent.getActivity(context, 0, startIntent, 0)

        val builder = NotificationCompat.Builder(context, "reminder")
                .setSmallIcon(R.drawable.ic_baseline_access_alarm)
                .setContentTitle("${item.name} is expiring ${
                    if (days.toInt() == 1)
                        "tomorrow"
                    else
                        "in $days days (at ${SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date(item.expiry))})"
                }")
                .setContentText("Click here to view more info")
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setContentIntent(pintent)
                .setAutoCancel(true)
        nmanager.notify("reminder", item.id.toInt(), builder.build())
    }
}