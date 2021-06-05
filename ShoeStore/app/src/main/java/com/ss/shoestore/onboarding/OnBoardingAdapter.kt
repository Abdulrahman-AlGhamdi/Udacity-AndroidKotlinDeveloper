package com.ss.shoestore.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnBoardingAdapter(
    private val fragments: ArrayList<Fragment>,
    manager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(manager, lifecycle) {

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}