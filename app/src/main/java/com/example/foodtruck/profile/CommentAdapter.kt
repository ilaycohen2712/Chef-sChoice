package com.example.foodtruck.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodtruck.R
import com.squareup.picasso.Picasso


// RecyclerView adapter
//responsible for managing the items in the RecyclerView
class CommentAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    // ViewHolder class
    // Define interfaces for click listeners
    interface EditClickListener {
        fun onEditClick(position: Int)
    }

    interface DeleteClickListener {
        fun onDeleteClick(position: Int)
    }
    // Define editClickListener and deleteClickListener variables
    var editClickListener: EditClickListener? = null
    var deleteClickListener: DeleteClickListener? = null

    //nested class that represents the view for each item in the RecyclerView
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Bind data to views
        fun bind(comment: Comment) {
            // Bind comment data to views in the layout
            itemView.findViewById<TextView>(R.id.placeNameTextView).text = comment.placeName
            itemView.findViewById<TextView>(R.id.commentTextView).text = comment.comment
            // Load image using Picasso or Glide
            Picasso.get().load(comment.photo).into(itemView.findViewById<ImageView>(R.id.photoImageView))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
        // Set click listeners for edit and delete buttons
        holder.itemView.findViewById<ImageButton>(R.id.editButton).setOnClickListener {
            editClickListener?.onEditClick(position)
        }

        holder.itemView.findViewById<ImageButton>(R.id.deleteButton).setOnClickListener {
            deleteClickListener?.onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}

