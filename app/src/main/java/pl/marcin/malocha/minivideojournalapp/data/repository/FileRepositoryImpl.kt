package pl.marcin.malocha.minivideojournalapp.data.repository

import android.net.Uri
import pl.marcin.malocha.minivideojournalapp.data.manager.MediaManager
import pl.marcin.malocha.minivideojournalapp.domain.repository.FileRepository

class FileRepositoryImpl(private val mediaManager: MediaManager) : FileRepository {
    override fun createVideoUri(): Uri {
        return mediaManager.createVideoUri()
    }
}