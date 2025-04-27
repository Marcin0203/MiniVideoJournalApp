package pl.marcin.malocha.minivideojournalapp.data.db

import kotlinx.coroutines.flow.Flow
import pl.marcin.malocha.minivideojournalapp.Videos

interface VideoDao {
    fun insert(videoPath: String, description: String?, createdAt: Long)
    fun getAll(): Flow<List<Videos>>
    fun delete(id: Long)
}