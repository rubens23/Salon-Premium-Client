package com.rubens.salonpremiumclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubens.salonpremiumclient.data.repositories.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class SharedViewModel : ViewModel(){

    private val _mostrarBottomNavigation: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)
    val mostrarBottomNavigation = _mostrarBottomNavigation


    fun mostrarBottomNavigationDepoisDoLogin(){
        viewModelScope.launch {
            _mostrarBottomNavigation.emit(true)
        }
    }


}