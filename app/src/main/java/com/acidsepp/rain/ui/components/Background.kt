package com.acidsepp.rain.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.acidsepp.rain.R

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