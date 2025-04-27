package pl.marcin.malocha.minivideojournalapp.data.mapper

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.exoplayer.ExoPlayer
import pl.marcin.malocha.minivideojournalapp.Videos
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoPlayerEntity
import java.util.Date

fun Videos.toDomain(): VideoEntity {
    return VideoEntity(
        id = id,
        uri = path.toUri(),
        description = description,
        createdAt = Date(createdAt)
    )
}

fun VideoEntity.toDb(): Videos {
    return Videos(
        id = id,
        path = uri.toString(),
        description = description,
        createdAt = createdAt.time
    )
}

fun VideoEntity.toPlayer(context: Context): VideoPlayerEntity {
    return VideoPlayerEntity(
        videoEntity = this,
        player = ExoPlayer.Builder(context).build()
    )
}