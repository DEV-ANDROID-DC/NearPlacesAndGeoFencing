package com.bigbang.myplacecompass.repository

import com.bigbang.myplacecompass.model.data.PlacesResponse
import com.bigbang.myplacecompass.model.data.Result
import com.bigbang.myplacecompass.model.network.PlacesRetrofitInstance
import io.reactivex.Observable

object PlacesRepository {

    fun getPlacesNearby(userLocation: String, radius: Int, type: String) : Observable<List<Result>> {
        return PlacesRetrofitInstance.getPlaces(userLocation, radius, type).map {
            it.results
        }
    }

}