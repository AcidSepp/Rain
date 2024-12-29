package com.acidsepp.rain

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.acidsepp.rain.ui.theme.RainTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

val rainLength: Duration = Duration.ofSeconds(16)
val crossfadeLength: Duration = Duration.ofSeconds(1)
val loopLength = rainLength.minus(crossfadeLength)

class MainActivity : ComponentActivity() {

    private var playerBCoroutineScope: Job? = null
    private var playerACoroutineScope: Job? = null

    private lateinit var mediaPlayerA: MediaPlayer
    private lateinit var mediaPlayerB: MediaPlayer
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val volumePreferenceKey = floatPreferencesKey("volume")
    private val stopped = AtomicBoolean(true)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val volumePreference = runBlocking {
            dataStore.data.map { preferences -> preferences[volumePreferenceKey] ?: 1.0f }.firstOrNull()
        } ?: 1.0f

        enableEdgeToEdge()
        setContent {
            RainTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    Background()
                    var sliderValue by remember { mutableFloatStateOf(volumePreference) }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PlayButton(lifecycleScope)
                        Slider(
                            value = sliderValue,
                            onValueChange = {
                                sliderValue = it
                                mediaPlayerA.setVolume(it, it)
                                mediaPlayerB.setVolume(it, it)
                                lifecycleScope.launch {
                                    dataStore.edit { settings ->
                                        settings[volumePreferenceKey] = it
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .alpha(0.8f)
                        )
                    }
                }
            }
        }

        mediaPlayerA = MediaPlayer.create(this, R.raw.rain)
        mediaPlayerA.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }

        mediaPlayerB = MediaPlayer.create(this, R.raw.rain)
        mediaPlayerB.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }

        startLooping(lifecycleScope)
    }

    private fun startLooping(lifecycleScope: LifecycleCoroutineScope) {
        if (!stopped.get()) {
            return
        }
        stopped.set(false)

        playerACoroutineScope?.cancel()
        playerBCoroutineScope?.cancel()

        playerACoroutineScope = lifecycleScope.launch {
            while (!stopped.get()) {
                mediaPlayerA.restart()
                Log.i("", "Media Player A started")
                delay(loopLength.toMillis() * 2)
            }
        }

        playerBCoroutineScope = lifecycleScope.launch {
            while (!stopped.get()) {
                delay(loopLength.toMillis())
                mediaPlayerB.restart()
                Log.i("", "Media Player B started")
                delay(loopLength.toMillis())
            }
        }
    }

    private fun stopLooping() {
        if (stopped.get()) {
            return
        }
        stopped.set(true)
        mediaPlayerA.pause()
        mediaPlayerB.pause()
        playerACoroutineScope?.cancel()
        playerBCoroutineScope?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerA.apply {
            stop()
            release()
        }

        mediaPlayerA.apply {
            stop()
            release()
        }
    }

    @Composable
    fun PlayButton(lifecycleScope: LifecycleCoroutineScope) {
        var isPlaying by remember { mutableStateOf(true) }

        // Main button logic
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(200.dp)
                .clickable {
                    if (isPlaying) {
                        stopLooping()
                        isPlaying = false
                    } else {
                        startLooping(lifecycleScope)
                        isPlaying = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Clear else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Stop" else "Play",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.8f)
            )
        }
    }
}


@Composable
fun Background() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            Modifier.fillMaxSize()
        )
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = "",
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun MediaPlayer.restart() = try {
    this.seekTo(0)
    this.start()
} catch (e: Exception) {
    Log.i("", "Could not stop media player", e)
}
