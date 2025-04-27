package pl.marcin.malocha.minivideojournalapp.app.navigation

import kotlinx.serialization.Serializable
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity

sealed interface NavGraph {
    @Serializable
    data object VideoListRoute

    @Serializable
    data class PlayerRoute(
        val videoEntity: VideoEntity
    )
}