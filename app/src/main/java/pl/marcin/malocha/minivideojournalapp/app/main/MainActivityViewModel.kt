package pl.marcin.malocha.minivideojournalapp.app.main

import androidx.lifecycle.ViewModel
import pl.marcin.malocha.minivideojournalapp.app.base.BaseUIEvent
import pl.marcin.malocha.minivideojournalapp.app.base.IBaseViewModel

interface IMainActivityViewModel : IBaseViewModel

class MainActivityViewModel : IMainActivityViewModel, ViewModel() {


    override fun onUIEvent(uiEvent: BaseUIEvent) {
    }
}