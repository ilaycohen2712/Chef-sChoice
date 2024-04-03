package com.example.foodtruck.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.foodtruck.database.entities.Place


@Dao
interface PlaceDao {
    @Insert
    fun insertPlace(place: Place)
    @Query("SELECT * FROM places")
    fun getAllPlaces(): LiveData<List<Place>>

    @Query("SELECT * FROM places WHERE id = :placeId")
    fun getPlaceById(placeId: Int): LiveData<Place>

    @Query("SELECT * FROM places WHERE name = :placeName")
    fun getPlaceById(placeName: String): LiveData<Place>

    @Update
    suspend fun updatePlace(place: Place)

}