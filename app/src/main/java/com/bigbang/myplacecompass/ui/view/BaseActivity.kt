package com.bigbang.myplacecompass.ui.view

import androidx.appcompat.app.AppCompatActivity
import com.bigbang.myplacecompass.PlacesApp

abstract class BaseActivity : AppCompatActivity() {
    fun getRepository() = (application as PlacesApp).getRepository()
}