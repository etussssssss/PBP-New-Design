package com.example.belajar.adapter


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import com.example.belajar.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belajar.data.ImageModelHome
import com.example.belajar.fragment.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore




class ImageAdapterHome(private val images: List<ImageModelHome>) : RecyclerView.Adapter<ImageAdapterHome.ImageViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var image: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position].imageUrl
        val imageId = images[position].imageId
        val caption = images[position].caption
        val usernameImage = images[position].usernameImage
        val deskripsi = images[position].desc

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.color.black)
            .into(holder.imageView)

        loadLoveStatus(imageId, holder.loveButton, holder.likesCountTextView)

//        FirebaseFirestore.getInstance().collection("user").document(images[position].userId).get().addOnSuccessListener { doc ->
//            image = doc.getString("pprofile").toString()
//        }

        holder.loveButton.setOnClickListener {
            toggleLoveStatus(imageId, holder.loveButton, holder.likesCountTextView)
        }

        holder.commentButton.setOnClickListener {
            val context = holder.itemView.context
            Toast.makeText(context, "$imageUrl, clicked at position $position", Toast.LENGTH_SHORT).show()

            val fragment = Comment()
            val bundle = Bundle().apply {
                putString("USERID", images[position].userId)
                putString("IMAGE_URL", imageUrl)
                putString("IMAGE_ID", imageId)
                putString("CAPTION", caption)
                putString("USERNAMEIMAGE", usernameImage)
                putString("DESKRIPSI", deskripsi)
                putString("myname", images[position].myUserForCommentar)
                putString("mypp", images[position].myPp)
            }
            fragment.arguments = bundle

            val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fragment)
            transaction.addToBackStack("fragment")
            transaction.commit()
        }

        holder.captionTextView.text = "Caption: $caption"
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val loveButton: ImageButton = itemView.findViewById(R.id.imageButtonLike1)
        val likesCountTextView: TextView = itemView.findViewById(R.id.countLikes)
        val commentButton: ImageButton = itemView.findViewById(R.id.imageButtonCmnt3)
        val captionTextView: TextView = itemView.findViewById(R.id.caption)
    }

    private fun loadLoveStatus(imageId: String, loveButton: ImageButton, likesCountTextView: TextView) {
        val loveRef = db.collection("like").document(imageId).collection("loves").document(userId ?: return)
        loveRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val isLoved = document.getBoolean("isLoved") == true
                updateLoveButton(loveButton, isLoved)
            }
        }.addOnFailureListener { exception ->
            Log.e("LoveStatus", "Error loading love status", exception)
        }

        val postRef = db.collection("like").document(imageId)
        postRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val likesCount = document.getLong("likesCount")?.toInt() ?: 0
                likesCountTextView.text = "$likesCount Likes"
            } else {
                likesCountTextView.text = "0 Likes"
            }
        }.addOnFailureListener { exception ->
            Log.e("LikesCount", "Error loading likes count", exception)
        }
    }

    private fun toggleLoveStatus(imageId: String, loveButton: ImageButton, likesCountTextView: TextView) {
        val loveRef = db.collection("like").document(imageId).collection("loves").document(userId ?: return)
        val postRef = db.collection("like").document(imageId)

        loveRef.get().addOnSuccessListener { document ->
            var isLoved = document.getBoolean("isLoved") == true
            isLoved = !isLoved

            loveRef.set(mapOf("isLoved" to isLoved)).addOnSuccessListener {
                updateLoveButton(loveButton, isLoved)
            }.addOnFailureListener { exception ->
                Log.e("LoveStatus", "Error updating love status", exception)
            }

            postRef.get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    postRef.update("likesCount", if (isLoved) FieldValue.increment(1) else FieldValue.increment(-1)).addOnSuccessListener {
                        postRef.get().addOnSuccessListener { updatedDoc ->
                            val likesCount = updatedDoc.getLong("likesCount")?.toInt() ?: 0
                            likesCountTextView.text = "$likesCount Likes"
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("LikesCount", "Error updating likes count", exception)
                    }
                } else {
                    val initialLikesCount = if (isLoved) 1 else 0
                    postRef.set(mapOf("likesCount" to initialLikesCount)).addOnSuccessListener {
                        likesCountTextView.text = "$initialLikesCount Likes"
                    }.addOnFailureListener { exception ->
                        Log.e("LikesCount", "Error creating likes document", exception)
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("LikesCount", "Error getting likes document", exception)
            }
        }
    }

    private fun updateLoveButton(loveButton: ImageButton, isLoved: Boolean) {
        if (isLoved) {
            loveButton.setImageResource(R.drawable.lovered) // Change to filled icon
        } else {
            loveButton.setImageResource(R.drawable.love) // Change to outline icon
        }
    }
}



