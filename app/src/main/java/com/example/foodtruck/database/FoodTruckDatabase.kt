package com.example.foodtruck.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.foodtruck.database.dao.RecipeDao
import com.example.foodtruck.database.dao.PostDao
import com.example.foodtruck.database.dao.UserDao
import com.example.foodtruck.database.entities.Recipe
import com.example.foodtruck.database.entities.Post
import com.example.foodtruck.database.entities.User

@Database(entities = [User::class, Recipe::class, Post::class], version = 9)
abstract class FoodTruckDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao
    abstract fun postDao(): PostDao
}