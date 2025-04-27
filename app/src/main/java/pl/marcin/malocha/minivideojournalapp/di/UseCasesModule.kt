package pl.marcin.malocha.minivideojournalapp.di

import org.koin.dsl.module
import pl.marcin.malocha.minivideojournalapp.domain.usecase.file.CreateVideoUriUseCase
import pl.marcin.malocha.minivideojournalapp.domain.usecase.videos.GetVideoPlayersUseCase
import pl.marcin.malocha.minivideojournalapp.domain.usecase.videos.GetVideosUseCase
import pl.marcin.malocha.minivideojournalapp.domain.usecase.videos.InsertVideoUseCase
import pl.marcin.malocha.minivideojournalapp.domain.usecase.videos.VideosUseCases

val useCasesModule = module {
    single { InsertVideoUseCase(get()) }
    single { GetVideosUseCase(get()) }
    single { VideosUseCases(get(), get()) }
    single { CreateVideoUriUseCase(get()) }
    single { GetVideoPlayersUseCase(get(), get()) }
}