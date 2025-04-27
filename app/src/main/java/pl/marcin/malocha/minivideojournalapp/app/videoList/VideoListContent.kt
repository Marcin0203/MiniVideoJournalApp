package pl.marcin.malocha.minivideojournalapp.app.videoList

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CaptureVideo
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel
import pl.marcin.malocha.minivideojournalapp.R
import pl.marcin.malocha.minivideojournalapp.app.base.BaseUIEvent
import pl.marcin.malocha.minivideojournalapp.app.helpers.collectAsEffect
import pl.marcin.malocha.minivideojournalapp.app.helpers.getActivityFromLocalContext
import pl.marcin.malocha.minivideojournalapp.app.theme.DialogBackground
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnCapturedVideoEvent
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnCheckedSelfPermissionEvent
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnHideDeniedDialog
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnHideRationalDialog
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnPermissionGrantedEvent
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnPermissionNotGrantedEvent
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListEvents.OnRecordVideoButtonClickedEvent
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity
import pl.marcin.malocha.minivideojournalapp.ui.theme.Typography

@Composable
fun VideoListContent(
    viewModel: IVideoListViewModel = koinViewModel<VideoListViewModel>(),
    onItemClick: (videoEntity: VideoEntity) -> Unit = { }
) {
    val context = LocalContext.current
    val dialogStates = viewModel.dialogStates.collectAsState().value
    val videoList = viewModel.videosState.collectAsState().value

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.onUIEvent(OnPermissionGrantedEvent)
        } else {
            context.getActivityFromLocalContext()?.let { activityContext ->
                val shouldShowRequestPermissionRationale =
                    activityContext.shouldShowRequestPermissionRationale(VideoListFragmentPermission)
                
                viewModel.onUIEvent(OnPermissionNotGrantedEvent(
                    shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale)
                )
            }
        }
    }

    val recordVideoLauncher = rememberLauncherForActivityResult(contract = CaptureVideo()) { isSaved ->
            viewModel.onUIEvent(OnCapturedVideoEvent(isSaved))
    }

    viewModel.actions.collectAsEffect { action ->
        when (action) {
            is VideoListActions.CheckSelfPermissionAction -> {
                val checkSelfPermissionResult = context.checkSelfPermission(VideoListFragmentPermission)

                viewModel.onUIEvent(OnCheckedSelfPermissionEvent(result = checkSelfPermissionResult))
            }
            is VideoListActions.RequestCameraPermission -> {
                permissionLauncher.launch(VideoListFragmentPermission)
            }
            is VideoListActions.RecordVideoAction -> {
                recordVideoLauncher.launch(action.uri)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = { RecordVideoButton(viewModel) }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        if (videoList.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        painter = painterResource(R.drawable.ghost),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Ghost Icon"
                    )

                    Text(
                        text = stringResource(R.string.empty_video_list_message)
                    )
                }
            }

        } else {
            LazyColumn(modifier = modifier) {
                items(videoList) { video ->
                    Text(
                        modifier = Modifier.clickable {
                            onItemClick(video)
                        },
                        text = video.uri.toString()
                    )
                }
            }
        }
    }

    if (dialogStates.showRationaleDialog) {
        PermissionRationaleDialog (permissionLauncher) {
            viewModel.onUIEvent(OnHideRationalDialog)
        }
    }

    if (dialogStates.showDeniedDialog) {
        PermissionDeniedDialog {
            viewModel.onUIEvent(OnHideDeniedDialog)
        }
    }
}

@Composable
fun RecordVideoButton(viewModel: IVideoListViewModel) {
    FloatingActionButton(
        onClick = { viewModel.onUIEvent(OnRecordVideoButtonClickedEvent) }
    ) {
        Icon(Icons.Filled.Add, "New Video")
    }
}

@Composable
fun PermissionRationaleDialog(
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    onDismissRequest: () -> Unit
) {
    Dialog(
        title = stringResource(R.string.dialog_title),
        message = stringResource(R.string.rationale_dialog_message),
        positiveButtonText = stringResource(R.string.dialog_ok_button),
        onPositiveButtonClick = { permissionLauncher.launch(VideoListFragmentPermission) },
        onDismissRequest = onDismissRequest
    )
}

@Composable
fun PermissionDeniedDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        title = stringResource(R.string.dialog_title),
        message = stringResource(R.string.denied_dialog_message),
        positiveButtonText = stringResource(R.string.dialog_settings_button),
        onPositiveButtonClick = { context.getActivityFromLocalContext()?.let {
            openAppSettings(it)
        } },
        onDismissRequest = onDismissRequest
    )
}

private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )

    context.startActivity(intent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dialog(
    title: String,
    message: String,
    positiveButtonText: String,
    onPositiveButtonClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    BasicAlertDialog(
        modifier = Modifier.background(color = DialogBackground),
        onDismissRequest = { },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = Typography.titleLarge
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = message,
                style = Typography.bodyMedium
            )

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = positiveButtonText,
                    style = Typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.clickable {
                        onPositiveButtonClick.invoke()

                        onDismissRequest.invoke()
                    }
                )

                Text(
                    text = stringResource(R.string.dialog_close_button),
                    style = Typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.clickable {
                        onDismissRequest.invoke()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoListPreview() {
    VideoListContent(viewModel = VideoListViewModelMock())
}

@Preview(showBackground = true)
@Composable
fun VideoListPreview_ShowRationaleDialog() {
    VideoListContent(
        viewModel = VideoListViewModelMock(
            dialogStates = MutableStateFlow(DialogStates(showRationaleDialog = true))
        )
    )
}

@Preview(showBackground = true)
@Composable
fun VideoListPreview_ShowDeniedDialog() {
    VideoListContent(
        viewModel = VideoListViewModelMock(
            dialogStates = MutableStateFlow(DialogStates(showDeniedDialog = true))
        )
    )
}

class VideoListViewModelMock(
    override val actions: SharedFlow<VideoListActions> = MutableSharedFlow(),
    override val dialogStates: StateFlow<DialogStates> = MutableStateFlow(DialogStates()),
    override val videosState: StateFlow<List<VideoEntity>> = MutableStateFlow(emptyList())
) : IVideoListViewModel {
    override fun onUIEvent(uiEvent: BaseUIEvent) {}
}