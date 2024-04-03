package com.example.foodtruck.utils

import android.app.Application
import com.example.foodtruck.database.DatabaseInstance
import com.example.foodtruck.database.entities.Place
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Utils {
    fun initializeDatabaseWithPlace(application: Application, imageUrl: String, name: String, address: String, description: String) {

        val storageReference = FirebaseStorage.getInstance().getReference(imageUrl)
        // Start the download of the URL
        storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
            val imageUrl = downloadUri.toString()
            // Directly use the imageUrl parameter
            val place = Place(
                name = name,
                address = address,
                description = description,
                placePhoto = imageUrl,
                rating = 0f // Assuming the initial rating is 0 for all new places
            )

            // Get your Room database and Dao
            val db = DatabaseInstance.getDatabase(application)
            val placeDao = db.placeDao()

            CoroutineScope(Dispatchers.IO).launch {
                placeDao.insertPlace(place)
            }
        }
    }

}