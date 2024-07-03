package com.example.belajar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class Registrasi : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)

        auth = Firebase.auth

        val username:EditText = findViewById(R.id.Email)
        val password:EditText = findViewById(R.id.Password)
        val nama:EditText = findViewById(R.id.Nama)

        val enter:Button = findViewById(R.id.Enter)
        val login:TextView = findViewById(R.id.login)

        enter.setOnClickListener {
            val nama = nama.text.toString()
            val email = username.text.toString()
            val password = password.text.toString()

            if (nama.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(nama,email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        login.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            super.onBackPressed()
        }

    }

    override fun onBackPressed() {
        super.onResume()
        super.onBackPressed()
    }

    fun goToSignInPage(view: View) {
        // Code untuk berpindah ke halaman sign in
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    fun registerUser(nama: String,email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            val user = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()
            if (task.isSuccessful) {
                if (user != null) {
                    val uid = user.uid

                    val user = hashMapOf(
                        "email" to email,
                        "nama" to nama,
                        "password" to password,
                        "pprofile" to "https://firebasestorage.googleapis.com/v0/b/belajar-48739.appspot.com/o/defaultImage%2Fdefaultpp.jpg?alt=media&token=81e1765c-29aa-4fd6-8697-11a63ab5909b"
                    )

                    db.collection("user").document(uid).set(user).addOnSuccessListener {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT)
                            .show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to save user data: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}