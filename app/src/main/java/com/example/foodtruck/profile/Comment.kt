package com.example.foodtruck.profile

data class Comment(
    val commentId: String,
    val placeName: String,
    val comment: String,
    var photo: String
)