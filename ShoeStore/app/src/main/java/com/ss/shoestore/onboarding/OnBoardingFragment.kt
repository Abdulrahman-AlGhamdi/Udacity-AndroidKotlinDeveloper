package com.ss.shoestore.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.ss.shoestore.databinding.FragmentOnBoardingBinding

class OnBoardingFragment : Fragment() {

    private lateinit var binding: FragmentOnBoardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingBinding.inflate(layoutInflater, container, false)

        init()

        return binding.root
    }

    private fun init() {
        val fragments = arrayListOf(WelcomeFragment(), InstructionFragment())
        val adapter = OnBoardingAdapter(fragments, requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.pagerIndicator, binding.viewPager) { _, _ ->

        }.attach()
    }
}