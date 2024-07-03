package com.example.belajar.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.belajar.R
import com.example.belajar.adapter.ImageAdapterHome
import com.example.belajar.data.ImageModelHome
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.storage
import org.w3c.dom.Comment


class Home : Fragment() {
    private lateinit var imageAdapter: ImageAdapterHome
    private lateinit var recyclerView: RecyclerView
    private lateinit var images: MutableList<ImageModelHome>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.rHome)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        images = mutableListOf()
        imageAdapter = ImageAdapterHome(images)
        recyclerView.adapter = imageAdapter
        fetchImagesFromFirebaseStorage()

        return view
    }

    private fun fetchImagesFromFirebaseStorage() {
        val db = FirebaseFirestore.getInstance()
        var id:String = ""
        db.collection("uploadImg").get().addOnSuccessListener { result ->
            for (document in result) {
                val userId = document.getString("userId")
                val image = document.getString("imageUrl")
                val caption = document.getString("caption")
                val usernameImage = document.getString("username")
                val desc =  document.getString("desc")

                Log.d(TAG, "Image URL: $image, $caption")
                images.add(ImageModelHome("$userId","$usernameImage" ,"$image", document.id, "$caption", "$desc" ,arguments?.getString("myname").toString(), arguments?.getString("mypp").toString()))
                imageAdapter.notifyDataSetChanged()
            }
        }


    }

    fun readComment(){
        val db = FirebaseFirestore.getInstance()
        val postId = "postId1"  // ID dari postingan yang dikomentari
        val commentsRef = db.collection("posts").document(postId).collection("comments")

        commentsRef.orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(context, "Failed to load comments: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val commentsList = mutableListOf<Comment>()
                for (doc in snapshots!!) {
                    val comment = doc.toObject(Comment::class.java)
                    commentsList.add(comment)
                }
                //commentsAdapter.submitList(commentsList)  // commentsAdapter adalah adapter RecyclerView
            }

    }

    fun addComment(){
        val db = FirebaseFirestore.getInstance()
        val postId = "postId1"  // ID dari postingan yang dikomentari
        val commentsRef = db.collection("posts").document(postId).collection("comments")

        val commentData = hashMapOf(
            "userId" to FirebaseAuth.getInstance().currentUser?.uid,
            "username" to "User Name",  // Dapatkan nama pengguna dari profil pengguna
            "comment" to "This is a comment",
            "timestamp" to System.currentTimeMillis()
        )

        commentsRef.add(commentData)
            .addOnSuccessListener {
                Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add comment: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


}