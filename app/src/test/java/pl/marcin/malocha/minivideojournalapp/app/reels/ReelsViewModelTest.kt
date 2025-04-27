package pl.marcin.malocha.minivideojournalapp.app.reels

import android.net.Uri
import androidx.media3.exoplayer.ExoPlayer
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pl.marcin.malocha.minivideojournalapp.app.reels.PlayerEvents.OnClearAllPlayers
import pl.marcin.malocha.minivideojournalapp.app.reels.PlayerEvents.OnPlayVideoEvent
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoPlayerEntity
import pl.marcin.malocha.minivideojournalapp.domain.usecase.videos.GetVideoPlayersUseCase
import java.util.Date

class ReelsViewModelTest {
    private val getVideoPlayersUseCaseMock = mockk<GetVideoPlayersUseCase>(relaxed = true)
    private val exoPlayerMock = mockk<ExoPlayer>(relaxed = true)
    private val exoPlayerMock2 = mockk<ExoPlayer>(relaxed = true)
    private val uriMock = mockk<Uri>(relaxed = true)

    private lateinit var  viewModel: ReelsViewModel

    private val testVideoPlayerEntity = VideoPlayerEntity(
        videoEntity = VideoEntity(
            id = 1,
            uri = uriMock,
            description = "Test Description",
            createdAt = Date(1745751600000)), // Sunday, 27 April 2025 13:00:00 GMT+02:00)
        player = exoPlayerMock
    )

    @Before
    fun setUp() {
        viewModel = createViewModel()
    }

    private fun createViewModel(): ReelsViewModel {
        return ReelsViewModel(
            videoPlayerEntity = testVideoPlayerEntity,
            getVideoPlayersUseCase = getVideoPlayersUseCaseMock
        )
    }

    @Test
    fun videosStateInit() = runTest {
        viewModel.videosState.test {
            val state = awaitItem()

            assertThat(state.isLoading).isTrue()
            assertThat(state.videos).isEqualTo(listOf(testVideoPlayerEntity))
        }
    }

    @Test
    fun observerVideosTest() = runTest {
        val videoPlayerEntity = VideoPlayerEntity(
            videoEntity = VideoEntity(
                id = 2,
                uri = uriMock,
                description = "Test Description2",
                createdAt = Date(1745751600000)), // Sunday, 27 April 2025 13:00:00 GMT+02:00)
            player = exoPlayerMock
        )

        every { getVideoPlayersUseCaseMock.invoke() } returns flow { emit(listOf(videoPlayerEntity)) }

        viewModel = createViewModel()

        viewModel.videosState.test {
            var state = awaitItem()

            assertThat(state.isLoading).isTrue()
            assertThat(state.videos).isEqualTo(listOf(testVideoPlayerEntity))

            state = awaitItem()

            assertThat(state.isLoading).isFalse()
            assertThat(state.videos).isEqualTo(listOf(videoPlayerEntity))
        }
    }

    @Test
    fun onUiEvent_OnClearAllPlayersTest() {
        viewModel.onUIEvent(OnClearAllPlayers)

        viewModel.videosState.value.videos.forEach {
            verify {
                it.player.release()
            }
        }
    }

    @Test
    fun onUiEvent_OnPlayVideoEvent() {
        val videoPlayers = listOf(
            VideoPlayerEntity(
                videoEntity = VideoEntity(
                    id = 1,
                    uri = uriMock,
                    description = "Test Description",
                    createdAt = Date(1745751600000)), // Sunday, 27 April 2025 13:00:00 GMT+02:00)
                player = exoPlayerMock
            ),
            VideoPlayerEntity(
                videoEntity = VideoEntity(
                    id = 2,
                    uri = uriMock,
                    description = "Test Description2",
                    createdAt = Date(1745751600000)), // Sunday, 27 April 2025 13:00:00 GMT+02:00)
                player = exoPlayerMock2
            )
        )

        (viewModel.videosState as MutableStateFlow).value = VideosState(videos = videoPlayers, isLoading = false)

        viewModel.onUIEvent(OnPlayVideoEvent(index = 0))

        verify {
            exoPlayerMock.setMediaItem(any())
            exoPlayerMock.prepare()
        }

        verify(exactly = 0) {
            exoPlayerMock.stop()
            exoPlayerMock.seekTo(any())
        }

        verify(exactly = 0) {
            exoPlayerMock2.setMediaItem(any())
            exoPlayerMock2.prepare()
        }

        verify {
            exoPlayerMock2.stop()
            exoPlayerMock2.seekTo(any())
        }
    }
}