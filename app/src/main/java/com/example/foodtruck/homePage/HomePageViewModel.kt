package com.example.foodtruck.homePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.foodtruck.database.DatabaseInstance
import com.example.foodtruck.database.dao.PlaceDao
import com.example.foodtruck.database.entities.Place

class HomePageViewModel(application: Application) : AndroidViewModel(application) {

    private val placeDao: PlaceDao = DatabaseInstance.getDatabase(application).placeDao()

    val places: LiveData<List<Place>> = placeDao.getAllPlaces()
}