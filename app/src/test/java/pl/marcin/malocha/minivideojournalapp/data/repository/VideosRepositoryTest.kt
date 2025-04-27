package pl.marcin.malocha.minivideojournalapp.data.repository

import android.net.Uri
import androidx.core.net.toUri
import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pl.marcin.malocha.minivideojournalapp.Videos
import pl.marcin.malocha.minivideojournalapp.data.db.VideoDao
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity
import java.util.Date

class VideosRepositoryTest {
    private val dao = mockk<VideoDao>(relaxed = true)

    private val testUri1Mock = mockk<Uri>(relaxed = true)
    private val testUri2Mock = mockk<Uri>(relaxed = true)

    private val testPath1 = "TestPath"
    private val testPath2 = "TestPath2"

    private val videosRepository = VideosRepositoryImpl(dao = dao)

    @Before
    fun setUp() {
        // Android framework components require mocking in unit tests,
        // as they cannot be instantiated on the JVM.
        mockkStatic(Uri::class)

        every { testPath1.toUri() } returns testUri1Mock
        every { testPath2.toUri() } returns testUri2Mock

        every { testUri1Mock.toString() } returns testPath1
        every { testUri2Mock.toString() } returns testPath2
    }

    @Test
    fun getAllTest() = runTest {
        val videosDb = listOf(
            Videos(
                id = 1,
                path = testPath1,
                description = "Test Description",
                createdAt = 1745751600000 // Sunday, 27 April 2025 13:00:00 GMT+02:00
            ),
            Videos(
                id = 2,
                path = testPath2,
                description = "Test Description 2",
                createdAt = 1745752200000 // Sunday, 27 April 2025 13:10:00 GMT+02:00
            )
        )

        every { dao.getAll() } returns flow { emit(videosDb) }

        val videos = videosRepository.getAll().first()

        assertThat(videos.size).isEqualTo(videosDb.size)

        videosDb.forEachIndexed { index, video ->
            assertVideoEntity(video, videos[index])
        }
    }

    @Test
    fun insertTest() = runTest {
        val videoEntity = VideoEntity(
            id = 1,
            uri = testUri1Mock,
            description = "Test Description",
            createdAt = Date(1745751600000) // Sunday, 27 April 2025 13:00:00 GMT+02:00
        )

        coEvery { dao.insert(any(), any(), any()) } just Runs

        videosRepository.insert(videoEntity)

        verify {
            dao.insert(
                withArg {
                    assertThat(it).isEqualTo(testPath1)
                },
                withArg {
                    assertThat(it).isEqualTo("Test Description")
                },
                withArg {
                    assertThat(it).isEqualTo(1745751600000) // Sunday, 27 April 2025 13:00:00 GMT+02:00
                },
            )
        }
    }

    @Test
    fun deleteTest() = runTest {
        coEvery { dao.delete(any()) } just Runs

        videosRepository.delete(1)

        verify {
            dao.delete(withArg { assertThat(it).isEqualTo(1) })
        }
    }

    private fun assertVideoEntity(videos: Videos, videoEntity: VideoEntity) {
        assertThat(videos.id).isEqualTo(videoEntity.id)
        assertThat(videos.path).isEqualTo(videoEntity.uri.toString())
        assertThat(videos.description).isEqualTo(videoEntity.description)
        assertThat(videos.createdAt).isEqualTo(videoEntity.createdAt.time)
    }
}