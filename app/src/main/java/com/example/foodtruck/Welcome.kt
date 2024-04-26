package com.example.foodtruck

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foodtruck.homePage.HomePageFragment
import com.example.foodtruck.login.LoginActivity
import com.example.foodtruck.signup.SignUpActivity
import com.example.foodtruck.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Welcome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Check if a user is already logged in using Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is already logged in, navigate to MainActivity
            navigateToMainActivity()
            return // Finish the Welcome activity to prevent it from being shown
        }

        // initialize the database with a new Recipe

        Log.d("RECIPE", "Before initializing database with recipe")

        Utils.initializeDatabaseWithRecipe(application, "dish_photos/lasagna.jpg", "lasagna", "2 Tomatoes, 100g Cheese, 1 box of lasagna papers, pepper, salt, 4 large eggs, cottage cheese, pepper, salt, 4 large eggs, cottage cheese, pepper, salt, 4 large eggs, cottage cheese, pepper, salt, 4 large eggs, cottage cheese", "mix and put in the oven, mix and put in the oven, mix and put in the oven, mix and put in the oven", 750)

        Log.d("RECIPE", "After initializing database with recipe")


        val registerButton: Button = findViewById(R.id.create_account_button)
        val loginButton: Button = findViewById(R.id.login_button)

        registerButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish() // Finish the Welcome activity to make it disappear
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Finish the Welcome activity to make it disappear
        }

    }
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
