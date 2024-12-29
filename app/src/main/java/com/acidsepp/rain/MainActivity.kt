package com.acidsepp.rain

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity() {

    private lateinit var mediaPlayerA: MediaPlayer
    private lateinit var mediaPlayerB: MediaPlayer
    private val stopped = AtomicBoolean(false)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaPlayerA = MediaPlayer.create(this, R.raw.raincrossfaded)
        mediaPlayerA.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }

        mediaPlayerB = MediaPlayer.create(this, R.raw.raincrossfaded)
        mediaPlayerB.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }

        lifecycleScope.launch {
            while (!stopped.get()) {
                Log.i("", "Media Player A started")
                mediaPlayerA.start()
                delay(mediaPlayerA.duration.toLong() - 1000L)
                delay(mediaPlayerA.duration.toLong() - 1000L)
            }
        }

        lifecycleScope.launch {
            while (!stopped.get()) {
                delay(mediaPlayerA.duration.toLong() - 1000L)
                Log.i("", "Media Player B started")
                mediaPlayerB.start()
                delay(mediaPlayerA.duration.toLong() - 1000L)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopped.set(true)

        // Release the MediaPlayer resources when the activity is destroyed
        mediaPlayerA.apply {
            stop()
            release()
        }

        mediaPlayerA.apply {
            stop()
            release()
        }
    }
}


