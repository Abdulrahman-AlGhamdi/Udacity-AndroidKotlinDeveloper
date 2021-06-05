package com.example.loadapp.ui.home

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.loadapp.MainActivity
import com.example.loadapp.MainActivity.Companion.KEY
import com.example.loadapp.MainActivity.Companion.NAME
import com.example.loadapp.MainActivity.Companion.STATUS
import com.example.loadapp.R
import com.example.loadapp.customview.LoadingButton
import com.example.loadapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var loadingButton: LoadingButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        loadingButton = binding.loadingButton
        radioGroupAction()
        createNotificationChannel()

        return binding.root
    }

    private fun radioGroupAction() {
        loadingButton.setOnClickListener {
            when (binding.radioGroup.checkedRadioButtonId) {
                binding.radioCoil.id -> downloadManager(COIL_REPOSITORY, "Coil Repository")
                binding.radioStarter.id -> downloadManager(STARTER_CODE_REPOSITORY, "Starter Code Repository")
                binding.radioRetrofit.id -> downloadManager(RETROFIT_REPOSITORY, "Retrofit Repository")
                else -> Toast.makeText(requireContext(), "Please select a file to download", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadManager(link: String, name: String) {
        binding.loadingButton.buttonState = ButtonState.Loading
        val request = DownloadManager.Request(Uri.parse(link))
            .setTitle(getString(R.string.app_name))
            .setDescription("File Downloaded")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalFilesDir(requireContext(), null, "file.zip")

        val downloadManager = requireContext().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)

        var isDownloading = true
        lifecycleScope.launch(Dispatchers.IO) {
            while (isDownloading) {
                val cursor = downloadManager.query(query)
                if (cursor != null && cursor.count >= 0 && cursor.moveToFirst()) {
                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_PAUSED -> {
                            isDownloading = false
                            showNotification(name, "download paused")
                            withContext(Dispatchers.Main) {
                                binding.loadingButton.buttonState = ButtonState.Completed
                            }
                        }
                        DownloadManager.STATUS_FAILED -> {
                            isDownloading = false
                            showNotification(name, "download failed")
                            withContext(Dispatchers.Main) {
                                binding.loadingButton.buttonState = ButtonState.Completed
                            }
                        }
                        DownloadManager.STATUS_RUNNING -> {

                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            isDownloading = false
                            showNotification(name, "download successful")
                            withContext(Dispatchers.Main) {
                                binding.loadingButton.buttonState = ButtonState.Completed
                            }
                        }
                    }
                } else {
                    isDownloading = false
                    showNotification(name, "download canceled")
                    withContext(Dispatchers.Main) {
                        binding.loadingButton.buttonState = ButtonState.Completed
                    }
                }
            }
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
                requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(name: String, status: String) {
        val intent = Intent(requireContext(), MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .putExtra(KEY, true)
            .putExtra(NAME, name)
            .putExtra(STATUS, status)

        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val action = NotificationCompat.Action.Builder(0,"Show download details", pendingIntent).build()

        val build = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(name)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(action)
            .build()

        NotificationManagerCompat.from(requireContext()).notify(NOTIFICATION_ID, build)
    }

    sealed class ButtonState {
        object Loading : ButtonState()
        object Completed : ButtonState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
        private const val NOTIFICATION_ID = 0
        private const val COIL_REPOSITORY = "https://github.com/coil-kt/coil"
        private const val RETROFIT_REPOSITORY = "https://github.com/square/retrofit"
        private const val STARTER_CODE_REPOSITORY = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
    }
}