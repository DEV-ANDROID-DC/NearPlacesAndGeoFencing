package com.bigbang.myplacecompass.model.network

import com.bigbang.myplacecompass.model.data.PlacesResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object PlacesRetrofitInstance {

    private var placesService: PlacesService

    init {
        placesService = createPlacesService(createRetrofit())
    }

    private fun createRetrofit(): Retrofit =
        Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://maps.googleapis.com/")
            .build()


    private fun createPlacesService(retrofit: Retrofit) = retrofit.create(PlacesService::class.java)

    fun getPlaces(userLocation: String, radius: Int, type: String): Observable<PlacesResponse> =
        placesService.getNearByPlaces(
            userLocation,
            radius,
            type,
            "AIzaSyA5rJh9FeteViW7n9M3iORS7d8L7W75wVI"
        )
}