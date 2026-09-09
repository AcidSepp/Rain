package com.acidsepp.rain.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun LoopControls(
    initialVolume: Float = 0.5f,
    onVolumeChange: (volume: Float) -> Unit = {},
    onPlay: () -> Unit = {},
    onPause: () -> Unit = {},
) {
    var sliderValue by remember { mutableFloatStateOf(initialVolume) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayButton(
            onPlay,
            onPause
        )
        VerticalDivider(
            modifier = Modifier
                .fillMaxSize(0.3f)
                .alpha(0f)
        )
        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                onVolumeChange(it)
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .alpha(0.8f)
        )
    }
}
