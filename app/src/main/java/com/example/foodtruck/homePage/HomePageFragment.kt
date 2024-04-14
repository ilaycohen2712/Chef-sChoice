package com.example.foodtruck.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodtruck.R
import com.example.foodtruck.databinding.FragmentHomePageBinding
import com.example.foodtruck.places.PlaceAdapter
import kotlinx.coroutines.launch

class HomePageFragment : Fragment(), PlaceAdapter.PlaceClickListener {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomePageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
        //return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize your adapter
        val placeAdapter = PlaceAdapter(this)

        // Setup RecyclerView
        binding.recyclerViewPlaces.apply {
            adapter = placeAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Observe changes in the database and submit list to adapter
        viewModel.places.observe(viewLifecycleOwner) { places ->
            placeAdapter.submitList(places)
        }
    }

    override fun onAddCommentClicked(placeName: String) {
        val bundle = bundleOf("placeName" to placeName)
        findNavController().navigate(R.id.action_homePageFragment_to_postFragment, bundle)
    }

    override fun onViewCommentsClicked(placeName: String) {
        val bundle = bundleOf("placeName" to placeName)
        findNavController().navigate(R.id.action_homePageFragment_to_commentsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
