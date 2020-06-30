package com.bigbang.myplacecompass.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bigbang.myplacecompass.repository.PlacesRepository
import com.bigbang.myplacecompass.model.data.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CompassViewModel(private val placesRepository: PlacesRepository): ViewModel() {

    val placesMutableData: MutableLiveData<List<Result>> = MutableLiveData()
//    val stateLiveData: MutableLiveData<List<Result>> = MutableLiveData()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun getGetNearbyPlaces( location: String, radius: Int, type: String) {
        compositeDisposable.clear()
        compositeDisposable.add(placesRepository.getPlacesNearby(
            location, radius, type
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({ placesList ->
                Log.d("TAG_X Repository", placesList.toString())
                placesMutableData.value = placesList
            }, { throwable ->
                Log.d("TAG_X", "${throwable.localizedMessage}")
            })
        )
    }


}