package com.example.belajar

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.belajar.databinding.ActivityInsideBinding
import com.example.belajar.fragment.Add
import com.example.belajar.fragment.Home
import com.example.belajar.fragment.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Inside : AppCompatActivity() {
    private lateinit var binding: ActivityInsideBinding
    private var activeFragmentId: Int = R.id.home // Default active fragment
    private var myusername: String = "Anonymous"
    private var mypprofile: String = "https://firebasestorage.googleapis.com/v0/b/belajar-48739.appspot.com/o/defaultImage%2Fdefaultpp.jpg?alt=media&token=81e1765c-29aa-4fd6-8697-11a63ab5909b"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dapatkan username terlebih dahulu, kemudian lanjutkan dengan pengaturan fragment
        getUserName {
            binding.bottomnav.setOnItemSelectedListener { menuItem ->
                if (menuItem.itemId != activeFragmentId) {
                    when (menuItem.itemId) {
                        R.id.home -> {
                            val fragment = Home()
                            val bundle = Bundle().apply {
                                putString("myname", myusername)
                                putString("mypp", mypprofile)
                            }
                            fragment.arguments = bundle
                            replaceFragment(fragment)
                            true
                        }
                        R.id.add -> {
                            val fragment = Add()
                            val bundle = Bundle().apply {
                                putString("myname", myusername)
                                putString("mypp", mypprofile)
                            }
                            fragment.arguments = bundle
                            replaceFragment(fragment)
                            true
                        }
                        R.id.profil -> {
                            val fragment = Profile()
                            val bundle = Bundle().apply {
                                putString("myname", myusername)
                                putString("mypp", mypprofile)
                            }
                            fragment.arguments = bundle
                            replaceFragment(fragment)
                            true
                        }
                        else -> false
                    }.also {
                        activeFragmentId = menuItem.itemId
                    }
                } else {
                    Toast.makeText(this, "Menu ini sudah dipilih", Toast.LENGTH_SHORT).show()
                    true // Return true to indicate event has been handled
                }
            }

            // Set the initial selected fragment
            val fragment = Home()
            val bundle = Bundle().apply {
                putString("myname", myusername)
                putString("mypp", mypprofile)
            }
            fragment.arguments = bundle
            replaceFragment(fragment) // Example: Start with Home fragment
        }
    }

    // Mengambil username dengan callback untuk memastikan asinkron selesai sebelum melanjutkan
    private fun getUserName(callback: () -> Unit) {
        val auth = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        if (auth != null) {
            val uid = auth.uid
            db.collection("user")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val displayName = document.getString("nama").toString()
                        val displaypp = document.getString("pprofile").toString()
                        if (displayName != null) {
                            myusername = displayName
                            mypprofile = displaypp
                        }
                    }
                    callback()
                }.addOnFailureListener { e ->
                    callback() // Tetap panggil callback meski terjadi kegagalan
                }
        } else {
            callback() // Jika auth null, tetap panggil callback
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
