package com.ss.shoestore.shoedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ss.shoestore.databinding.FragmentShoeDetailsBinding
import com.ss.shoestore.models.ShoeModel
import com.ss.shoestore.viewmodel.ShoesViewModel

class ShoeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentShoeDetailsBinding
    private val viewModel: ShoesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShoeDetailsBinding.inflate(inflater, container, false)

        init()
        onItemAdded()
        dismissDialog()

        return binding.root
    }

    private fun init() {
        binding.toolbar.setupWithNavController(findNavController())
        val shoe = ShoeModel("", "", "", "", null)
        binding.shoeDetailsViewModel = viewModel
        binding.shoeItemData = shoe
    }

    private fun onItemAdded() {
        viewModel.shoesState.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().popBackStack()
                viewModel.onEventComplete()
            }
        })
    }

    private fun dismissDialog() {
        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}