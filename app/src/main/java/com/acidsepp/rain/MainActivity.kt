package com.acidsepp.rain

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.protyposis.android.mediaplayer.MediaPlayer
import net.protyposis.android.mediaplayer.UriSource

class MainActivity : ComponentActivity() {

    private var mediaPlayerA = MediaPlayer()
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val volumePreferenceKey = floatPreferencesKey("volume")

    private var volume = 1.0f

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        volume = runBlocking {
            dataStore.data.map { preferences -> preferences[volumePreferenceKey] ?: 1.0f }
                .firstOrNull()
        } ?: 1.0f

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
                        PlayButton(::startLooping, ::stopLooping)
                        VerticalDivider(modifier = Modifier.fillMaxSize(0.3f).alpha(0f))
                        Slider(
                            value = sliderValue,
                            onValueChange = {
                                volume = it
                                sliderValue = it
                                mediaPlayerA.setVolume(it, it)
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

        mediaPlayerA.setDataSource(UriSource(this, this.resourceUri(R.raw.rain)))
        mediaPlayerA.isLooping = true
        mediaPlayerA.prepare()

        startLooping()
    }

    @Synchronized
    private fun startLooping() {
        mediaPlayerA.start()
    }

    @Synchronized
    private fun stopLooping() {
        mediaPlayerA.pause()
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
}

fun Context.resourceUri(resourceId: Int): Uri = with(resources) {
    Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(getResourcePackageName(resourceId))
        .appendPath(getResourceTypeName(resourceId))
        .appendPath(getResourceEntryName(resourceId))
        .build()
}
