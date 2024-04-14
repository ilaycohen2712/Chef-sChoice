package com.example.foodtruck.database

import android.content.Context
import androidx.room.Room

object DatabaseInstance {

    @Volatile
    private var INSTANCE: FoodTruckDatabase? = null

    fun getDatabase(context: Context): FoodTruckDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                FoodTruckDatabase::class.java,
                "foodtruck-424ff-default-rtdb"
            )
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }
}