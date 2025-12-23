package com.lssgoo.planner.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lssgoo.planner.data.local.LocalStorageManager

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleAllReminders(context)
        }
    }

    private fun rescheduleAllReminders(context: Context) {
        val storageManager = LocalStorageManager(context)
        val reminders = storageManager.getReminders()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        reminders.filter { it.isEnabled && it.reminderTime > System.currentTimeMillis() }.forEach { reminder ->
            val alarmIntent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra(NotificationReceiver.EXTRA_TITLE, reminder.title)
                putExtra(NotificationReceiver.EXTRA_MESSAGE, reminder.description)
                putExtra(NotificationReceiver.EXTRA_TYPE, NotificationReceiver.TYPE_ONE_TIME)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.id.hashCode(),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.reminderTime,
                pendingIntent
            )
        }
    }
}
