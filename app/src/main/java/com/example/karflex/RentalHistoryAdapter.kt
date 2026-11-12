package com.example.karflex

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RentalHistoryAdapter :
    RecyclerView.Adapter<RentalHistoryAdapter.HistoryViewHolder>() {

    private val rentals = mutableListOf<Rental>()

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val ivVehicle: ImageView =
            view.findViewById(R.id.ivHistoryVehicle)

        val tvBrand: TextView =
            view.findViewById(R.id.tvHistoryBrand)

        val tvModel: TextView =
            view.findViewById(R.id.tvHistoryModel)

        val tvDuration: TextView =
            view.findViewById(R.id.tvHistoryDuration)

        val tvStartDate: TextView =
            view.findViewById(R.id.tvHistoryStartDate)

        val tvStatus: TextView =
            view.findViewById(R.id.tvHistoryStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rental_history, parent, false)

        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HistoryViewHolder,
        position: Int
    ) {

        val rental = rentals[position]
        val vehicle = rental.vehicle

        holder.ivVehicle.setImageResource(vehicle.imageRes)
        holder.ivVehicle.contentDescription = vehicle.modelName

        holder.tvBrand.text = vehicle.brand
        holder.tvModel.text = vehicle.modelName

        holder.tvDuration.text =
            holder.itemView.resources.getQuantityString(
                R.plurals.days,
                rental.duration,
                rental.duration
            )

        holder.tvStartDate.text = rental.startDate.toDisplayDate()

        holder.tvStatus.text = rental.status

        if (rental.status == Rental.STATUS_ACTIVE) {
            holder.tvStatus.setTextColor(Color.parseColor("#22C55E"))
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#9CA3AF"))
        }
    }

    override fun getItemCount(): Int = rentals.size

    fun submitList(newList: List<Rental>) {
        rentals.clear()
        rentals.addAll(newList)
        notifyDataSetChanged()
    }
}
