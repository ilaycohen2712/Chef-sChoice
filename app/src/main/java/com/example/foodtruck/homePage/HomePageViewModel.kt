package com.example.foodtruck.homePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.foodtruck.database.DatabaseInstance
import com.example.foodtruck.database.dao.RecipeDao
import com.example.foodtruck.database.entities.Recipe

class HomePageViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeDao: RecipeDao = DatabaseInstance.getDatabase(application).recipeDao()

    val recipes: LiveData<List<Recipe>> = recipeDao.getAllRecipes()
}