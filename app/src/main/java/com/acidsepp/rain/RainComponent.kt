package com.acidsepp.rain

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RainComponent {

    @Provides
    @Singleton
    fun provideMediaPlayer(
        @ApplicationContext context: Context,
    ): ExoPlayer {
        val mediaPlayer = ExoPlayer.Builder(context).build()
        mediaPlayer.setMediaItem(MediaItem.fromUri(context.resourceUri(R.raw.rain)))
        mediaPlayer.repeatMode = Player.REPEAT_MODE_ONE
        return mediaPlayer
    }

    private fun Context.resourceUri(resourceId: Int): Uri = with(resources) {
        Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResourcePackageName(resourceId))
            .appendPath(getResourceTypeName(resourceId))
            .appendPath(getResourceEntryName(resourceId)).build()
    }
}