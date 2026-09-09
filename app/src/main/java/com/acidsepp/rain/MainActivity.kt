package com.acidsepp.rain

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.media3.exoplayer.ExoPlayer
import com.acidsepp.rain.ui.components.Background
import com.acidsepp.rain.ui.components.LoopControls
import com.acidsepp.rain.ui.theme.RainTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private val volumePreferenceKey = floatPreferencesKey("volume")
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var mediaPlayer: ExoPlayer

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialVolume = runBlocking {
            dataStore.data.map { preferences ->
                preferences[volumePreferenceKey] ?: 1.0f
            }.firstOrNull()
        } ?: 1.0f
        mediaPlayer.volume = initialVolume
        mediaPlayer.play()

        enableEdgeToEdge()
        setContent {
            RainTheme {
                Background()
                LoopControls(initialVolume, {
                    mediaPlayer.volume = it
                    lifecycleScope.launch {
                        dataStore.edit { settings ->
                            settings[volumePreferenceKey] = it
                        }
                    }
                }, mediaPlayer::play, mediaPlayer::pause)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.pause()
    }
}

@Composable
@Preview
fun Preview() {
    RainTheme {
        Background()
        LoopControls(0.5f, {}, {}, {})
    }
}