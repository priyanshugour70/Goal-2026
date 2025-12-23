package com.lssgoo.planner.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.lssgoo.planner.MainActivity
import com.lssgoo.planner.R
import com.lssgoo.planner.data.model.MotivationalThoughts

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "PLANNER_REMINDERS"
        const val CHANNEL_NAME = "Planner Reminders"
        
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_TYPE = "extra_type"
        
        const val TYPE_RECURRING = "recurring"
        const val TYPE_ONE_TIME = "one_time"
        const val TYPE_MOTIVATIONAL = "motivational"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Planner"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Don't forget your goals!"
        val type = intent.getStringExtra(EXTRA_TYPE) ?: TYPE_ONE_TIME

        // Create notification channel for Android O and above
        createNotificationChannel(context)

        // Show motivational thought if requested
        val finalMessage = if (type == TYPE_MOTIVATIONAL) {
            MotivationalThoughts.getThoughtOfTheDay()
        } else {
            message
        }

        showNotification(context, title, finalMessage)
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use appropriate icon
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Reminders for your goals"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
