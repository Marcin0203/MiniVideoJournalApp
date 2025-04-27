package pl.marcin.malocha.minivideojournalapp.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity

interface VideosRepository {
    fun getAll(): Flow<List<VideoEntity>>
    suspend fun insert(videoEntity: VideoEntity)
    suspend fun delete(id: Long)
}