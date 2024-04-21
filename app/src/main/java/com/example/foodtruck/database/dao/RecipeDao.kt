package com.example.foodtruck.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.foodtruck.database.entities.Recipe


@Dao
interface RecipeDao {
    @Insert
    fun insertRecipe(recipe: Recipe)
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: Int): LiveData<Recipe>

    @Query("SELECT * FROM recipes WHERE name = :dishName")
    fun getRecipeById(dishName: String): LiveData<Recipe>

    @Update
    suspend fun updateRecipe(recipe: Recipe)

}