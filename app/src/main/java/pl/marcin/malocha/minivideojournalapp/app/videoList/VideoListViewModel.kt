package pl.marcin.malocha.minivideojournalapp.app.videoList

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.marcin.malocha.minivideojournalapp.app.base.BaseUIEvent
import pl.marcin.malocha.minivideojournalapp.app.base.IBaseViewModel
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListActions.CheckSelfPermissionAction
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListActions.RecordVideoAction
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListActions.RequestCameraPermission
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnCapturedVideoEvent
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnCheckedSelfPermissionEvent
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnHideDeniedDialog
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnHideRationalDialog
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnPermissionNotGrantedEvent
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnRecordVideoButtonClickedEvent
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity
import pl.marcin.malocha.minivideojournalapp.domain.usecase.file.CreateVideoUriUseCase
import pl.marcin.malocha.minivideojournalapp.domain.usecase.videos.VideosUseCases

interface IVideoListViewModel : IBaseViewModel {
    val actions: SharedFlow<VideoListActions>
    val dialogStates: StateFlow<DialogStates>
    val videosState: StateFlow<List<VideoEntity>>
}

const val VideoListFragmentPermission: String = CAMERA

class VideoListViewModel (
    private val createVideoUriUseCase: CreateVideoUriUseCase,
    private val videosUseCases: VideosUseCases
) : IVideoListViewModel, ViewModel() {
    private val _actions = MutableSharedFlow<VideoListActions>()
    override val actions = _actions.asSharedFlow()

    private val _dialogStates = MutableStateFlow(DialogStates())
    override val dialogStates: StateFlow<DialogStates> = _dialogStates

    private val _videosState = MutableStateFlow(emptyList<VideoEntity>())
    override val videosState: StateFlow<List<VideoEntity>> = _videosState

    private val tempVideoUriState = MutableStateFlow<Uri?>(null)

    init {
        observerVideos()
    }

    override fun onUIEvent(uiEvent: BaseUIEvent) {
        when (uiEvent) {
            is OnRecordVideoButtonClickedEvent -> {
                ensurePermission()
            }
            is OnCheckedSelfPermissionEvent -> {
                checkPermissionResult(uiEvent.result)
            }
            is OnHideRationalDialog -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _dialogStates.update { dialogStates.value.copy(showRationaleDialog = false) }
                }
            }
            is OnHideDeniedDialog -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _dialogStates.update { dialogStates.value.copy(showDeniedDialog = false) }
                }
            }
            is VideoListEvents.OnPermissionGrantedEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    recordVideo()
                }
            }
            is OnPermissionNotGrantedEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _dialogStates.update {
                        dialogStates.value.copy(
                            showRationaleDialog = uiEvent.shouldShowRequestPermissionRationale,
                            showDeniedDialog = !uiEvent.shouldShowRequestPermissionRationale
                        )
                    }
                }
            }
            is OnCapturedVideoEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (uiEvent.isSaved) {
                        tempVideoUriState.value?.let {
                            videosUseCases.insertVideo(uri = it, description = null)
                        }
                    }

                    tempVideoUriState.update { null }
                }
            }
        }
    }

    private fun ensurePermission() {
        viewModelScope.launch(Dispatchers.IO) {
            _actions.emit(CheckSelfPermissionAction)
        }
    }

    private fun checkPermissionResult(result: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (result == PERMISSION_GRANTED) {
                recordVideo()
            } else {
                _actions.emit(RequestCameraPermission)
            }
        }
    }

    private suspend fun recordVideo() {
        createVideoUriUseCase().let { tempUri ->
            tempVideoUriState.update { tempUri }

            _actions.emit(RecordVideoAction(uri = tempUri))
        }
    }

    private fun observerVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            videosUseCases.getVideos().collect { newVideos ->
                _videosState.update { newVideos }
            }
        }
    }
}

data class DialogStates(
    val showRationaleDialog: Boolean = false,
    val showDeniedDialog: Boolean = false
)

sealed class VideoListEvents : BaseUIEvent() {
    data object OnRecordVideoButtonClickedEvent : VideoListEvents()

    data object OnPermissionGrantedEvent : VideoListEvents()

    data object OnHideRationalDialog : VideoListEvents()

    data object OnHideDeniedDialog : VideoListEvents()

    data class OnPermissionNotGrantedEvent(val shouldShowRequestPermissionRationale: Boolean) : VideoListEvents()

    data class OnCheckedSelfPermissionEvent(val result: Int) : VideoListEvents()

    data class OnCapturedVideoEvent(val isSaved: Boolean) : VideoListEvents()
}

sealed class VideoListActions {
    data object RequestCameraPermission : VideoListActions()

    data object CheckSelfPermissionAction : VideoListActions()

    data class RecordVideoAction(val uri: Uri) : VideoListActions()
}