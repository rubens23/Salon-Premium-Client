package com.rubens.salonpremiumclient.utils

import com.rubens.salonpremiumclient.data.models.Hour
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarHelperImpl: CalendarHelper {

    override fun obterListaDeDias(
        mes: String,
        ano: String
    ): List<Map<String, String>> {
        val listaDeDias = mutableListOf<Map<String, String>>()

        val formatoDiaSemana = SimpleDateFormat("E", Locale.getDefault())
        val formatoNumeroDia = SimpleDateFormat("d", Locale.getDefault())

        val calendario = Calendar.getInstance(Locale.getDefault())
        calendario.clear()
        calendario.set(Calendar.MONTH, SimpleDateFormat("MMMM", Locale.getDefault()).parse(mes)?.month ?: 0)
        calendario.set(Calendar.YEAR, ano.toInt())

        val ultimoDiaDoMes = calendario.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..ultimoDiaDoMes) {
            calendario.set(Calendar.DAY_OF_MONTH, i)

            val diaDaSemana = formatoDiaSemana.format(calendario.time)
            val numeroDoDia = formatoNumeroDia.format(calendario.time)

            val mapa = mapOf("dia" to diaDaSemana, "numero" to numeroDoDia)
            listaDeDias.add(mapa)
        }

        return listaDeDias
    }

    override fun getHoursListByPeriodOfDay(selectedPeriodOfDay: String): ArrayList<Hour> {
        return when(selectedPeriodOfDay){
            "Manhã"->{
                arrayListOf(
                    Hour("08:00", false),
                    Hour("09:00", false),
                    Hour("10:00", false),
                    // Adicione mais horas conforme necessário
                )
            }
            "Tarde"->{
                arrayListOf(
                    Hour("12:00", false),
                    Hour("13:00", false),
                    Hour("14:00", false),
                    // Adicione mais horas conforme necessário
                )
            }
            "Noite"->{
                arrayListOf(
                    Hour("18:00", false),
                    Hour("19:00", false),
                    Hour("20:00", false),
                    // Adicione mais horas conforme necessário
                )
            }
            else-> arrayListOf<Hour>()
        }
    }

    override fun obterNumeroDoMes(nomeDoMes: String): String {
        val calendar = Calendar.getInstance()
        val locale = Locale.getDefault()
        val monthNames = DateFormatSymbols(locale).months
        val mes = monthNames.indexOfFirst { it.equals(nomeDoMes, ignoreCase = true) }+ 1

        return String.format("%02d", mes)
    }

    override fun obterListaDeDiasComDiaDeDoisDigitos(listaDeDiasFormatados: ArrayList<String>): List<String> {
        var listaFormatada = arrayListOf<String>()
        listaDeDiasFormatados.forEach {
            data->
            val parts = data.split("/")
            val day = parts[0].toInt()
            val month = parts[1]
            val year = parts[2]

            val formattedDay = String.format("%02d", day)
            listaFormatada.add("$formattedDay/$month/$year")

        }
        return listaFormatada
    }

    override fun obterDiaComDoisDigitos(numberDay: String): String {
        return String.format("%02d", numberDay.toInt())
    }

    override fun pegarDiaDaSemanaPorStringDeData(data: String): String {
        val formato = SimpleDateFormat("dd/MM/yyyy")
        val dataFormatada = formato.parse(data)
        val calendario = Calendar.getInstance()
        calendario.time = dataFormatada
        val diaDaSemana = calendario.get(Calendar.DAY_OF_WEEK)

        return when (diaDaSemana) {
            Calendar.SUNDAY -> "dom"
            Calendar.MONDAY -> "seg"
            Calendar.TUESDAY -> "ter"
            Calendar.WEDNESDAY -> "qua"
            Calendar.THURSDAY -> "qui"
            Calendar.FRIDAY -> "sex"
            Calendar.SATURDAY -> "sab"
            else -> throw IllegalArgumentException("Data inválida")
        }
    }

}