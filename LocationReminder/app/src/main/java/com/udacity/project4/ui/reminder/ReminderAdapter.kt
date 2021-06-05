package com.udacity.project4.ui.reminder

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.project4.databinding.ItemReminderRowBinding
import com.udacity.project4.model.ReminderModel
import com.udacity.project4.service.LocationReminderService
import com.udacity.project4.ui.details.DetailsActivity
import java.util.*

class ReminderAdapter : Adapter<ReminderAdapter.ReminderViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ReminderModel>() {
        override fun areItemsTheSame(oldItem: ReminderModel, newItem: ReminderModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReminderModel, newItem: ReminderModel): Boolean {
            return oldItem == newItem
        }
    })

    inner class ReminderViewHolder(private val binding: ItemReminderRowBinding) : ViewHolder(binding.root) {

        fun bind(reminder: ReminderModel) {
            binding.title.text = reminder.title

            if (reminder.description.isNullOrEmpty())
                binding.description.visibility = View.GONE
            else binding.description.text = reminder.description

            binding.location.text = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                reminder.latitude,
                reminder.longitude
            )

            binding.root.setOnClickListener {
                itemView.context.startActivity(
                    Intent(itemView.context, DetailsActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra(LocationReminderService.REMINDER, reminder)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(
            ItemReminderRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size
}