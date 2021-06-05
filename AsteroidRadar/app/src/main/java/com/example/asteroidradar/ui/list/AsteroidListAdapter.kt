package com.example.asteroidradar.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.asteroidradar.R
import com.example.asteroidradar.databinding.RowAsteroidItemBinding
import com.example.asteroidradar.models.Asteroid
import com.example.asteroidradar.ui.list.AsteroidListAdapter.*

class AsteroidListAdapter(
    private val asteroidList: List<Asteroid>
) : RecyclerView.Adapter<AsteroidViewHolder>() {

    inner class AsteroidViewHolder(private val binding: RowAsteroidItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(asteroid: Asteroid) {
            binding.date.text = asteroid.closeApproachData.first().closeApproachDate
            binding.name.text = asteroid.name
            if (asteroid.isPotentiallyHazardousAsteroid)
                binding.image.load(R.drawable.ic_status_potentially_hazardous)
            else binding.image.load(R.drawable.ic_status_normal)

            binding.root.setOnClickListener {
                val direction = AsteroidListFragmentDirections
                val action = direction.actionAsteroidListFragmentToAsteroidDetailFragment(asteroid)
                itemView.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder(
            RowAsteroidItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.bind(asteroidList[position])
    }

    override fun getItemCount() = asteroidList.size
}