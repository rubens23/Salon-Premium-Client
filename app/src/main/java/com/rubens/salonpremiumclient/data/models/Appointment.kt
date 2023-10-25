package com.rubens.salonpremiumclient.data.models

import java.io.Serializable

data class Appointment(
    val day: String,
    val month: String,
    val employee: String,
    val hour: String,
    val service: String,
    val clientName: String,
    val clientUid: String?,
    val employeeKey: String,
    val appointmentDayFormatted: String,
    val confirmacaoAtendimento: String = "aguardando confirmacao",
    var appointmentId: String = ""
): Serializable
