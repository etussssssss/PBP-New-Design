package com.example.belajar

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_progress)

        var progbar = findViewById<ProgressBar>(R.id.loading_progbar)
        progbar.visibility = ProgressBar.VISIBLE
        Handler().postDelayed({
            var intent : Intent
            if (FirebaseAuth.getInstance().currentUser != null) {
                intent = Intent(this, Inside::class.java)
            } else{
                intent = Intent(this, FirstProgress::class.java)
            }
            progbar.visibility = ProgressBar.GONE
            startActivity(intent)
        }, 5000)

    }
}