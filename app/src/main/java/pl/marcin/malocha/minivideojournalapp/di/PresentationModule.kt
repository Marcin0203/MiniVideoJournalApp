package pl.marcin.malocha.minivideojournalapp.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pl.marcin.malocha.minivideojournalapp.app.reels.ReelsViewModel
import pl.marcin.malocha.minivideojournalapp.app.videoList.VideoListViewModel
import pl.marcin.malocha.minivideojournalapp.data.mapper.toPlayer
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity

val presentationModule = module {
    viewModel {
        VideoListViewModel(get(), get())
    }

    viewModel { (videoEntity: VideoEntity) ->
        ReelsViewModel(videoEntity.toPlayer(get()), get())
    }
}