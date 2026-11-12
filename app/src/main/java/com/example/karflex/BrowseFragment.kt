package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BrowseFragment : Fragment(R.layout.fragment_browse) {

    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var tvVehicleCount: TextView
    private lateinit var filterViews: Map<TextView, String?>

    private var selectedType: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedType = savedInstanceState?.getString(STATE_SELECTED_TYPE) ?: selectedType

        val session = SessionManager(requireContext())
        val username = session.username ?: getString(R.string.guest)

        view.findViewById<TextView>(R.id.tvGreeting).text =
            getString(R.string.browse_greeting, username)

        view.findViewById<TextView>(R.id.tvAvatar).apply {
            text = username.take(1).uppercase()

            setOnClickListener {
                (activity as? MainActivity)?.selectSection(R.id.nav_profile)
            }
        }

        tvVehicleCount = view.findViewById(R.id.tvVehicleCount)

        vehicleAdapter = VehicleAdapter { vehicle ->
            openVehicle(vehicle)
        }

        view.findViewById<RecyclerView>(R.id.rvVehicles).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = vehicleAdapter
            itemAnimator = null
        }

        // FILTER BY TYPE
        filterViews = mapOf(
            view.findViewById<TextView>(R.id.filterAll) to null,
            view.findViewById<TextView>(R.id.filterCar) to Vehicle.TYPE_CAR,
            view.findViewById<TextView>(R.id.filterMotorcycle) to Vehicle.TYPE_MOTORCYCLE
        )

        filterViews.forEach { (filterView, type) ->
            filterView.setOnClickListener {
                selectedType = type
                loadVehicles()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_SELECTED_TYPE, selectedType)
    }

    override fun onResume() {
        super.onResume()
        loadVehicles()
    }

    // Refresh when Browse is shown again.
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            loadVehicles()
        }
    }

    private fun loadVehicles() {
        if (view == null) return

        filterViews.forEach { (filterView, type) ->
            filterView.isSelected = type == selectedType
        }

        // Get currently rented vehicle IDs from SQLite.
        val databaseHelper = DatabaseHelper(requireContext())
        val rentedVehicleIds = databaseHelper.getRentedVehicleIds()

        // Mark rented vehicles so VehicleAdapter can display them in red.
        val vehicles = SampleData.vehicles(selectedType).map { vehicle ->
            vehicle.copy(
                isRented = vehicle.id in rentedVehicleIds
            )
        }

        vehicleAdapter.submitList(vehicles)

        tvVehicleCount.text =
            resources.getQuantityString(
                R.plurals.vehicle_count,
                vehicles.size,
                vehicles.size
            )
    }

    private fun openVehicle(vehicle: Vehicle) {
        val intent = Intent(
            requireContext(),
            VehicleDetailActivity::class.java
        ).apply {
            putExtra(VehicleDetailActivity.EXTRA_VEHICLE_ID, vehicle.id)
            putExtra(VehicleDetailActivity.EXTRA_VEHICLE_IMAGE, vehicle.imageRes)
            putExtra(VehicleDetailActivity.EXTRA_VEHICLE_BRAND, vehicle.brand)
            putExtra(VehicleDetailActivity.EXTRA_VEHICLE_MODEL, vehicle.modelName)
            putExtra(VehicleDetailActivity.EXTRA_VEHICLE_TYPE, vehicle.type)
            putExtra(VehicleDetailActivity.EXTRA_VEHICLE_TRANSMISSION, vehicle.transmission)
            putExtra(VehicleDetailActivity.EXTRA_VEHICLE_PRICE, vehicle.pricePerDay)
            putExtra(VehicleDetailActivity.EXTRA_VEHICLE_DESCRIPTION, vehicle.description)
            putExtra(VehicleDetailActivity.EXTRA_VEHICLE_IS_RENTED, vehicle.isRented)
        }

        startActivity(intent)
    }

    private companion object {
        const val STATE_SELECTED_TYPE = "selected_vehicle_type"
    }
}
