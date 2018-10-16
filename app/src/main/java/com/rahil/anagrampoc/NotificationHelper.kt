package com.rahil.anagrampoc

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi

/**
 * Helper class to manage notification channels, and create notifications.
 */
internal class NotificationHelper
/**
 * Registers notification channels, which can be used later by individual notifications.
 * @param ctx The application context
 */
(ctx: Context) : ContextWrapper(ctx) {
    private val manager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val chan1 = NotificationChannel(PRIMARY_CHANNEL,
                    getString(R.string.noti_channel_default), NotificationManager.IMPORTANCE_DEFAULT)
            chan1.lightColor = Color.GREEN
            chan1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            manager.createNotificationChannel(chan1)
        }


    }

    /**
     * Get a notification of type 1
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     * @param title the title of the notification
     * *
     * @param body the body text for the notification
     * *
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    fun getNotification(title: String, body: String): Notification.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, PRIMARY_CHANNEL)
        } else {
            Notification.Builder(applicationContext)
        }.apply {
            setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_launcher))
            setAutoCancel(true)
            setOnlyAlertOnce(true)
            setSmallIcon(smallIcon)
            setOngoing(true)
            setContentTitle(title)
            setContentText(body)
        }
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            /**
     * Send a notification.
     * @param id The ID of the notification
     * *
     * @param notification The notification object
     */
    fun notify(id: Int, notification: Notification.Builder) {
        manager.notify(id, notification.build())
    }

    /**
     * Get the small icon for this app
     * @return The small icon resource id
     */
    private val smallIcon: Int
        get() = android.R.drawable.stat_notify_chat


    companion object {
        val PRIMARY_CHANNEL = "default"
    }
}