//class ImageAdapterHome(private val images: List<ImageModelHome>) : RecyclerView.Adapter<ImageAdapterHome.ImageViewHolder>() {
//
//    private var image:String = ""
//    private val db = FirebaseFirestore.getInstance()
//    private var isLoved = false
//    private val userId = FirebaseAuth.getInstance().currentUser?.uid
//    private var likesCount = 0
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
//        return ImageViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
//        val imageUrl = images[position].imageUrl
//        val imageId = images[position].imageId
//        val caption = images[position].caption
//        val usernameImage = images[position].usernameImage
//        val deskripsi = images[position].desc
//
//        Glide.with(holder.itemView.context)
//            .load(imageUrl)
//            .placeholder(R.color.black)
//            .into(holder.imageView)
//
//
//        loadLoveStatus(imageId, holder.itemView.findViewById<ImageButton>(R.id.imageButtonLike1))
////        val db = FirebaseFirestore.getInstance()
//        db.collection("user").document(images[position].userId).get().addOnSuccessListener { doc ->
//                image = doc.getString("pprofile").toString()
//        }
//
//
//        holder.itemView.findViewById<ImageButton>(R.id.imageButtonLike1).setOnClickListener {
//            isLoved = !isLoved
//            // Get document reference for the user's love status
//            val loveRef = db.collection("like").document(imageId).collection("loves").document(userId ?: "")
//
//            // Update Firestore
//            loveRef.set(mapOf("isLoved" to isLoved)).addOnSuccessListener {
//                if (isLoved) {
//                    holder.itemView.findViewById<ImageButton>(R.id.imageButtonLike1).setImageResource(R.drawable.lovered) // Change to filled icon
//                } else {
//                    holder.itemView.findViewById<ImageButton>(R.id.imageButtonLike1).setImageResource(R.drawable.love) // Change to outline icon
//                }
//            }.addOnFailureListener { exception ->
//                Log.e("LoveStatus", "Error updating love status", exception)
//            }
//
//
//        }
//
//
//        // Menambahkan listener klik untuk ImageButton di sini
//        holder.itemView.findViewById<ImageButton>(R.id.imageButtonCmnt3).setOnClickListener {
//            // Aksi yang akan dilakukan saat ImageButton diklik
//            val context = holder.itemView.context
//            Toast.makeText(context, "$imageUrl ,clicked at position $position", Toast.LENGTH_SHORT).show()
//
//            // Contoh: Pindah ke Fragment CommentFragment
//            val fragment = Comment()
//            val bundle = Bundle().apply {
//                putString("PPUSER", image)
//                putString("IMAGE_URL", imageUrl)
//                putString("IMAGE_ID", imageId)
//                putString("CAPTION", caption)
//                putString("USERNAMEIMAGE", usernameImage)
//                putString("DESKRIPSI", deskripsi)
//                //username anda
//                putString("myname", images[position].myUserForCommentar)
//                putString("mypp", images[position].myPp)
//            }
//            fragment.arguments = bundle
//
//            val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.nav_host_fragment, fragment)
//            transaction.addToBackStack("fragment")
//            transaction.commit()
//        }
//        holder.itemView.findViewById<TextView>(R.id.caption).text = "Caption: $caption"
//
//
//
//    }
//
//    override fun getItemCount(): Int {
//        return images.size
//    }
//
//    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val imageView: ImageView = itemView.findViewById(R.id.imageView)
//    }
//
//    private fun loadLoveStatus(imageId:String, loveButton:ImageButton) {
//        // Get document reference for the user's love status
//        val loveRef = db.collection("like").document(imageId).collection("loves").document(userId ?: return)
//
//        loveRef.get().addOnSuccessListener { document ->
//            if (document.exists()) {
//                isLoved = document.getBoolean("isLoved") == true
//                if (isLoved) {
//                    loveButton.setImageResource(R.drawable.lovered) // Change to filled icon
//                } else {
//                    loveButton.setImageResource(R.drawable.love) // Change to outline icon
//                }
//            }
//        }.addOnFailureListener { exception ->
//            Log.e("LoveStatus", "Error loading love status", exception)
//        }
//    }
//}


