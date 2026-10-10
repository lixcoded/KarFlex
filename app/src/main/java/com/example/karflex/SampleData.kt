package com.example.karflex

import java.time.LocalDate

/**
 * TEMPORARY in-memory data so the Home page can be previewed before the
 * Vehicle / Rentals tables and the Volley JSON fetch are built.
 *
 * Vehicles are copied from karflex.json. Replace every call to this object
 * with DatabaseHelper queries once the database layer is ready.
 */
object SampleData {

    private val vehicles = listOf(
        Vehicle(
            id = 1,
            modelName = "Honda Vario 160",
            type = Vehicle.TYPE_MOTORCYCLE,
            brand = "Honda",
            pricePerDay = 90000,
            transmission = "Automatic",
            image = "https://honda-motor.id/wp-content/uploads/2024/02/New-Honda-Vario-160-CBS-Active-Matte-Black.png",
            description = "A nimble scooter with excellent fuel economy, ideal for city mobility."
        ),
        Vehicle(
            id = 2,
            modelName = "Yamaha NMAX",
            type = Vehicle.TYPE_MOTORCYCLE,
            brand = "Yamaha",
            pricePerDay = 100000,
            transmission = "Automatic",
            image = "https://yamahacianjur.com/wp-content/uploads/2023/10/NMAX-STD.jpg",
            description = "A premium automatic motorcycle offering comfort for long-distance rides."
        ),
        Vehicle(
            id = 3,
            modelName = "Toyota Avanza",
            type = Vehicle.TYPE_CAR,
            brand = "Toyota",
            pricePerDay = 350000,
            transmission = "Manual",
            image = "https://d1g6w7sntckt92.cloudfront.net/public/images/car_gallery_images/vUYy6ktf3RYqwaQJtfpyXox5h2FhAsvfgeu01QEy.jpg",
            description = "A family MPV with 7-passenger capacity, suitable for long trips."
        ),
        Vehicle(
            id = 4,
            modelName = "Honda Brio",
            type = Vehicle.TYPE_CAR,
            brand = "Honda",
            pricePerDay = 300000,
            transmission = "Automatic",
            image = "https://dealermobilhondabanjarmasin.com/wp-content/uploads/2018/09/All-New-brio-Banjarmasin.png",
            description = "A nimble, fuel-efficient city car for everyday use."
        )
    )

    private val rentals = mutableListOf(
        Rental(
            id = 1,
            vehicle = vehicles[2],
            userId = 1,
            duration = 3,
            startDate = LocalDate.now().minusDays(1).toString(),
            status = Rental.STATUS_ACTIVE
        )
    )

    // A vehicle counts as rented out while it has an Active rental.
    fun vehicles(type: String? = null): List<Vehicle> =
        vehicles
            .filter { type == null || it.type == type }
            .map { vehicle ->
                vehicle.copy(
                    isRented = rentals.any {
                        it.vehicle.id == vehicle.id && it.status == Rental.STATUS_ACTIVE
                    }
                )
            }

    fun activeRentals(): List<Rental> =
        rentals.filter { it.status == Rental.STATUS_ACTIVE }

    fun updateDuration(rentalId: Int, duration: Int) {
        val index = rentals.indexOfFirst { it.id == rentalId }
        if (index != -1) {
            rentals[index] = rentals[index].copy(duration = duration)
        }
    }

    fun deleteRental(rentalId: Int) {
        rentals.removeAll { it.id == rentalId }
    }
}
