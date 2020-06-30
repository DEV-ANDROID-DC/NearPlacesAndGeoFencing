package com.bigbang.myplacecompass.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bigbang.myplacecompass.service.GeofenceTransitionsJobIntentService

class GeofenceBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    GeofenceTransitionsJobIntentService.enqueueWork(context, intent)
  }
}