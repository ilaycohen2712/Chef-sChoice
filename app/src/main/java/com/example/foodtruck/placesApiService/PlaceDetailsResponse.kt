package com.example.foodtruck.placesApiService

data class PlaceResponse(val candidates: List<PlaceDetails>)
data class PlaceDetails(val name: String, val rating:Double)