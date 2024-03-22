package com.example.foodtruck

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth=Firebase.auth
        val registertext: TextView= findViewById(R.id.textView_register_now)
        registertext.setOnClickListener {
            val intent=Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        val loginButton:Button= findViewById(R.id.button_login)
        loginButton.setOnClickListener {
            performLogin()
        }


        //login from exsisting user
        //adding a login button
    }
    private fun performLogin(){
        val email : EditText =findViewById(R.id.editText_email_login)
        val password: EditText =findViewById(R.id.editTextTextPassword_login)
        //null check on input
        if(email.text.isEmpty()||password.text.isEmpty())
        {
            Toast.makeText(this,"Please fill all the fields",Toast.LENGTH_SHORT).show()
            return
        }
        val emailInput=email.text.toString()
        val passwordInput=password.text.toString()

        auth.signInWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, navigte to the MainActivity
                    val intent=Intent(this ,MainActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(
                        baseContext,
                        "Success",
                        Toast.LENGTH_SHORT,
                    ).show()

                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "Authentication failed. ${it.localizedMessage}",
                    Toast.LENGTH_SHORT,
                ).show()

            }
    }
}