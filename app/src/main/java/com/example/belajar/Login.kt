package com.example.belajar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        val enter: Button = findViewById(R.id.Enter)
        val regis: TextView = findViewById(R.id.regis)

        enter.setOnClickListener {
            processLogin()
        }

        regis.setOnClickListener {
            val intent = Intent(this, Registrasi::class.java)
            startActivity(intent)
            super.onBackPressed()
        }

    }

    override fun onBackPressed() {
        super.onResume()
        super.onBackPressed()
    }

    fun goToSignUpPage(view: View) {
        // Code untuk berpindah ke halaman sign in
        val intent = Intent(this, Registrasi::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    fun processLogin(){
        val username: EditText = findViewById(R.id.Email)
        val password: EditText = findViewById(R.id.Password)

        if (username.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Please fill all the credential", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(username.text.toString(), password.text.toString()).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this@Login, Inside::class.java)
                startActivity(intent)
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(baseContext, "Login failed.", Toast.LENGTH_SHORT,).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}