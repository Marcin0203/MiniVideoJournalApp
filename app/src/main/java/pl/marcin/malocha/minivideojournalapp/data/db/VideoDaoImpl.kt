package pl.marcin.malocha.minivideojournalapp.data.db

class VideoDaoImpl(
    private val databaseHelper: VideoDatabaseHelper
) : VideoDao {

    override fun insert(videoPath: String, description: String?, createdAt: Long) {
        databaseHelper.insert(videoPath, description, createdAt)
    }

    override fun getAll() = databaseHelper.getAllVideos()

    override fun delete(id: Long) {
        databaseHelper.deleteVideo(id)
    }
}