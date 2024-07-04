package com.example.belajar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FirstProgress : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val btn: Button = findViewById(R.id.button)

        btn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            super.onStart()
        }
    }
}