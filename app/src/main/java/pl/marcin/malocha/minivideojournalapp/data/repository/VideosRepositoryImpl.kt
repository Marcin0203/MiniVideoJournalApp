package pl.marcin.malocha.minivideojournalapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.marcin.malocha.minivideojournalapp.data.db.VideoDao
import pl.marcin.malocha.minivideojournalapp.data.mapper.toDb
import pl.marcin.malocha.minivideojournalapp.data.mapper.toDomain
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity
import pl.marcin.malocha.minivideojournalapp.domain.repository.VideosRepository

class VideosRepositoryImpl(
    private val dao: VideoDao
) : VideosRepository {
    override fun getAll(): Flow<List<VideoEntity>> {
        return dao.getAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insert(videoEntity: VideoEntity) {
        val dbVideoEntity = videoEntity.toDb()

        dao.insert(dbVideoEntity.path, dbVideoEntity.description, dbVideoEntity.createdAt)
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }
}