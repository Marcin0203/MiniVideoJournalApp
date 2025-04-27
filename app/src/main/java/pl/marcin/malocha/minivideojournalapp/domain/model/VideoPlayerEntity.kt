package pl.marcin.malocha.minivideojournalapp.domain.model

import androidx.media3.exoplayer.ExoPlayer

data class VideoPlayerEntity(
    val videoEntity: VideoEntity,
    val player: ExoPlayer
)