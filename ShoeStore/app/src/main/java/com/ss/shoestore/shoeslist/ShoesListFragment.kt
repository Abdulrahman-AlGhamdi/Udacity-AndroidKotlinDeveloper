package com.ss.shoestore.shoeslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ss.shoestore.R
import com.ss.shoestore.databinding.FragmentShoesListBinding
import com.ss.shoestore.databinding.RowItemLayoutBinding
import com.ss.shoestore.login.LoginFragmentDirections
import com.ss.shoestore.viewmodel.ShoesViewModel

class ShoesListFragment : Fragment() {

    private lateinit var binding: FragmentShoesListBinding
    private val viewModel: ShoesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShoesListBinding.inflate(inflater, container, false)

        init()
        showShoesList()
        navigateToShoeDetails()

        return binding.root
    }

    private fun init() {
        val configuration = AppBarConfiguration.Builder(R.id.shoesListFragment).build()
        binding.toolbar.setupWithNavController(findNavController(), configuration)

        binding.toolbar.menu.findItem(R.id.sign_out).setOnMenuItemClickListener {
            val action = ShoesListFragmentDirections.actionShoesListFragmentToLoginFragment()
            findNavController().navigate(action)
            true
        }
    }

    private fun showShoesList() {
        viewModel.shoesList.observe(viewLifecycleOwner, {
            for (shoe in it) {
                val listLayout = RowItemLayoutBinding.inflate(layoutInflater)
                listLayout.shoeItemData = shoe
                binding.shoeList.addView(listLayout.root)
            }
        })
    }

    private fun navigateToShoeDetails() {
        binding.add.setOnClickListener {
            val action = ShoesListFragmentDirections.actionShoesListFragmentToShoeDetailsFragment()
            findNavController().navigate(action)
        }
    }
}