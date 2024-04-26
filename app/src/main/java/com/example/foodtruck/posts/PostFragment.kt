package com.example.foodtruck.post


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.foodtruck.databinding.FragmentPostBinding
import com.example.foodtruck.posts.PostViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by viewModels()

    // Variable to hold the selected image URI
    private var selectedImageUri: Uri? = null

    // ActivityResultLauncher for the image picker
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imageViewUploadedPhoto.setImageURI(uri)
            selectedImageUri = uri
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve dishName from the arguments passed to this fragment
        val dishName = arguments?.getString("dishName") ?: "Unknown"
        Log.d("dishName", "$dishName ! ")

        binding.buttonChoosePhoto.setOnClickListener {
            // Launch the image picker
            imagePickerLauncher.launch("image/*")
        }

        binding.buttonSubmitComment.setOnClickListener {
            val commentText = binding.inputComment.text.toString()
            val userId = getCurrentUserId()
            Log.d("CheckComment",commentText)
            Log.d("CheckComment",userId.toString())

            selectedImageUri?.let { uri ->
                if (userId != null && commentText.isNotEmpty()) {
                    uploadImageToFirebaseStorage(uri) { photoUrl ->
                        viewModel.createPost(userId, commentText, photoUrl, dishName,
                            onSuccess = {
                                // Navigate back only on success
                                findNavController().navigateUp()
                            },
                            onFailure = { e ->
                                // Log the error or show an error message
                                Log.e("PostFragment", "Failed to create post", e)
                                Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "Please write a comment", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "Please share your dish picture", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle clicks on the go back icon
        binding.goBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCurrentUserId(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, callback: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images/${System.currentTimeMillis()}_${imageUri.lastPathSegment}")
        val uploadTask = imagesRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                callback(downloadUri.toString())
            } else {
                // Handle the error
                task.exception?.message?.let {
                    // Show error message to the user
                }
            }
        }
    }
}
