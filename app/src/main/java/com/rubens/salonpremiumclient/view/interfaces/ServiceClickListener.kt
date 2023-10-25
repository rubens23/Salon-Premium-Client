package com.rubens.salonpremiumclient.view.interfaces

import com.rubens.salonpremiumclient.data.models.Service

interface ServiceClickListener {
    fun onServiceClick(service: Service)
}