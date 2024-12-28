package com.acidsepp.rain

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.acidsepp.rain.ui.theme.RainTheme
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val volumePreferenceKey = floatPreferencesKey("volume")

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
                    ImageWithBackground(
                        painter = painterResource(id = R.drawable.background),
                        backgroundDrawableResId = R.drawable.background,
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize()
                    )
                    var sliderValue by remember { mutableFloatStateOf(volumePreference) }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PlayButton(mediaPlayer!!)
                        Slider(
                            value = sliderValue,
                            onValueChange = {
                                sliderValue = it
                                mediaPlayer!!.setVolume(it, it)
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

        mediaPlayer = MediaPlayer.create(this, R.raw.rain)
        mediaPlayer?.apply {
            isLooping = true

            // Ignore audio focus changes
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setVolume(volumePreference, volumePreference)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Release the MediaPlayer resources when the activity is destroyed
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
    }
}


@Composable
fun ImageWithBackground(
    painter: Painter,
    @DrawableRes backgroundDrawableResId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(backgroundDrawableResId),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
        )
        Image(
            painter = painter,
            contentDescription = contentDescription,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            modifier = Modifier
                .matchParentSize()
        )
    }
}


@Composable
fun PlayButton(mediaPlayer: MediaPlayer) {
    var isPlaying by remember { mutableStateOf(mediaPlayer.isPlaying) }


    // Update the state whenever the MediaPlayer starts/stops
    DisposableEffect(mediaPlayer) {
        val listener = MediaPlayer.OnCompletionListener {
            isPlaying = false
        }
        mediaPlayer.setOnCompletionListener(listener)

        onDispose {
            mediaPlayer.setOnCompletionListener(null)
        }
    }

    // Main button logic
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(200.dp)
            .clickable {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    isPlaying = false
                } else {
                    mediaPlayer.start()
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