package com.example.asteroidradar.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.asteroidradar.databinding.FragmentAsteroidListBinding
import kotlinx.coroutines.flow.collect

class AsteroidListFragment : Fragment() {

    private var _binding: FragmentAsteroidListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AsteroidListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAsteroidListBinding.inflate(inflater, container, false)

        showImageOfTheDay()
        bindAsteroidData()

        return binding.root
    }

    private fun bindAsteroidData() {
        lifecycleScope.launchWhenStarted {
            viewModel.getAsteroidList()?.collect {
                binding.asteroidRecycler.apply {
                    this.layoutManager = LinearLayoutManager(requireContext())
                    this.adapter = AsteroidListAdapter(it)
                }
            }
        }
    }

    private fun showImageOfTheDay() {
        lifecycleScope.launchWhenStarted {
            viewModel.getImageOfTheData()?.collect {
                binding.imageOfTheDay.load(it.url)
                binding.imageText.text = it.title
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}