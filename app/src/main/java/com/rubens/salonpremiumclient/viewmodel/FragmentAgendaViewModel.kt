package com.rubens.salonpremiumclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubens.salonpremiumclient.data.models.Appointment
import com.rubens.salonpremiumclient.data.repositories.FirebaseRepository
import com.rubens.salonpremiumclient.utils.CalendarHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentAgendaViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val calendarHelper: CalendarHelper
) : ViewModel() {

    private val _pegouListaDeAppointments: MutableSharedFlow<ArrayList<Appointment>> = MutableSharedFlow(replay = 0)
    val pegouListaDeAppointments = _pegouListaDeAppointments

    fun pegarTodasAppointmentsDoCliente() {
        firebaseRepository.pegarTodosAppointmentsDoCliente {
            listaDeAppointments->
            viewModelScope.launch {
                _pegouListaDeAppointments.emit(listaDeAppointments)
            }

        }
    }

    fun pegarDiaDaSemanaAbreviado(data: String): String {
        return calendarHelper.pegarDiaDaSemanaPorStringDeData(data)

    }

}