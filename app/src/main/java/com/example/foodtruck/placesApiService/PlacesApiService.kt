package com.example.foodtruck.placesApiService

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {
    @GET("maps/api/place/findplacefromtext/json")
    fun findPlaceFromText(
        @Query("input") input: String,
        @Query("inputtype") inputType: String = "textquery",
        @Query("fields") fields: String = "name,rating",
        @Query("key") apiKey: String

    ): Call<PlaceResponse>
}