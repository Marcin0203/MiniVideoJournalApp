package pl.marcin.malocha.minivideojournalapp.data.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pl.marcin.malocha.minivideojournalapp.VideoDatabase
import pl.marcin.malocha.minivideojournalapp.Videos

class VideoDatabaseHelper(
    database: VideoDatabase
) {
    private val queries = database.videosQueries

    fun insert(uri: String, description: String?, createdAt: Long) {
        queries.insert(path = uri, description = description, createdAt = createdAt)
    }

    fun getAllVideos(): Flow<List<Videos>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    fun deleteVideo(id: Long) {
        queries.deleteById(id)
    }
}