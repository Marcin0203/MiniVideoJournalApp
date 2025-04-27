package pl.marcin.malocha.minivideojournalapp.domain.usecase.videos

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.marcin.malocha.minivideojournalapp.data.mapper.toPlayer
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoPlayerEntity
import pl.marcin.malocha.minivideojournalapp.domain.repository.VideosRepository

class GetVideoPlayersUseCase(
    private val repository: VideosRepository,
    private val context: Context
) {
    operator fun invoke(): Flow<List<VideoPlayerEntity>> {
        return repository.getAll()
            .map { list ->
                list.map {
                    it.toPlayer(context)
                }
            }
    }
}