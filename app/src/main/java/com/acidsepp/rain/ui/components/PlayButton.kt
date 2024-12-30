package com.acidsepp.rain.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.acidsepp.rain.ui.theme.PauseIcon
import net.protyposis.android.mediaplayer.MediaPlayer

@Composable
fun PlayButton(mediaPlayer: MediaPlayer) {
    var isPlaying by remember { mutableStateOf(true) }
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
            imageVector = if (isPlaying) PauseIcon else Icons.Filled.PlayArrow,
            contentDescription = if (isPlaying) "Stop" else "Play",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.8f)
        )
    }
}