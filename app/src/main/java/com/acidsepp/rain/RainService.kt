package com.acidsepp.rain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import net.protyposis.android.mediaplayer.MediaPlayer
import javax.inject.Inject

@AndroidEntryPoint
class RainService : Service() {

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            1337,
            buildForegroundNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
        mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.prepare()
        mediaPlayer.start()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun buildForegroundNotification(): Notification {
        val channel = NotificationChannel(
            "Rain",
            "Rain",
            NotificationManager.IMPORTANCE_LOW
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channel.id)
            .setOngoing(true)
            .setContentTitle("Rain")
            .setContentText("Rain").build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.apply {
            stop()
            release()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }
}

