package com.example.belajar.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.belajar.Inside
import com.example.belajar.Login
import com.example.belajar.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID



class Profile : Fragment() {

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var profileImageView: ImageView
    private lateinit var logout:ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val text = view.findViewById<TextView>(R.id.myusername)
        text.text = arguments?.getString("myname")

        profileImageView = view.findViewById(R.id.mypp)
        logout = view.findViewById(R.id.btnLogout)

        // Load initial profile picture
//        arguments?.getString("mypp")?.let { profilePicUrl ->
//            Glide.with(view.context)
//                .load(profilePicUrl)
//                .placeholder(R.color.black)
//                .into(profileImageView)
//        }

        logout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        profileImageView.setOnClickListener {
            openFileChooser()
        }

        getUserName()
        return view
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Iya") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        activity?.finish()
    }

    private fun getUserName() {
        val auth = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        if (auth != null) {
            val uid = auth.uid
            db.collection("user").document(uid).get().addOnSuccessListener { document ->
                    if (document != null) {
                        val displayName = document.getString("nama").toString()
                        val displaypp = document.getString("pprofile").toString()
                        if (displayName != null) {
                            Glide.with(requireContext())
                                .load(displaypp)
                                .placeholder(R.color.black)
                                .into(profileImageView)
                        }
                    }

                }
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            profileImageView.setImageURI(imageUri)
            uploadFile()
        }
    }

    private fun uploadFile() {
        if (imageUri != null) {
            val fileReference = FirebaseStorage.getInstance().getReference("pprofile").child(UUID.randomUUID().toString())

            fileReference.putFile(imageUri!!).addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnSuccessListener

                    val db = FirebaseFirestore.getInstance()
                    db.collection("user").document(userId).update("pprofile", uri.toString()).addOnSuccessListener {

                        val intent = Intent(requireContext(), Inside::class.java)
                        startActivity(intent)

                        Toast.makeText(requireContext(), "Profile photo updated", Toast.LENGTH_SHORT).show()
                        super.onStart()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update profile photo: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show()
        }
    }
}


