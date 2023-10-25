package com.rubens.salonpremiumclient.utils

import com.example.appsalaobelezaclientemvvm.model.dataClasses.Day

interface TypeConverter {
    fun transformDayListOfMapsToDayListOfDays(listaDeDias: List<Map<String, String>>, dateFormatted: List<String>): ArrayList<Day>
}