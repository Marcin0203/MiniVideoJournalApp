package pl.marcin.malocha.minivideojournalapp.app.reels

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.ui.compose.PlayerSurface
import pl.marcin.malocha.minivideojournalapp.app.reels.PlayerEvents.OnClearAllPlayers
import pl.marcin.malocha.minivideojournalapp.app.reels.PlayerEvents.OnPlayVideoEvent

@Composable
fun ReelsContent(
    viewModel: IReelsViewModel,
) {
    val videosState = viewModel.videosState.collectAsState().value
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { videosState.videos.size }
    )
    var lastPage by remember { mutableIntStateOf(-1) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onUIEvent(OnClearAllPlayers)
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
    ) { index ->

            if (lastPage != pagerState.settledPage) {
                viewModel.onUIEvent(OnPlayVideoEvent(index = pagerState.settledPage))

                lastPage = pagerState.settledPage
            }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerSurface(
                player = videosState.videos[index].player,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}