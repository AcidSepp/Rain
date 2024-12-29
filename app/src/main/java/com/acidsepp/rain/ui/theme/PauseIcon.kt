package com.acidsepp.rain.ui.theme

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val PauseIcon: ImageVector
    get() {
        if (_pauseIcon != null) {
            return _pauseIcon!!
        }
        _pauseIcon = materialIcon(name = "Filled.PauseIcon") {
            materialPath {
                // Draw the left bar
                moveTo(6.0f, 5.0f)
                lineTo(6.0f, 19.0f)
                lineTo(10.0f, 19.0f)
                lineTo(10.0f, 5.0f)
                close()

                // Draw the right bar
                moveTo(14.0f, 5.0f)
                lineTo(14.0f, 19.0f)
                lineTo(18.0f, 19.0f)
                lineTo(18.0f, 5.0f)
                close()
            }
        }
        return _pauseIcon!!
    }

private var _pauseIcon: ImageVector? = null
