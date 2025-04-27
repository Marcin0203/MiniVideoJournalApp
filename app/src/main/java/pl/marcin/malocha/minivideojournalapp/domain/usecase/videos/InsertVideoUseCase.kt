package pl.marcin.malocha.minivideojournalapp.domain.usecase.videos

import android.net.Uri
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity
import pl.marcin.malocha.minivideojournalapp.domain.repository.VideosRepository
import java.util.Date

class InsertVideoUseCase(
    private val repository: VideosRepository
) {
    suspend operator fun invoke(uri: Uri, description: String?) {
        val videoEntity = VideoEntity(
            uri = uri,
            description = description,
            createdAt = Date()
        )

        repository.insert(videoEntity)
    }
}