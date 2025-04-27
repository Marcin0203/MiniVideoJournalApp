package pl.marcin.malocha.minivideojournalapp.domain.usecase

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity
import pl.marcin.malocha.minivideojournalapp.domain.repository.VideosRepository
import pl.marcin.malocha.minivideojournalapp.domain.usecase.videos.GetVideosUseCase
import java.util.Date

class GetVideosUseCaseTest {
    private val videosRepository = mockk<VideosRepository>(relaxed = true)
    private val uriMock = mockk<Uri>(relaxed = true)

    private val useCase = GetVideosUseCase(videosRepository)

    @Test
    fun getVideosUseCaseTest() = runTest {
        every { videosRepository.getAll() } returns flow { emit(listOf(
            VideoEntity(
                id = 1,
                uri = uriMock,
                description = "Test Description",
                createdAt = Date(1745751600000) // Sunday, 27 April 2025 13:00:00 GMT+02:00
            )
        )) }

        val videos = useCase().first()

        assertThat(videos.size).isEqualTo(1)
        assertThat(videos[0].id).isEqualTo(1)
        assertThat(videos[0].uri).isEqualTo(uriMock)
        assertThat(videos[0].description).isEqualTo("Test Description")
        assertThat(videos[0].createdAt).isEqualTo(Date(1745751600000)) // Sunday, 27 April 2025 13:00:00 GMT+02:00
    }
}