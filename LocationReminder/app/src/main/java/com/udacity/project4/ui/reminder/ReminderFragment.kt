package com.udacity.project4.ui.reminder

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.project4.R
import com.android.project4.databinding.FragmentReminderBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.ui.RemindersListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderFragment : Fragment() {

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!
    private val reminderViewModel: RemindersListViewModel by viewModel()
    private val adapter = ReminderAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {
        setHasOptionsMenu(true)
        getAllReminders()
        deleteReminder()
        binding.floating.setOnClickListener { checkLocationIsEnabled() }
    }

    private fun getAllReminders() {
        reminderViewModel.getAllReminders()
        reminderViewModel.getRemindersState.observe(viewLifecycleOwner, {
            when (it) {
                is RemindersListViewModel.RemindersState.Empty -> {
                    binding.progress.visibility = View.GONE
                    binding.empty.visibility = View.VISIBLE
                    binding.list.visibility = View.GONE
                }
                is RemindersListViewModel.RemindersState.Failed -> {
                    it.message?.let { Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show() }
                }
                is RemindersListViewModel.RemindersState.Loading -> {
                    binding.empty.visibility = View.GONE
                    binding.list.visibility = View.GONE
                    binding.progress.visibility = View.VISIBLE
                }
                is RemindersListViewModel.RemindersState.ReminderList -> {
                    adapter.differ.submitList(it.reminderList)
                    binding.list.adapter = adapter
                    binding.progress.visibility = View.GONE
                    binding.empty.visibility = View.GONE
                    binding.list.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun deleteReminder() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val reminder = adapter.differ.currentList[position]
                reminderViewModel.deleteReminder(reminder)
                Snackbar.make(requireView(), "Reminder Successfully Deleted", Snackbar.LENGTH_SHORT)
                    .setAction("Undo") { lifecycleScope.launchWhenCreated {
                        reminderViewModel.saveReminder(reminder)
                    } }.show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.list)
    }

    private fun checkLocationIsEnabled() {
        val locationRequest = LocationRequest.create().apply {
            this.priority = LocationRequest.PRIORITY_LOW_POWER
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponse = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponse.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) exception.startResolutionForResult(requireActivity(), 5)
            else exception.message?.let { Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show() }
        }

        locationSettingsResponse.addOnCompleteListener {
            if (it.isSuccessful) requestLocationPermissions()
        }
    }

    private fun requestLocationPermissions() {
        val foreground = (PERMISSION_GRANTED == checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION))
        val background = (PERMISSION_GRANTED == checkSelfPermission(requireContext(), ACCESS_BACKGROUND_LOCATION))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (foreground && background) {
                val action = ReminderFragmentDirections.actionReminderFragmentToMapsFragment()
                findNavController().navigate(action)
            } else permissionRequest.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION))
        } else {
            if (foreground) {
                val action = ReminderFragmentDirections.actionReminderFragmentToMapsFragment()
                findNavController().navigate(action)
            } else permissionRequest.launch(arrayOf(ACCESS_FINE_LOCATION))
        }
    }

    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.values.toSet().contains(true)) {
            val action = ReminderFragmentDirections.actionReminderFragmentToMapsFragment()
            findNavController().navigate(action)
        } else Snackbar.make(requireView(), "Location permission needed to use the app", Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.reminder_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout_menu) {
            AuthUI.getInstance().signOut(requireContext())
            val action = ReminderFragmentDirections.actionReminderFragmentToLoginFragment()
            findNavController().navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}