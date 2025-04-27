package pl.marcin.malocha.minivideojournalapp.app.reels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.marcin.malocha.minivideojournalapp.app.base.BaseUIEvent
import pl.marcin.malocha.minivideojournalapp.app.base.IBaseViewModel
import pl.marcin.malocha.minivideojournalapp.app.reels.PlayerEvents.OnClearAllPlayers
import pl.marcin.malocha.minivideojournalapp.app.reels.PlayerEvents.OnPlayVideoEvent
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoPlayerEntity
import pl.marcin.malocha.minivideojournalapp.domain.usecase.videos.GetVideoPlayersUseCase

interface IReelsViewModel : IBaseViewModel {
    val videosState: StateFlow<VideosState>
}

class ReelsViewModel(
    videoPlayerEntity: VideoPlayerEntity,
    private val getVideoPlayersUseCase: GetVideoPlayersUseCase
) : IReelsViewModel, ViewModel() {
    private val _videosState: MutableStateFlow<VideosState> = MutableStateFlow(
        VideosState(videos = listOf(videoPlayerEntity), isLoading = true)
    )
    override val videosState: StateFlow<VideosState> = _videosState

    init {
        observerVideos()
    }

    override fun onUIEvent(uiEvent: BaseUIEvent) {
        when (uiEvent) {
            is OnPlayVideoEvent -> {
                playVideo(uiEvent.index)
            }
            is OnClearAllPlayers -> {
                clearAllPlayers()
            }
        }
    }

    private fun playVideo(indexToPlay: Int) {
        val videos = videosState.value.videos

        videos.forEachIndexed { index, videoPlayerEntity ->
            if (indexToPlay == index) {
                val mediaItem = MediaItem.fromUri(videoPlayerEntity.videoEntity.uri)

                videoPlayerEntity.player.apply {
                    repeatMode = ExoPlayer.REPEAT_MODE_ALL
                    setMediaItem(mediaItem)
                    prepare()
                    playWhenReady = true
                }
            } else {
                videoPlayerEntity.player.apply {
                    stop()
                    playWhenReady = false
                    seekTo(0)
                }
            }
        }
    }

    private fun clearAllPlayers() {
        val videos = videosState.value.videos

        videos.forEach {
            it.player.apply {
                release()
            }
        }
    }

    //todo
    private fun handleError(error: PlaybackException) {
        when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> {
                // Handle network connection error
                println("Network connection error")
            }

            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> {
                // Handle file not found error
                println("File not found")
            }

            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> {
                // Handle decoder initialization error
                println("Decoder initialization error")
            }

            else -> {
                // Handle other types of errors
                println("Other error: ${error.message}")
            }
        }
    }

    private fun observerVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            getVideoPlayersUseCase().collect { newVideos ->
                _videosState.update {
                    videosState.value.copy(
                        videos = newVideos,
                        isLoading = false
                    )
                }
            }
        }
    }
}

// todo add currentVideoIndex
data class VideosState(
    val videos: List<VideoPlayerEntity>,
    val isLoading: Boolean
)

sealed class PlayerEvents : BaseUIEvent() {
    data class OnPlayVideoEvent(val index: Int) : PlayerEvents()
    data object OnClearAllPlayers : PlayerEvents()
}