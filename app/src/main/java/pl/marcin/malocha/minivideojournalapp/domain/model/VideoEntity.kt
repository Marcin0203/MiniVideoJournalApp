package pl.marcin.malocha.minivideojournalapp.domain.model

import android.net.Uri
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class VideoEntity(
    val id: Long = 0,
    @Contextual val uri: Uri,
    val description: String?,
    @Contextual val createdAt: Date
)