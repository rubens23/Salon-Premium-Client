package com.rubens.salonpremiumclient.utils

import com.example.appsalaobelezaclientemvvm.model.dataClasses.Day

class TypeConverterImpl: TypeConverter {
    override fun transformDayListOfMapsToDayListOfDays(listaDeDias: List<Map<String, String>>, dateFormatted: List<String>): ArrayList<Day> {

        var list = arrayListOf<Day>()
        for (i in listaDeDias.indices){
            val map = listaDeDias[i]
            list.add(
                Day(
                    labelDay = map["dia"]!!,
                    numberDay = map["numero"]!!,
                    isSelected = false,
                    dateFormatted = dateFormatted[i]
                )
            )

        }

        return list
    }
}