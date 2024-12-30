package com.acidsepp.rain

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.acidsepp.rain.ui.components.Background
import com.acidsepp.rain.ui.components.PlayButton
import com.acidsepp.rain.ui.theme.RainTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.protyposis.android.mediaplayer.MediaPlayer
import javax.inject.Inject

private val volumePreferenceKey = floatPreferencesKey("volume")
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var mediaPlayer: MediaPlayer
    private lateinit var serviceIntent: Intent

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val volume = runBlocking {
            dataStore.data.map { preferences -> preferences[volumePreferenceKey] ?: 1.0f }
                .firstOrNull()
        } ?: 1.0f
        mediaPlayer.setVolume(volume)

        enableEdgeToEdge()
        setContent {
            RainTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    Background()
                    var sliderValue by remember { mutableFloatStateOf(volume) }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PlayButton(mediaPlayer)
                        VerticalDivider(
                            modifier = Modifier
                                .fillMaxSize(0.3f)
                                .alpha(0f)
                        )
                        Slider(
                            value = sliderValue,
                            onValueChange = {
                                sliderValue = it
                                mediaPlayer.setVolume(it, it)
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

        serviceIntent = Intent(this, RainService::class.java)
        startService(serviceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(serviceIntent)
    }
}
