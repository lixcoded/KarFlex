package com.example.karflex

object SampleData {

    private val vehicleList = listOf(
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

    fun vehicles(type: String? = null): List<Vehicle> {
        return vehicleList
            .filter { type == null || it.type == type }
    }

    fun vehicleById(vehicleId: Int): Vehicle? {
        return vehicleList.find { it.id == vehicleId }
    }
}
