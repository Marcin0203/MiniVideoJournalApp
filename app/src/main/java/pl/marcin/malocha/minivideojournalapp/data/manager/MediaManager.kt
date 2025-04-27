package pl.marcin.malocha.minivideojournalapp.data.manager

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediaManager(private val context: Context) {
    private val videoDirectory: File by lazy {
        File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_MOVIES), "").apply {
            if (!exists()) mkdirs()
        }
    }

    fun createVideoUri(): Uri {
        val videoFile = createVideoFile()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            videoFile
        )
    }

    private fun createVideoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "VIDEO_$timeStamp.mp4"

        return File(videoDirectory, fileName)
    }
}