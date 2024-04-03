package com.example.foodtruck.places

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodtruck.database.entities.Place
import com.example.foodtruck.databinding.FragmentPlaceBinding
import com.example.foodtruck.placesApiService.PlaceResponse
import com.example.foodtruck.placesApiService.PlacesApiService
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory

//import com.bumptech.glide.Glide

class PlaceAdapter(private val clickListener: PlaceClickListener) : ListAdapter<Place, PlaceAdapter.PlaceViewHolder>(PlaceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = FragmentPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
    }


    class PlaceViewHolder(private val binding: FragmentPlaceBinding, private val clickListener: PlaceClickListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: Place) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(PlacesApiService::class.java)
            service.findPlaceFromText(place.name,fields = "name,rating",apiKey="AIzaSyBsV4dpcOTGvGNpk3C8Zdm_viZAGui4C1k").enqueue(object : Callback<PlaceResponse> {
                override fun onResponse(call: Call<PlaceResponse>, response: Response<PlaceResponse>) {
                    if (response.isSuccessful) {
                        val rating = response.body()?.candidates?.firstOrNull()?.rating
                        // Update your UI with the rating
                        binding.textViewPlaceRating.text = "Rating: ${rating}"
                    }
                }

                override fun onFailure(call: Call<PlaceResponse>, t: Throwable) {
                    // Handle failure
                    }
            })

            binding.textViewPlaceName.text = place.name
            binding.textViewPlaceAddress.text = place.address
            binding.textViewPlaceDescription.text = place.description

            Picasso.get().invalidate(place.placePhoto)
            Picasso.get()
                .load(place.placePhoto)
                .into(binding.imageViewPhoto as ImageView)

            // Set up click listener for the "Add Comment" button
            binding.buttonAddComment.setOnClickListener {
                clickListener.onAddCommentClicked(place.name)
            }

            binding.buttonViewComments.setOnClickListener {
                clickListener.onViewCommentsClicked(place.name)
            }
        }
    }

    interface PlaceClickListener {
        fun onAddCommentClicked(placeName: String)
        fun onViewCommentsClicked(placeName: String)
    }
}

class PlaceDiffCallback : DiffUtil.ItemCallback<Place>() {
    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem == newItem
    }
}