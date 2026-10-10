package com.example.karflex

import androidx.annotation.DrawableRes

// Mirrors the Vehicle table in the ERD.
data class Vehicle(
    val id: Int,
    val modelName: String,
    val type: String,
    val brand: String,
    val pricePerDay: Int,
    val transmission: String,
    val image: String,
    val description: String,
    val isRented: Boolean = false
) {
    companion object {
        const val TYPE_CAR = "Car"
        const val TYPE_MOTORCYCLE = "Motorcycle"
    }
}

// Mirrors the Rentals table in the ERD (VehicleID resolved into the full Vehicle).
data class Rental(
    val id: Int,
    val vehicle: Vehicle,
    val userId: Int,
    val duration: Int,
    val startDate: String,
    val status: String
) {
    companion object {
        const val STATUS_ACTIVE = "Active"
        const val STATUS_COMPLETED = "Completed"
    }
}

// Vehicle photos ship with the app (res/drawable-nodpi), matched by model name,
// so they keep working once the data comes from the JSON / database instead.
@get:DrawableRes
val Vehicle.imageRes: Int
    get() = when {
        modelName.contains("Avanza", ignoreCase = true) -> R.drawable.vehicle_avanza
        modelName.contains("Brio", ignoreCase = true) -> R.drawable.vehicle_brio
        modelName.contains("Vario", ignoreCase = true) -> R.drawable.vehicle_vario
        modelName.contains("NMAX", ignoreCase = true) -> R.drawable.vehicle_nmax
        type == Vehicle.TYPE_MOTORCYCLE -> R.drawable.vehicle_nmax
        else -> R.drawable.ic_car
    }
