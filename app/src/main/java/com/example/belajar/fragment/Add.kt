package com.example.belajar.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.belajar.R
import com.example.belajar.data.Upload
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class Add : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    private lateinit var imageView: ImageView
    private lateinit var editTextCaption: EditText
    private lateinit var editTextDesc: EditText
    private lateinit var desc:EditText
    private lateinit var buttonUpload: Button

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        imageView = view.findViewById(R.id.imageView)
        editTextCaption = view.findViewById(R.id.editTextCaption)
        editTextDesc = view.findViewById(R.id.deskripsi)
        desc = view.findViewById(R.id.deskripsi)
        buttonUpload = view.findViewById(R.id.buttonUpload)

        imageView.setOnClickListener {
            openFileChooser()
        }

        buttonUpload.setOnClickListener {
            uploadFile()
        }

        return view
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }


    private fun uploadFile() {
        if (imageUri != null && editTextCaption.text != null) {
            val imageId = UUID.randomUUID().toString()
            val fileReference = FirebaseStorage.getInstance().getReference("uploadImg").child(imageId)

            fileReference.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val caption = editTextCaption.text.toString().trim()
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Anonymous"

                    val upload = hashMapOf(
                        "userId" to userId,
                        "username" to arguments?.getString("myname"),
//                        "mypprofile" to arguments?.getString("mypp"),
                        "imageUrl" to uri.toString(),
                        "caption" to caption,
                        "desc" to desc.text.toString().trim()
                    )

                    val db = FirebaseFirestore.getInstance()
                    db.collection("uploadImg").document(imageId).set(upload).addOnSuccessListener {
                        Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT).show()
                        imageView.setImageResource(R.drawable.designadd_img)
                        imageUri = null
                        editTextCaption.text.clear() // Menghapus teks setelah berhasil upload
                        editTextDesc.text.clear()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to upload: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(requireContext(), "Ada yang belom di isi tuh :)", Toast.LENGTH_SHORT).show()
        }
    }


//    private fun uploadFile() {
//        if (imageUri != null) {
//            val imageId = UUID.randomUUID().toString()
//            val fileReference = FirebaseStorage.getInstance().getReference("uploadImg").child(imageId)
//
//            fileReference.putFile(imageUri!!).addOnSuccessListener {
//                    fileReference.downloadUrl.addOnSuccessListener { uri ->
//
//                        val caption = editTextCaption.text.toString().trim()
//                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Anonymous"
//
//                        val upload = hashMapOf(
//                            "userId" to userId,
//                            "username" to arguments?.getString("myname"),
//                            "imageUrl" to uri.toString(),
//                            "caption" to caption
//                        )
//
//                        val db = FirebaseFirestore.getInstance()
//                        db.collection("uploadImg").document(imageId).set(upload).addOnSuccessListener {
//                                Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT).show()
//                            }.addOnFailureListener {
//                                Toast.makeText(requireContext(), "Failed to upload: ${it.message}", Toast.LENGTH_SHORT).show()
//                            }
//                    }
//
//                    imageView.setImageResource(R.drawable.designadd_img)
//                    imageUri = null
//                }.addOnFailureListener {
//                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
//                }
//
//        } else {
//            Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show()
//        }
//    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            Add().apply {
                arguments = Bundle().apply {

                }
            }
    }
}