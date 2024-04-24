package com.example.foodtruck.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView



class ProfileFragment : Fragment(),
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
    private lateinit var lastLocation : Location
    private var currentName: String? = null
    private var currentProfilePicUrl: String? = null

    companion object {
        private const val DEFAULT_ZOOM = 15f
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
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
        commentRecyclerView = view.findViewById(R.id.userCommentRecyclerView)
        commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())


//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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

//    override fun onMapReady(map: GoogleMap) {
//        googleMap = map
//        googleMap.uiSettings.isZoomControlsEnabled = true
//        googleMap.setOnMarkerClickListener(this@ProfileFragment)
//        setupMap()
//    }
//    private fun setupMap() {
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
//            return
//        }
//
//        googleMap.isMyLocationEnabled = true
//        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
//            if (location != null) {
//                lastLocation = location
//                val currentLatLng = LatLng(location.latitude, location.longitude)
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
//                addMarkers()
//            } else {
//                Toast.makeText(requireContext(), "Could not get current location", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun addMarkers() {
//        // Add a marker for Algorithmim
//        val location1 = LatLng(32.109333, 34.855499)
//        googleMap.addMarker(MarkerOptions().position(location1).title("Israel Farkash teacher of algo, Tel Aviv"))
//
//        // Add a marker for Rishon LeZion
//        val location2 = LatLng(31.9585, 34.8101)
//        googleMap.addMarker(MarkerOptions().position(location2).title("Moshe Cohen teacher of C/C++,jAVA,Rishon LeZion"))
//
//        // Add a marker for Jerusalem
//        val location3 = LatLng(31.7683, 35.2137)
//        googleMap.addMarker(MarkerOptions().position(location3).title("Yuval Cohen teacher of data structure, Jerusalem"))
//
//        // Add a marker for Modiin
//        val location4 = LatLng(31.8904, 35.0057)
//        googleMap.addMarker(MarkerOptions().position(location4).title("Chen Amrani teacher of Java,Modiin"))
//
//        // Add a marker for Ramat Gan
//        val location5 = LatLng(32.0853, 34.8119)
//        googleMap.addMarker(MarkerOptions().position(location5).title("Ori Farkash teacher of C++,Ramat Gan"))
//
//        // Add a marker for Petach Tikva
//        val location6 = LatLng(32.0869, 34.8878)
//        googleMap.addMarker(MarkerOptions().position(location6).title("Oren ShemTov teacher of Python,Petach Tikva"))
//
//        // Add a marker for Haifa
//        val location7 = LatLng(32.8054, 34.9721)
//        googleMap.addMarker(MarkerOptions().position(location7).title("Roy Ben Moshe teacher of Analiza, Haifa"))
//
//        // Add a marker for Ness Ziona
//        val location8 = LatLng(31.9503, 34.7881)
//        googleMap.addMarker(MarkerOptions().position(location8).title("Israel Israeli teacher of algo,Ness Ziona"))
//
//        // Add a marker for Holon
//        val location9 = LatLng(32.0097, 34.7746)
//        googleMap.addMarker(MarkerOptions().position(location9).title("Israel Farkash teacher of Computational models ,Holon"))
//
//        // Add a marker for Rosh HaAyin
//        val location10 = LatLng(32.0649, 34.9395)
//        googleMap.addMarker(MarkerOptions().position(location10).title("Israel Farkash teacher of Kotlin , Rosh HaAyin"))
//
//        // Add a marker for Beer Yaakov
//        val location11 = LatLng(31.9167, 34.7833)
//        googleMap.addMarker(MarkerOptions().position(location11).title("Israel Farkash teacher of C# ,Beer Yaakov"))
//
//        // Move the camera to the Rishon LeZion marker
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location2, 12f))
//    }

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
                .into(profileImageView)
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


//    override fun onMarkerClick(marker: Marker): Boolean {
//        val countryName = marker.title
//        val bundle = Bundle().apply {
//            putString("countryName", countryName)
//        }
//        val profileFragment = ProfileFragment().apply {
//            arguments = bundle
//        }
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, profileFragment)
//            .addToBackStack(null)
//            .commit()
//        return true
//    }
}


