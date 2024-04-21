package com.example.foodtruck.profile

data class Comment(
    val commentId: String,
    val dishName: String,
    val comment: String,
    var photo: String
)