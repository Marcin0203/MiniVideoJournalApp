package pl.marcin.malocha.minivideojournalapp.domain.usecase.videos

import kotlinx.coroutines.flow.Flow
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity
import pl.marcin.malocha.minivideojournalapp.domain.repository.VideosRepository

class GetVideosUseCase(
    private val repository: VideosRepository
) {
    operator fun invoke(): Flow<List<VideoEntity>> {
        return repository.getAll()
    }
}