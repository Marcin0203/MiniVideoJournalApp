package pl.marcin.malocha.minivideojournalapp.data.repository

import android.net.Uri
import androidx.core.net.toUri
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import pl.marcin.malocha.minivideojournalapp.data.manager.MediaManager

class FileRepositoryTest {
    private val mediaManagerMock = mockk<MediaManager>(relaxed = true)

    // Android framework components require mocking in unit tests,
    // as they cannot be instantiated on the JVM.
    private val uriMock = mockk<Uri>(relaxed = true)

    private val fileRepository = FileRepositoryImpl(mediaManager = mediaManagerMock)

    @Test
    fun createVideoUriTest() {
        val testUriString = "testUri"

        every { uriMock.toString() } returns testUriString
        every { mediaManagerMock.createVideoUri() } returns uriMock

        val uri = fileRepository.createVideoUri()

        verify {
            mediaManagerMock.createVideoUri()
        }

        assertThat(uri.toString()).isEqualTo(testUriString)
    }
}