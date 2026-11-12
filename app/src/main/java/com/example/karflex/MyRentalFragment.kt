package com.example.karflex

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyRentalFragment : Fragment(R.layout.fragment_my_rental) {

    private lateinit var rentalAdapter: RentalAdapter
    private lateinit var rvRentals: RecyclerView
    private lateinit var layoutEmpty: View

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        rvRentals = view.findViewById(R.id.rvRentals)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)

        rentalAdapter = RentalAdapter(
            onSaveDuration = ::updateDuration,
            onCancelRental = ::confirmCancel
        )

        rvRentals.layoutManager = LinearLayoutManager(requireContext())
        rvRentals.adapter = rentalAdapter

        view.findViewById<MaterialButton>(R.id.btnBrowse).setOnClickListener {
            (activity as? MainActivity)?.selectSection(R.id.nav_browse)
        }
    }

    override fun onResume() {
        super.onResume()
        loadRentals()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            loadRentals()
        }
    }

    private fun loadRentals() {
        if (view == null) return

        // Only show rentals belonging to the logged-in user.
        val userId = sessionManager.userId

        val rentals = if (sessionManager.isLoggedIn && userId > 0) {
            databaseHelper.getActiveRentals(userId)
        } else {
            emptyList()
        }

        rentalAdapter.submitList(rentals)

        rvRentals.isVisible = rentals.isNotEmpty()
        layoutEmpty.isVisible = rentals.isEmpty()
    }

    // =========================
    // UPDATE DURATION
    // =========================

    private fun updateDuration(rental: Rental, input: String): Boolean {
        val duration = input.trim().toIntOrNull()

        if (duration == null || duration <= 0) {
            showToast(R.string.error_duration_invalid)
            return false
        }

        val userId = sessionManager.userId

        if (!sessionManager.isLoggedIn || userId <= 0) {
            showMessage("Please log in again.")
            return false
        }

        val updated = databaseHelper.updateRentalDuration(
            transactionId = rental.id,
            userId = userId,
            duration = duration
        )

        if (updated) {
            showToast(R.string.rental_updated)
            loadRentals()
            return true
        }

        showMessage("Unable to update rental. Please try again.")
        return false
    }

    // =========================
    // CANCEL RENTAL
    // =========================

    private fun confirmCancel(rental: Rental) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.cancel_rental_title)
            .setMessage(
                getString(
                    R.string.cancel_rental_message,
                    rental.vehicle.modelName
                )
            )
            .setNegativeButton(R.string.cancel_rental_keep, null)
            .setPositiveButton(R.string.cancel_rental_confirm) { _, _ ->

                val userId = sessionManager.userId

                if (!sessionManager.isLoggedIn || userId <= 0) {
                    showMessage("Please log in again.")
                    return@setPositiveButton
                }

                val deleted = databaseHelper.deleteRental(
                    transactionId = rental.id,
                    userId = userId
                )

                if (deleted) {
                    showToast(R.string.rental_cancelled)
                    loadRentals()
                } else {
                    showMessage("Unable to cancel rental. Please try again.")
                }
            }
            .show()
    }

    private fun showToast(messageRes: Int) {
        Toast.makeText(
            requireContext(),
            messageRes,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
