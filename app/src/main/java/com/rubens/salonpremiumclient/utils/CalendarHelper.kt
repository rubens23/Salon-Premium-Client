package com.rubens.salonpremiumclient.utils

import com.rubens.salonpremiumclient.data.models.Hour
import java.util.Locale

interface CalendarHelper {

    fun obterListaDeDias(
        mes: String,
        ano: String
    ): List<Map<String, String>>

    fun getHoursListByPeriodOfDay(selectedPeriodOfDay: String): ArrayList<Hour>

    fun obterNumeroDoMes(nomeDoMes: String): String

    fun obterListaDeDiasComDiaDeDoisDigitos(listaDeDiasFormatados: ArrayList<String>): List<String>
    fun obterDiaComDoisDigitos(numberDay: String): String


    fun pegarDiaDaSemanaPorStringDeData(data: String): String
}