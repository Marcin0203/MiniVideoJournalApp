package pl.marcin.malocha.minivideojournalapp.domain.repository

import android.net.Uri

interface FileRepository {
    fun createVideoUri(): Uri
}