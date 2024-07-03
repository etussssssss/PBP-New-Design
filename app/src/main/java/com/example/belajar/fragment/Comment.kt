package com.example.belajar.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belajar.CallBack.FirestoreCallback
import com.example.belajar.R
import com.example.belajar.adapter.CommentAdapter
import com.example.belajar.data.DataComment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class Comment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var commentText: EditText
    private lateinit var sendButton: Button
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var username:TextView
    private lateinit var judul: TextView
    private lateinit var deskripsi: TextView
    private lateinit var commentsList: MutableList<DataComment>
    private lateinit var commentsAdapter: CommentAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comment, container, false)

        val userIDD = arguments?.getString("USERID")
        val usernameImage = arguments?.getString("USERNAMEIMAGE")
        val imageUrl = arguments?.getString("IMAGE_URL")
        val imageName = arguments?.getString("IMAGE_ID")
        val imageCaption = arguments?.getString("CAPTION")
        val desc = arguments?.getString("DESKRIPSI")


        //USER NAME ANDA KETIKA KOMENTAR
        val myUserForCommentar = arguments?.getString("myname")
        val mypprofile = arguments?.getString("mypp") //Blom di pakai

        imageView = view.findViewById(R.id.imageView4)
        commentText = view.findViewById(R.id.commentEditText)
        sendButton = view.findViewById(R.id.sendButton)
        username = view.findViewById(R.id.usernameTextView)
        judul = view.findViewById(R.id.judul)
        deskripsi = view.findViewById(R.id.deskripsi)
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView)
//        val imageUser:ImageView = view.findViewById(R.id.imageUser)

        commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentsList = mutableListOf()
        commentsAdapter = CommentAdapter(commentsList)
        commentsRecyclerView.adapter = commentsAdapter



        Glide.with(requireContext()).load(imageUrl).into(imageView)

        sendButton.setOnClickListener {
            if (!commentText.text.isNullOrEmpty() && imageName != null && myUserForCommentar != null && mypprofile != null) {
                addComment(imageName, commentText.text.toString(), myUserForCommentar, mypprofile)
            } else {
                Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        username.text = usernameImage
        judul.text = imageCaption
        deskripsi.text = desc


        if (userIDD != null) {
            FirebaseFirestore.getInstance().collection("user").document(userIDD).get().addOnSuccessListener { doc ->
                Glide.with(requireContext()).load(doc.getString("pprofile")).into(view.findViewById(R.id.imageUser))

            }
        }
//        Glide.with(requireContext()).load(ppuser).into(view.findViewById(R.id.imageUser))

        imageName?.let {
            loadComments(it)
        }

        return view
    }

    private fun addComment(imageId: String, commentar: String, username: String, mypprofile: String) {
        val db = FirebaseFirestore.getInstance()
        val commentId = "$username-${UUID.randomUUID().toString().substring(0, 6)}"
        val commentsRef = db.collection("posts").document(imageId).collection("comments").document(commentId)

        val commentData = hashMapOf(
            "userId" to FirebaseAuth.getInstance().currentUser?.uid,
            "username" to username,
            "comment" to commentar,
            "timestamp" to Date(System.currentTimeMillis())
        )

        commentsRef.set(commentData).addOnSuccessListener {
            Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show()
            commentText.text.clear()

            // Fetch the profile image and add the comment to the list
            getImage(FirebaseAuth.getInstance().currentUser?.uid!!, object : FirestoreCallback {
                override fun onCallback(imageUrl: String) {
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                    val timestamp = Date(System.currentTimeMillis())
                    val date = dateFormat.format(timestamp)

                    commentsList.add(DataComment(username, commentar, date, imageUrl))
                    commentsAdapter.notifyItemInserted(commentsList.size - 1)
                    commentsRecyclerView.scrollToPosition(commentsList.size - 1) // Scroll to the latest comment
                }
            })
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to add comment: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadComments(imageId: String) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val db = FirebaseFirestore.getInstance()
        val commentsRef = db.collection("posts").document(imageId).collection("comments")

        commentsRef.orderBy("timestamp", Query.Direction.ASCENDING).get().addOnSuccessListener { documents ->
            commentsList.clear()
            for (document in documents) {
                val username = document.getString("username") ?: "Anonymous"
                val comment = document.getString("comment") ?: ""
                val timestamp = document.getTimestamp("timestamp")
                val date = timestamp?.toDate()?.let { dateFormat.format(it) } ?: "Unknown date"
                val userId = document.getString("userId").toString()

                getImage(userId, object : FirestoreCallback {
                    override fun onCallback(imageUrl: String) {
                        commentsList.add(DataComment(username, comment, date, imageUrl))
//                        Log.d("Comment", "Username: $username, Comment: $comment, Date: $date")
                        commentsAdapter.notifyDataSetChanged()
                    }
                })
            }
        }.addOnFailureListener { exception ->
            Log.w("Comment", "Error getting documents: ", exception)
        }
    }

    private fun getImage(userId: String, callback: FirestoreCallback) {
        val db = FirebaseFirestore.getInstance()
        db.collection("user").document(userId).get().addOnSuccessListener { doc ->
            val imgUserCmnt = doc.getString("pprofile").toString()
            callback.onCallback(imgUserCmnt)
        }
    }
}



