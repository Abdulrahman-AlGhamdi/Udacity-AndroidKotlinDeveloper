package com.udacity.project4.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.project4.databinding.ActivityDetailsBinding
import com.udacity.project4.model.ReminderModel
import com.udacity.project4.service.LocationReminderService.Companion.REMINDER
import java.util.*

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val reminder = intent.getParcelableExtra(REMINDER) as? ReminderModel
        binding.title.text = reminder?.title

        if (reminder?.description.isNullOrEmpty())
            binding.description.visibility = View.GONE
        else binding.description.text = reminder?.description

        binding.location.text = String.format(
            Locale.getDefault(),
            "Lat: %1$.5f, Long: %2$.5f",
            reminder?.latitude,
            reminder?.longitude
        )
    }
}