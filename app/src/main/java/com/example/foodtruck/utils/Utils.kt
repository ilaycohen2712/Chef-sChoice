package com.example.foodtruck.utils

import android.app.Application
import com.example.foodtruck.database.DatabaseInstance
import com.example.foodtruck.database.entities.Recipe
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Utils {
    fun initializeDatabaseWithRecipe(application: Application, imageUrl: String, name: String, materials: String, preparation: String,calories: Int) {

        val storageReference = FirebaseStorage.getInstance().getReference(imageUrl)
        // Start the download of the URL
        storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
            val imageUrl = downloadUri.toString()
            // Directly use the imageUrl parameter
            val recipe = Recipe(
                name = name,
                materials = materials,
                preparation = preparation,
                dishPhoto = imageUrl,
                calories = calories,
            )

            // Get your Room database and Dao
            val db = DatabaseInstance.getDatabase(application)
            val recipeDao = db.recipeDao()

            CoroutineScope(Dispatchers.IO).launch {
                recipeDao.insertRecipe(recipe)
            }
        }
    }

}