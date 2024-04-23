package com.example.foodtruck.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodtruck.R
import com.example.foodtruck.Welcome
import com.example.foodtruck.shared.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment(), OnMapReadyCallback,
    CommentAdapter.EditClickListener, CommentAdapter.DeleteClickListener {

    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var profileImageView: CircleImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentName: String? = null
    private var currentProfilePicUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_profile, container, false
        )
        profileImageView = view.findViewById(R.id.profileImageView)
        nameTextView = view.findViewById(R.id.nameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        logoutButton = view.findViewById(R.id.logoutButton)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        // Initialize RecyclerView
        commentRecyclerView = view.findViewById(R.id.userCommentRecyclerView)
        commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize the Google Map fragment
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfilePhoto()
        observeUserData()
        observeUserComments()
        auth = FirebaseAuth.getInstance()
        logoutButton.setOnClickListener {
            logoutUser()
        }

        editProfileButton.setOnClickListener {
            editProfile()
        }

        // Fetch and display profile photo
        val defaultPhotoResourceId = R.drawable.profile_photo_placeholder
        val defaultPhotoUri = Uri.parse("android.resource://${requireContext().packageName}/$defaultPhotoResourceId")

        viewModel.fetchProfilePhoto(defaultPhotoUri)
        viewModel.fetchUserName()
        viewModel.fetchUserEmail()
        viewModel.fetchUserComments()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Check if the app has permission to access the device's location
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Enable My Location button and set its listener
        googleMap.isMyLocationEnabled = true

        // Get the last known location of the device
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // Got last known location. In some rare situations this can be null.
                if (location !== null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.addMarker(
                        MarkerOptions().position(currentLatLng).title("Your Location")
                    )
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM)
                    )
                } else {
                    // Handle null location
                    Toast.makeText(
                        requireContext(),
                        "Could not get current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun observeUserComments() {
        viewModel.userComments.observe(viewLifecycleOwner) { comments ->
            // Update RecyclerView with the list of comments
            commentRecyclerView.adapter = CommentAdapter(comments).apply {
                editClickListener = this@ProfileFragment
                deleteClickListener = this@ProfileFragment
            }
        }
    }

    private fun observeProfilePhoto() {
        viewModel.profilePhotoUrl.observe(viewLifecycleOwner) { photoUrl ->
            Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.profile_photo_placeholder) // Placeholder image while loading
                .error(R.drawable.profile_photo_placeholder) // Image to show in case of error
                .into(profileImageView as ImageView)
        }
    }

    private fun observeUserData() {
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            Log.d("TAG", "observeUserData: $name")
            // Update UI with user's name
            nameTextView.text = name
            currentName = name
        }

        val userEmail = viewModel.fetchUserEmail()
        // Update UI with user's email
        emailTextView.text = userEmail
    }

    private fun logoutUser() {
        auth.signOut()
        // navigate the user to welcome activity
        val intent = Intent(requireContext(), Welcome::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun editProfile() {
        val dialogFragment = EditProfileDialogFragment().apply {
            arguments = Bundle().apply {
                putString("currentName", currentName)
                putString("currentProfilePicUrl", currentProfilePicUrl)
            }
        }
        dialogFragment.show(parentFragmentManager, "EditProfileDialogFragment")
    }

    override fun onEditClick(position: Int) {
        // Retrieve the comment at the given position
        val comment = viewModel.userComments.value?.get(position)
        // Open a dialog fragment to edit the comment
        comment?.let {
            val args = Bundle().apply {
                putString("commentId", it.commentId)
            }

            val editCommentDialogFragment = EditCommentDialogFragment().apply {
                arguments = args
            }

            editCommentDialogFragment.show(parentFragmentManager, "EditCommentDialogFragment")
        }
    }

    override fun onDeleteClick(position: Int) {
        // Retrieve the comment at the given position
        val comment = viewModel.userComments.value?.get(position)

        //delete the comment from the database
        comment?.let {
            viewModel.deleteComment(comment.commentId)
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 15f
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
