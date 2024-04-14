package com.example.foodtruck.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.foodtruck.database.dao.PlaceDao
import com.example.foodtruck.database.dao.PostDao
import com.example.foodtruck.database.dao.UserDao
import com.example.foodtruck.database.entities.Place
import com.example.foodtruck.database.entities.Post
import com.example.foodtruck.database.entities.User

@Database(entities = [User::class, Place::class, Post::class], version = 7)
abstract class FoodTruckDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun placeDao(): PlaceDao
    abstract fun postDao(): PostDao
}