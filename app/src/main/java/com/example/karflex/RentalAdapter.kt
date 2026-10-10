package com.example.karflex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

/**
 * @param onSaveDuration returns true when the new duration was accepted,
 *   which closes the inline update form.
 */
class RentalAdapter(
    private val onSaveDuration: (Rental, String) -> Boolean,
    private val onCancelRental: (Rental) -> Unit
) : ListAdapter<Rental, RentalAdapter.RentalViewHolder>(DiffCallback) {

    // Rental whose "update duration" form is currently open
    private var editingRentalId: Int? = null

    class RentalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivVehicle: ImageView = view.findViewById(R.id.ivRentalVehicle)
        val tvBrand: TextView = view.findViewById(R.id.tvRentalBrand)
        val tvModel: TextView = view.findViewById(R.id.tvRentalModel)
        val tvStatus: TextView = view.findViewById(R.id.tvRentalStatus)
        val tvStartDate: TextView = view.findViewById(R.id.tvRentalStartDate)
        val tvDuration: TextView = view.findViewById(R.id.tvRentalDuration)
        val layoutEditDuration: View = view.findViewById(R.id.layoutEditDuration)
        val etDuration: EditText = view.findViewById(R.id.etDuration)
        val btnSaveDuration: MaterialButton = view.findViewById(R.id.btnSaveDuration)
        val btnUpdate: MaterialButton = view.findViewById(R.id.btnUpdateRental)
        val btnCancel: MaterialButton = view.findViewById(R.id.btnCancelRental)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rental, parent, false)
        return RentalViewHolder(view)
    }

    override fun onBindViewHolder(holder: RentalViewHolder, position: Int) {
        val rental = getItem(position)
        val resources = holder.itemView.resources
        val isEditing = rental.id == editingRentalId

        holder.ivVehicle.setImageResource(rental.vehicle.imageRes)
        holder.ivVehicle.contentDescription = rental.vehicle.modelName
        holder.tvBrand.text = rental.vehicle.brand
        holder.tvModel.text = rental.vehicle.modelName
        holder.tvStatus.text = rental.status
        holder.tvStartDate.text = rental.startDate.toDisplayDate()
        holder.tvDuration.text =
            resources.getQuantityString(R.plurals.days, rental.duration, rental.duration)

        holder.layoutEditDuration.isVisible = isEditing
        holder.btnUpdate.setText(if (isEditing) R.string.action_close else R.string.action_update)
        if (!isEditing) {
            holder.etDuration.text.clear()
        } else if (holder.etDuration.text.isEmpty()) {
            holder.etDuration.setText(rental.duration.toString())
        }

        holder.btnUpdate.setOnClickListener {
            if (isEditing) hideKeyboard(holder.etDuration)
            toggleEditing(rental.id)
        }

        holder.btnSaveDuration.setOnClickListener {
            if (onSaveDuration(rental, holder.etDuration.text.toString())) {
                hideKeyboard(holder.etDuration)
                toggleEditing(rental.id)
            }
        }

        holder.btnCancel.setOnClickListener {
            onCancelRental(rental)
        }
    }

    private fun hideKeyboard(field: EditText) {
        field.clearFocus()
        field.context.getSystemService(InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(field.windowToken, 0)
    }

    private fun toggleEditing(rentalId: Int) {
        val previous = editingRentalId
        editingRentalId = if (previous == rentalId) null else rentalId

        listOfNotNull(previous, editingRentalId).distinct().forEach { id ->
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) notifyItemChanged(index)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Rental>() {
        override fun areItemsTheSame(oldItem: Rental, newItem: Rental) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Rental, newItem: Rental) =
            oldItem == newItem
    }
}
