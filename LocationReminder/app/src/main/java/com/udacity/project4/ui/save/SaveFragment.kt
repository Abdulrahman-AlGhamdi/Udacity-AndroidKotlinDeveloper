package com.udacity.project4.ui.save

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.project4.databinding.FragmentSaveBinding
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.broadcast.GeofenceBroadcastReceiver
import com.udacity.project4.model.ReminderModel
import com.udacity.project4.ui.RemindersListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class SaveFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSaveBinding
    private val reminderViewModel: RemindersListViewModel by viewModel()
    private val saveViewModel: SaveReminderViewModel by viewModels()
    private val argument: SaveFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveBinding.inflate(inflater, container, false)

        val reminder = ReminderModel()
        binding.viewModel = saveViewModel
        binding.reminder = reminder
        binding.lifecycleOwner = this
        saveReminder()

        return binding.root
    }

    private fun saveReminder() {
        binding.save.setOnClickListener {
            val reminder = ReminderModel(
                title = saveViewModel.reminderTitle.value,
                description = saveViewModel.reminderDescription.value,
                latitude = argument.latitude.toDouble(),
                longitude = argument.longitude.toDouble()
            )

            when (saveViewModel.validateEnteredData(reminder)) {
                1 -> showMessage("Please add title")
                2 -> showMessage("Please add location")
                3 -> {
                    lifecycleScope.launchWhenCreated {
                        reminderViewModel.saveReminder(reminder)
                        reminderViewModel.getAllReminders()
                        reminderViewModel.getRemindersState.observe(viewLifecycleOwner, {
                            if (it is RemindersListViewModel.RemindersState.ReminderList) {
                                if (it.reminderList.isNotEmpty()) {
                                    createGeofence(it.reminderList.last())
                                    val action = SaveFragmentDirections.actionSaveFragmentToReminderFragment()
                                    findNavController().navigate(action)
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    private fun createGeofence(reminder: ReminderModel) {
        val geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        val geofence = Geofence.Builder()
            .setRequestId((reminder.id).toString())
            .setCircularRegion(reminder.latitude!!.toDouble(), reminder.longitude!!.toDouble(), 500f)
            .setExpirationDuration(TimeUnit.HOURS.toMillis(5))
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val pendingIntent: PendingIntent by lazy {
            val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
            PendingIntent.getBroadcast(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
            geofencingClient.addGeofences(geofenceRequest, pendingIntent).run {
                this.addOnSuccessListener {
                    Log.d("TAG_TEST", "Geofence added")
                }
                this.addOnFailureListener {
                    Log.d("TAG_TEST", "Failed to add geofence")
                }
            }
    }

    private fun showMessage(message: String) = requireDialog().window?.decorView?.let {
        Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
    }
}