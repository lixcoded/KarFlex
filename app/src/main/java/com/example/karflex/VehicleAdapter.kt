package com.example.karflex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class VehicleAdapter(
    private val onVehicleClick: (Vehicle) -> Unit
) : ListAdapter<Vehicle, VehicleAdapter.VehicleViewHolder>(DiffCallback) {

    class VehicleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.cardVehicle)
        val ivVehicle: ImageView = view.findViewById(R.id.ivVehicle)
        val tvType: TextView = view.findViewById(R.id.tvType)
        val tvRentedBadge: TextView = view.findViewById(R.id.tvRentedBadge)
        val tvBrand: TextView = view.findViewById(R.id.tvBrand)
        val tvModelName: TextView = view.findViewById(R.id.tvModelName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvPriceUnit: TextView = view.findViewById(R.id.tvPriceUnit)
        val tvTransmission: TextView = view.findViewById(R.id.tvTransmission)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = getItem(position)
        val context = holder.itemView.context

        holder.ivVehicle.setImageResource(vehicle.imageRes)
        holder.ivVehicle.contentDescription = vehicle.modelName
        holder.tvType.text = vehicle.type
        holder.tvBrand.text = vehicle.brand
        holder.tvModelName.text = vehicle.modelName
        holder.tvPrice.text = vehicle.pricePerDay.toRupiah()
        holder.tvTransmission.text = vehicle.transmission

        // Rented-out vehicles get a red card
        val rented = vehicle.isRented
        holder.tvRentedBadge.isVisible = rented
        holder.card.setCardBackgroundColor(
            ContextCompat.getColor(context, if (rented) R.color.kf_red else R.color.kf_surface)
        )
        holder.card.strokeColor =
            ContextCompat.getColor(context, if (rented) R.color.kf_red_stroke else R.color.kf_stroke)

        val subtleText = ContextCompat.getColor(
            context,
            if (rented) R.color.kf_red_soft else R.color.kf_text_label
        )
        holder.tvBrand.setTextColor(subtleText)
        holder.tvPriceUnit.setTextColor(subtleText)

        holder.card.setOnClickListener { onVehicleClick(vehicle) }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Vehicle>() {
        override fun areItemsTheSame(oldItem: Vehicle, newItem: Vehicle) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Vehicle, newItem: Vehicle) =
            oldItem == newItem
    }
}
