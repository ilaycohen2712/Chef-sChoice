package com.example.foodtruck.places


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.foodtruck.databinding.FragmentPlaceBinding


class PlaceFragment : Fragment() {

    private var _binding: FragmentPlaceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaceViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt("placeId")?.let { placeId ->
            viewModel.getPlaceById(placeId).observe(viewLifecycleOwner) { place ->
                place?.let {
                    binding.textViewPlaceName.text = it.name
                    binding.textViewPlaceAddress.text = it.address
                    binding.textViewPlaceDescription.text = it.description
//                    viewModel.fetchPlaceRating(it) // Fetch rating for the place
                    /*
                    Picasso.get().invalidate(it.placePhoto)
                    Picasso.get()
                        .load(it.placePhoto)
                        .into(binding.imageViewPhoto as ImageView)*/
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
