package com.example.foodtruck.allPosts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodtruck.database.entities.Post
import com.example.foodtruck.databinding.FragmentCommentsBinding
import com.example.foodtruck.posts.FirebasePost
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentsFragment : Fragment() {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var commentAdapter: CommentAdapter
    private lateinit var placeName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("placeName")?.let { placeNameArg ->
            placeName = placeNameArg
        }

        setupRecyclerView()
        fetchComments()

        // Handle clicks on the go back icon
        binding.goBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter()
        binding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }
    }

    private fun fetchComments() {
        Log.d("CommentsFragment", "Fetching comments for place: $placeName")
        val databaseReference = FirebaseDatabase.getInstance().reference.child("posts")
        databaseReference.orderByChild("placeName").equalTo(placeName)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comments = dataSnapshot.children.mapNotNull {
                        val key = it.key
                        val post = it.getValue(FirebasePost::class.java)
                        if (key != null && post != null) {
                            Pair(key, post)
                        } else {
                            null
                        }
                    }
                    commentAdapter.submitList(comments)
                    Log.d("CommentsFragment", "Found ${comments.size} comments")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}