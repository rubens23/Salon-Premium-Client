package com.rubens.salonpremiumclient.data.models

data class Employee(
    val name: String = "",
    val job: String = "",
    val imageLink: String = "",
    var isSelected: Boolean = false,
    var employeeKey: String = "",
    var servicesList: ArrayList<String> = ArrayList(),
    var dtDayOff: String = "",
    var employeeAvailable: Boolean = true
)
