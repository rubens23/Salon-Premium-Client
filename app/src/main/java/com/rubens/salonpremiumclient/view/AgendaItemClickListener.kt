package com.rubens.salonpremiumclient.view

import com.rubens.salonpremiumclient.data.models.Appointment

interface AgendaItemClickListener {
    fun onAgendaItemClickListener(appointment: Appointment)

    fun pegarDiaDaSemanaAbreviado(data: String): String
}