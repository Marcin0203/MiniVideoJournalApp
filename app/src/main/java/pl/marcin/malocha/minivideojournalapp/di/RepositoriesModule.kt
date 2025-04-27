package pl.marcin.malocha.minivideojournalapp.di

import org.koin.dsl.module
import pl.marcin.malocha.minivideojournalapp.data.manager.MediaManager
import pl.marcin.malocha.minivideojournalapp.data.repository.FileRepositoryImpl
import pl.marcin.malocha.minivideojournalapp.data.repository.VideosRepositoryImpl
import pl.marcin.malocha.minivideojournalapp.domain.repository.FileRepository
import pl.marcin.malocha.minivideojournalapp.domain.repository.VideosRepository

val repositoriesModule = module {
    single<FileRepository> { FileRepositoryImpl(get()) }
    single<VideosRepository> { VideosRepositoryImpl(get()) }

    single { MediaManager(get()) }
}