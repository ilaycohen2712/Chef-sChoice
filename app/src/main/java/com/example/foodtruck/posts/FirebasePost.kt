package com.example.foodtruck.posts

data class FirebasePost(
    val userId: String? = null,
    val comment: String? = null,
    val photo: String? = null,
    val dishName: String? = null
)