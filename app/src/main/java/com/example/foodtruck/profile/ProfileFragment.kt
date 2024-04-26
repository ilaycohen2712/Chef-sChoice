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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.Places.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView



class ProfileFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
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
    private lateinit var placesClient: PlacesClient

    companion object {
        private const val DEFAULT_ZOOM = 15f
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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
        placesClient = createClient(this.requireContext())
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnMarkerClickListener(this@ProfileFragment)
        setupMap()
    }
    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        googleMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                addMarkers()
            } else {
                Toast.makeText(requireContext(), "Could not get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarkers() {
        searchPlaceAndGetId("Hava's soup factory") { placeId1 ->
            val location1 = LatLng(32.109333, 34.855499)
            val marker1 = googleMap.addMarker(MarkerOptions().position(location1).title("Hava's soup factory, Tel Aviv"))
            if (marker1 != null) {
                marker1.tag = "place1"
            }

            searchPlaceAndGetId("Eat with Hava") { placeId2 ->
                val location2 = LatLng(31.9585, 34.8101)
                val marker2 = googleMap.addMarker(MarkerOptions().position(location2).title("Eat with Hava, Rishon LeZion"))
                if (marker2 != null) {
                    marker2.tag = "place2"
                }

                searchPlaceAndGetId("Hava's meat paradise") { placeId3 ->
                    val location3 = LatLng(31.7683, 35.2137)
                    val marker3 = googleMap.addMarker(MarkerOptions().position(location3).title("Hava's meat paradise, Jerusalem"))
                    if (marker3 != null) {
                        marker3.tag = "place3"
                    }

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, 12f))
                }
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


    override fun onMarkerClick(marker: Marker): Boolean {
        val placeId = marker.tag as String // assuming you set the place ID as the marker's tag
        val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.OPENING_HOURS)

        placesClient.fetchPlace(FetchPlaceRequest.newInstance(placeId, placeFields))
            .addOnSuccessListener { response ->
                val place = response.place
                val name = place.name
                val address = place.address
                val openingHours = place.openingHours?.weekdayText?.joinToString("\n") ?: "Opening hours not available"

                val placeDetailsFragment = PlaceDetailsFragment.newInstance(name, address, openingHours)
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, placeDetailsFragment)
                    .addToBackStack(null)
                    .commit()
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileFragment", "Place details fetch failed: $exception")
            }

        return true
    }
    private fun searchPlaceAndGetId(query: String, callback: (String) -> Unit) {
        val request = FindCurrentPlaceRequest.newInstance(listOf(Place.Field.ID, Place.Field.NAME))

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response ->
                for (placeLikelihood in response.placeLikelihoods) {
                    val place = placeLikelihood.place
                    if (place.name?.contains(query, ignoreCase = true) == true) {
                        place.id?.let { callback(it) }
                        return@addOnSuccessListener
                    }
                }
                callback("")
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileFragment", "Place search failed: $exception")
                callback("") // Error occurred
            }
    }
}


