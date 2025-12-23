package com.lssgoo.planner.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.lssgoo.planner.MainActivity
import com.lssgoo.planner.R
import com.lssgoo.planner.features.dashboard.models.MotivationalThoughts
import com.lssgoo.planner.features.dashboard.models.NotificationContext
import java.util.*

/**
 * Notification Manager for Planner App
 * Handles all notification scheduling and display
 */
class PlannerNotificationManager(private val context: Context) {
    
    companion object {
        const val CHANNEL_REMINDERS = "planner_reminders"
        const val CHANNEL_TASKS = "planner_tasks"
        const val CHANNEL_MOTIVATION = "planner_motivation"
        const val CHANNEL_DEADLINES = "planner_deadlines"
        
        const val ACTION_TASK_REMINDER = "com.lssgoo.planner.TASK_REMINDER"
        const val ACTION_DAILY_MOTIVATION = "com.lssgoo.planner.DAILY_MOTIVATION"
        const val ACTION_DEADLINE_WARNING = "com.lssgoo.planner.DEADLINE_WARNING"
        const val ACTION_CUSTOM_REMINDER = "com.lssgoo.planner.CUSTOM_REMINDER"
        
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_TYPE = "type"
        const val EXTRA_ITEM_ID = "item_id"
    }
    
    init {
        createNotificationChannels()
    }
    
    /**
     * Create all notification channels
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            
            // Reminders Channel
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for notes and custom alerts"
                enableVibration(true)
                enableLights(true)
            }
            
            // Tasks Channel
            val tasksChannel = NotificationChannel(
                CHANNEL_TASKS,
                "Task Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Task reminders and updates"
                enableVibration(true)
            }
            
            // Motivation Channel
            val motivationChannel = NotificationChannel(
                CHANNEL_MOTIVATION,
                "Daily Motivation",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Motivational messages and tips"
            }
            
            // Deadlines Channel
            val deadlinesChannel = NotificationChannel(
                CHANNEL_DEADLINES,
                "Deadline Warnings",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Warnings for approaching deadlines"
                enableVibration(true)
                enableLights(true)
            }
            
            notificationManager.createNotificationChannels(
                listOf(remindersChannel, tasksChannel, motivationChannel, deadlinesChannel)
            )
        }
    }
    
    /**
     * Check if notification permission is granted
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    /**
     * Show immediate notification
     */
    fun showNotification(
        notificationId: Int,
        title: String,
        message: String,
        channelId: String = CHANNEL_REMINDERS,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {
        if (!hasNotificationPermission()) return
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
    
    /**
     * Show motivational notification
     */
    fun showMotivationalNotification(userName: String = "") {
        val thought = MotivationalThoughts.getNotificationMessage(
            NotificationContext.MORNING_MOTIVATION,
            userName
        )
        
        showNotification(
            notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            title = "💡 Daily Inspiration",
            message = thought,
            channelId = CHANNEL_MOTIVATION,
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }
    
    /**
     * Schedule a reminder notification
     */
    fun scheduleReminder(
        notificationId: Int,
        title: String,
        message: String,
        triggerTime: Long,
        itemId: String = ""
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_CUSTOM_REMINDER
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_ITEM_ID, itemId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Schedule the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }
    
    /**
     * Cancel a scheduled reminder
     */
    fun cancelReminder(notificationId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
    
    /**
     * Schedule daily motivation notification
     */
    fun scheduleDailyMotivation(hour: Int = 8, minute: Int = 0, userName: String = "") {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // If time has passed, schedule for next day
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_DAILY_MOTIVATION
            putExtra("user_name", userName)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
    
    /**
     * Schedule periodic task reminders (every few hours)
     */
    fun schedulePeriodicReminders(intervalHours: Int = 4) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_TASK_REMINDER
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1002,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val intervalMillis = intervalHours * 60 * 60 * 1000L
        
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis,
            pendingIntent
        )
    }
    
    /**
     * Show task deadline warning
     */
    fun showDeadlineWarning(taskTitle: String, hoursRemaining: Int) {
        val message = when {
            hoursRemaining <= 1 -> "⚠️ Less than an hour left for: $taskTitle"
            hoursRemaining <= 24 -> "⏰ $hoursRemaining hours left for: $taskTitle"
            else -> "📅 ${hoursRemaining / 24} days left for: $taskTitle"
        }
        
        showNotification(
            notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            title = "Deadline Approaching!",
            message = message,
            channelId = CHANNEL_DEADLINES,
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }
}

/**
 * Broadcast Receiver for scheduled notifications
 */
class NotificationReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = PlannerNotificationManager(context)
        
        when (intent.action) {
            PlannerNotificationManager.ACTION_DAILY_MOTIVATION -> {
                val userName = intent.getStringExtra("user_name") ?: ""
                notificationManager.showMotivationalNotification(userName)
            }
            
            PlannerNotificationManager.ACTION_TASK_REMINDER -> {
                val thought = MotivationalThoughts.getNotificationMessage(
                    NotificationContext.TASK_REMINDER
                )
                notificationManager.showNotification(
                    notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                    title = "📋 Check Your Tasks",
                    message = "Don't forget your pending tasks! $thought",
                    channelId = PlannerNotificationManager.CHANNEL_TASKS
                )
            }
            
            PlannerNotificationManager.ACTION_CUSTOM_REMINDER -> {
                val notificationId = intent.getIntExtra(
                    PlannerNotificationManager.EXTRA_NOTIFICATION_ID,
                    (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                )
                val title = intent.getStringExtra(PlannerNotificationManager.EXTRA_TITLE) ?: "Reminder"
                val message = intent.getStringExtra(PlannerNotificationManager.EXTRA_MESSAGE) ?: ""
                
                notificationManager.showNotification(
                    notificationId = notificationId,
                    title = title,
                    message = message,
                    channelId = PlannerNotificationManager.CHANNEL_REMINDERS,
                    priority = NotificationCompat.PRIORITY_HIGH
                )
            }
            
            PlannerNotificationManager.ACTION_DEADLINE_WARNING -> {
                val title = intent.getStringExtra(PlannerNotificationManager.EXTRA_TITLE) ?: "Deadline Warning"
                val message = intent.getStringExtra(PlannerNotificationManager.EXTRA_MESSAGE) ?: ""
                
                notificationManager.showNotification(
                    notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                    title = title,
                    message = message,
                    channelId = PlannerNotificationManager.CHANNEL_DEADLINES,
                    priority = NotificationCompat.PRIORITY_HIGH
                )
            }
            
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reschedule notifications after device reboot
                // This would need to reload all scheduled reminders from storage
            }
        }
    }
}

/**
 * Boot Receiver to reschedule notifications after device restart
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule all notifications
            val notificationManager = PlannerNotificationManager(context)
            notificationManager.scheduleDailyMotivation()
            notificationManager.schedulePeriodicReminders()
        }
    }
}
