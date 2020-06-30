package com.bigbang.myplacecompass.model.network

import com.bigbang.myplacecompass.model.data.PlacesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


//https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyA5rJh9FeteViW7n9M3iORS7d8L7W75wVI
interface PlacesService {
    @GET("maps/api/place/nearbysearch/json")
    fun getNearByPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") apiKey: String
    ) : Observable<PlacesResponse>

}