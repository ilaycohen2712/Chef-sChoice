package com.example.foodtruck.places

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.foodtruck.database.DatabaseInstance
import com.example.foodtruck.database.entities.Place

class PlaceViewModel(application: Application) : AndroidViewModel(application) {
    private val placeDao = DatabaseInstance.getDatabase(application).placeDao()


    fun getPlaceById(placeId: Int): LiveData<Place> {
        return placeDao.getPlaceById(placeId)
    }

}