package com.acidsepp.rain

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.protyposis.android.mediaplayer.MediaPlayer
import net.protyposis.android.mediaplayer.UriSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RainComponent {

    @Provides
    @Singleton
    fun provideMediaPlayer(
        @ApplicationContext context: Context
    ): MediaPlayer {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(UriSource(context, context.resourceUri(R.raw.rain)))
        mediaPlayer.isLooping = true
        return mediaPlayer
    }

    private fun Context.resourceUri(resourceId: Int): Uri = with(resources) {
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResourcePackageName(resourceId))
            .appendPath(getResourceTypeName(resourceId))
            .appendPath(getResourceEntryName(resourceId))
            .build()
    }
}