package com.bigbang.myplacecompass

import android.app.Application
import com.bigbang.myplacecompass.repository.ReminderRepository

class PlacesApp : Application(){
    private lateinit var repository: ReminderRepository

    override fun onCreate() {
        super.onCreate()
        repository = ReminderRepository(this)
    }

    fun getRepository() = repository
}