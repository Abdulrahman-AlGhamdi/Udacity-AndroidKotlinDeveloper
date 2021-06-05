package com.udacity.project4.ui.map

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.project4.R
import com.android.project4.databinding.FragmentMapsBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.ui.RemindersListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapFragment: SupportMapFragment
    private val viewModel: RemindersListViewModel by viewModel()
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        setHasOptionsMenu(true)
        createNotificationChannel()

        mapFragment.getMapAsync {
            googleMap = it
            showAllGeofences()
            trackUserCurrentLocation()
            addMarkerOnLongClick()
            addMarkerOnPoiClick()
            saveReminder()
        }
    }



    private fun showAllGeofences() {
        viewModel.getAllReminders()
        viewModel.getRemindersState.observe(viewLifecycleOwner, {
            if (it is RemindersListViewModel.RemindersState.ReminderList) {
                it.reminderList.forEach { reminder ->
                    if (reminder.latitude != null && reminder.longitude != null) {
                        val latLng = LatLng(reminder.latitude, reminder.longitude)
                        reminder.title?.let { title ->
                            showMarker(latLng, title)
                        }
                    }
                }
            }
        })
    }

    private fun trackUserCurrentLocation() {
        val userLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
        val locationRequest = LocationRequest.create().apply {
            this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            this.interval = 5000
        }
        val foreground = (checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)
        val background = (checkSelfPermission(requireContext(), ACCESS_BACKGROUND_LOCATION) == PERMISSION_GRANTED)

        userLocation.requestLocationUpdates(locationRequest, object : LocationCallback() {
            @SuppressLint("MissingPermission")
            override fun onLocationResult(location: LocationResult) {
                super.onLocationResult(location)

                val lastLocation = location.lastLocation

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (foreground && background) {
                        googleMap.isMyLocationEnabled = true
                        location.lastLocation
                        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        userLocation.removeLocationUpdates(this)
                    } else permissionRequest.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION))
                } else {
                    if (foreground) {
                        googleMap.isMyLocationEnabled = true
                        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        userLocation.removeLocationUpdates(this)
                    } else permissionRequest.launch(arrayOf(ACCESS_FINE_LOCATION))
                }
            }
        }, Looper.getMainLooper())
    }

    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.values.toSet().contains(true)) init()
            else Snackbar.make(
            requireView(),
            "Location permission needed to use the app",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun addMarkerOnLongClick() {
        googleMap.setOnMapLongClickListener {
            googleMap.clear()
            showAllGeofences()

            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                it.latitude,
                it.longitude
            )

            showMarker(it, snippet)
            saveReminder(it)
        }
    }

    private fun addMarkerOnPoiClick() {
        googleMap.setOnPoiClickListener {
            googleMap.clear()
            showAllGeofences()

            showMarker(it.latLng, it.name)
            saveReminder(it.latLng)
        }
    }

    private fun showMarker(latLng: LatLng, title: String) {
        val marker = MarkerOptions()
            .position(latLng)
            .title(title)

        val circle = CircleOptions()
            .center(latLng)
            .strokeWidth(1f)
            .fillColor(Color.argb(70, 150, 150, 150))
            .radius(500.0)

        googleMap.addMarker(marker)?.apply {
            this.showInfoWindow()
        }
        googleMap.addCircle(circle)
    }

    private fun saveReminder(latLng: LatLng? = null) {
        binding.save.setOnClickListener {
            if (latLng != null) {
                val action = MapsFragmentDirections.actionMapsFragmentToSaveFragment(
                    latitude = latLng.latitude.toFloat(),
                    longitude = latLng.longitude.toFloat()
                )
                findNavController().navigate(action)
            } else Toast.makeText(requireContext(), "Please select a location to save it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.map_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.normal_map -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            R.id.hybrid_map -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
            R.id.satellite_map -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            }
            R.id.terrain_map -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
        const val GEOFENCE_REQUEST_ID = "geofence_request_id"
    }
}