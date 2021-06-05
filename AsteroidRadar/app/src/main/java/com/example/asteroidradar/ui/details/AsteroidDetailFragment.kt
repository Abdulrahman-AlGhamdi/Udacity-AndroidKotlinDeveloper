package com.example.asteroidradar.ui.details

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.asteroidradar.R
import com.example.asteroidradar.databinding.FragmentAsteroidDetailBinding

class AsteroidDetailFragment : Fragment() {

    private var _binding: FragmentAsteroidDetailBinding? = null
    private val binding get() = _binding!!
    private val argument: AsteroidDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAsteroidDetailBinding.inflate(inflater, container, false)

        showDetails()

        return binding.root
    }

    private fun showDetails() {
        val asteroid = argument.asteroid
        val asteroidData = asteroid.closeApproachData.first()

        if (asteroid.isPotentiallyHazardousAsteroid)
            binding.todayImage.load(R.drawable.asteroid_hazardous)
        else binding.todayImage.load(R.drawable.asteroid_safe)

        binding.closeApproachDate.text = asteroidData.closeApproachDate
        binding.absoluteMagnitude.text = asteroid.absoluteMagnitude.toString() + " au"
        binding.estimatedDiameter.text = asteroid.estimatedDiameter.kilometers.estimatedDiameterMax.toString() + " km"
        binding.relativeVelocity.text = asteroidData.relativeVelocity.kilometersPerSeconds + " km/s"
        binding.distanceFromEarth.text = asteroidData.missDistance.astronomical + " au"

        binding.information.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                this.setMessage("""
                    The astronomical unit (au) is a unit of length, roughly the distance from Earth
                    th the sun and equal to about 150 million kilometres (93 million miles)
                """.trimIndent())
                this.setPositiveButton("OK", null)
                this.create()
            }.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}