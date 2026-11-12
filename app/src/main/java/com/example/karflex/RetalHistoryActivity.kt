package com.example.karflex

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.widget.addTextChangedListener

class RentalHistoryActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    private lateinit var adapter: RentalHistoryAdapter
    private lateinit var etSearchRental: EditText
    private lateinit var rvRentalHistory: RecyclerView
    private lateinit var tvEmptyHistory: TextView

    private var allRentals: List<Rental> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rental_history)

        findViewById<View>(R.id.root).applySystemBarsPadding(
            top = true,
            bottom = true
        )

        databaseHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = "Rental History"
            setNavigationOnClickListener {
                finish()
            }
        }

        etSearchRental = findViewById(R.id.etSearchRental)
        rvRentalHistory = findViewById(R.id.rvRentalHistory)
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory)

        adapter = RentalHistoryAdapter()

        rvRentalHistory.layoutManager = LinearLayoutManager(this)
        rvRentalHistory.adapter = adapter

        etSearchRental.addTextChangedListener { text ->
            filterRentals(text?.toString().orEmpty())
        }

        loadRentalHistory()
    }

    private fun loadRentalHistory() {

        val userId = sessionManager.userId

        if (userId <= 0) {
            allRentals = emptyList()
            showRentals(emptyList())
            return
        }

        allRentals = databaseHelper.getRentalHistory(userId)

        filterRentals(etSearchRental.text.toString())
    }

    private fun filterRentals(query: String) {

        val filteredList = if (query.isBlank()) {
            allRentals
        } else {
            allRentals.filter { rental ->
                rental.vehicle.modelName.contains(
                    query.trim(),
                    ignoreCase = true
                )
            }
        }

        showRentals(filteredList)
    }

    private fun showRentals(rentals: List<Rental>) {

        adapter.submitList(rentals)

        val isEmpty = rentals.isEmpty()

        tvEmptyHistory.visibility =
            if (isEmpty) View.VISIBLE else View.GONE

        rvRentalHistory.visibility =
            if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()

        if (::databaseHelper.isInitialized) {
            loadRentalHistory()
        }
    }
}
