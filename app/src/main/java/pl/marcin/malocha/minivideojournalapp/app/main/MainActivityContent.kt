package pl.marcin.malocha.minivideojournalapp.app.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import pl.marcin.malocha.minivideojournalapp.app.base.BaseUIEvent
import pl.marcin.malocha.minivideojournalapp.app.navigation.CustomNavType
import pl.marcin.malocha.minivideojournalapp.app.navigation.NavGraph
import pl.marcin.malocha.minivideojournalapp.app.reels.ReelsContent
import pl.marcin.malocha.minivideojournalapp.app.reels.ReelsViewModel
import pl.marcin.malocha.minivideojournalapp.app.theme.MiniVideoJournalAppTheme
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListContent
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity
import kotlin.reflect.typeOf

@Composable
fun MainActivityContent(viewModel: IMainActivityViewModel = viewModel<MainActivityViewModel>()) {
    MiniVideoJournalAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = NavGraph.VideoListRoute,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<NavGraph.VideoListRoute> {
                    VideoListContent { videoEntity ->
                        navController.navigate(NavGraph.PlayerRoute(videoEntity = videoEntity))
                    }
                }

                composable<NavGraph.PlayerRoute>(
                    typeMap = mapOf(typeOf<VideoEntity>() to CustomNavType.VideoType)
                ) {
                    val arguments = it.toRoute<NavGraph.PlayerRoute>()
                    val reelsViewModel: ReelsViewModel = koinViewModel(parameters = { parametersOf(arguments.videoEntity) })

                    ReelsContent(viewModel = reelsViewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    MainActivityContent(viewModel = MainActivityViewModelMock())
}

class MainActivityViewModelMock : IMainActivityViewModel {
    override fun onUIEvent(uiEvent: BaseUIEvent) {}
}