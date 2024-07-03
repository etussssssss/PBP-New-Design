package com.example.belajar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belajar.R
import com.example.belajar.data.DataComment

class CommentAdapter(private val comments: List<DataComment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        val timestamp:TextView = itemView.findViewById(R.id.timestamp)
        val imageUser:ImageView = itemView.findViewById(R.id.imageUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)

        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.usernameTextView.text = comment.username
        holder.commentTextView.text = comment.commentText
        holder.timestamp.text = comment.timestamp

        Glide.with(holder.itemView.context)
            .load(comment.imageUser)
            .placeholder(R.color.black)
            .into(holder.imageUser)
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}

