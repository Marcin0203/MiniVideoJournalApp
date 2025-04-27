package pl.marcin.malocha.minivideojournalapp.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import pl.marcin.malocha.minivideojournalapp.di.dbModule
import pl.marcin.malocha.minivideojournalapp.di.presentationModule
import pl.marcin.malocha.minivideojournalapp.di.repositoriesModule
import pl.marcin.malocha.minivideojournalapp.di.useCasesModule

class VideoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@VideoApplication)
            modules(listOf(presentationModule, repositoriesModule, useCasesModule, dbModule))
        }
    }
}