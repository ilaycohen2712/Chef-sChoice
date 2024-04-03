package com.example.foodtruck

import android.content.Intent
import android.os.Bundle
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

        // initialize the database with a new place

        Utils.initializeDatabaseWithPlace(application, "cafeEurope.jpg", "קפה אירופה", "שדרות רוטשילד 9, תל אביב-יפו", "מסעדה צנועה עם חצר שבה מגישים מנות יצירתיות, עם תפריט בראנץ', יינות וקוקטיילים.")
        Utils.initializeDatabaseWithPlace(application, "sunyoung.jpg", "סאן יאנג", "ברנר 14, תל אביב-יפו", "מסעדת פיוז'ן אסיאתי מודרנית שבה מגישים סושי, ארוחות בוקר מקומיות וסטייקים במתחם מסוגנן ומאוורר.")
        Utils.initializeDatabaseWithPlace(application, "rustico.jpg", "רוסטיקו רוטשילד", "שדרות רוטשילד 15, תל אביב-יפו", "נמצאת בלב של תל אביב, ברחוב היפה והמרכזי ביותר של העיר. קחו לעצמכם פסק זמן קטן ותנו קפיצה לאיטליה באמצע היום או במהלך הערב.")
        Utils.initializeDatabaseWithPlace(application, "mayer.jpg", "קפה מאייר", "דיזנגוף 98, תל אביב-יפו", "ביסטרו ים תיכוני המשלב בין אווירה פריזאית לחומרי גלם מעולים מאגן הים התיכון בו כל אחד יכול למצוא את עצמו בכל שעה ביום.")
        Utils.initializeDatabaseWithPlace(application, "emesh.jpg", "אמש", "לילינבלום 30, תל אביב-יפו", "בר/מסעדה אופנתי עם תאורה דרמטית, מרפסת גדולה ותוססת בחצר ומתחם אירועים")


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
