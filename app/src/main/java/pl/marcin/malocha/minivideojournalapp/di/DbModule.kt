package pl.marcin.malocha.minivideojournalapp.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.dsl.module
import pl.marcin.malocha.minivideojournalapp.VideoDatabase
import pl.marcin.malocha.minivideojournalapp.data.db.VideoDao
import pl.marcin.malocha.minivideojournalapp.data.db.VideoDaoImpl
import pl.marcin.malocha.minivideojournalapp.data.db.VideoDatabaseHelper

val dbModule = module {
    single {
        val driver = AndroidSqliteDriver(VideoDatabase.Schema, get(), "VideoDatabase.db")

        VideoDatabase(driver)
    }

    single { VideoDatabaseHelper(get()) }

    single<VideoDao> { VideoDaoImpl(get()) }
}