package com.ss.shoestore.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ss.shoestore.common.Constants.IS_FINISHED_KEY
import com.ss.shoestore.common.Constants.SHARED_PREFERENCES_NAME
import com.ss.shoestore.databinding.FragmentInstructionBinding

class InstructionFragment : Fragment() {

    private lateinit var binding: FragmentInstructionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstructionBinding.inflate(inflater, container, false)

        onClick()

        return binding.root
    }

    private fun onClick() {
        binding.finish.setOnClickListener {
            disableViewPager()

            val direction = OnBoardingFragmentDirections
            val action = direction.actionOnBoardingFragmentToShoesListFragment()
            findNavController().navigate(action)
        }
    }

    private fun disableViewPager() {
        val sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_FINISHED_KEY, true)
        editor.apply()
    }
}