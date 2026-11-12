package com.example.karflex

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.appbar.MaterialToolbar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VehicleDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_VEHICLE_ID = "vehicle_id"
        const val EXTRA_VEHICLE_IMAGE = "vehicle_image"
        const val EXTRA_VEHICLE_BRAND = "vehicle_brand"
        const val EXTRA_VEHICLE_MODEL = "vehicle_model"
        const val EXTRA_VEHICLE_TYPE = "vehicle_type"
        const val EXTRA_VEHICLE_TRANSMISSION = "vehicle_transmission"
        const val EXTRA_VEHICLE_PRICE = "vehicle_price"
        const val EXTRA_VEHICLE_DESCRIPTION = "vehicle_description"
        const val EXTRA_VEHICLE_IS_RENTED = "vehicle_is_rented"
    }

    private lateinit var databaseHelper: DatabaseHelper

    private var vehicleId: Int = -1
    private var vehiclePrice: Int = 0
    private var vehicleIsRented: Boolean = false

    private lateinit var etRentalDays: EditText
    private lateinit var tvDetailTotalPrice: TextView
    private lateinit var layoutRentForm: LinearLayout
    private lateinit var tvRentedOutNotice: TextView
    private lateinit var btnRentVehicle: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)

        // Apply status bar inset so the toolbar is not covered
        val rootView = findViewById<View>(R.id.root)

        val initialLeft = rootView.paddingLeft
        val initialTop = rootView.paddingTop
        val initialRight = rootView.paddingRight
        val initialBottom = rootView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val statusBarHeight = insets.getInsets(
                WindowInsetsCompat.Type.statusBars()
            ).top

            view.setPadding(
                initialLeft,
                initialTop + statusBarHeight,
                initialRight,
                initialBottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(rootView)

        databaseHelper = DatabaseHelper(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val ivDetailVehicle = findViewById<ImageView>(R.id.ivDetailVehicle)
        val tvDetailBrand = findViewById<TextView>(R.id.tvDetailBrand)
        val tvDetailModelName = findViewById<TextView>(R.id.tvDetailModelName)
        val tvDetailType = findViewById<TextView>(R.id.tvDetailType)
        val tvDetailTransmission = findViewById<TextView>(R.id.tvDetailTransmission)
        val tvDetailPrice = findViewById<TextView>(R.id.tvDetailPrice)
        val tvDetailDescription = findViewById<TextView>(R.id.tvDetailDescription)

        etRentalDays = findViewById(R.id.etRentalDays)
        tvDetailTotalPrice = findViewById(R.id.tvDetailTotalPrice)
        layoutRentForm = findViewById(R.id.layoutRentForm)
        tvRentedOutNotice = findViewById(R.id.tvRentedOutNotice)
        btnRentVehicle = findViewById(R.id.btnRentVehicle)

        vehicleId = intent.getIntExtra(EXTRA_VEHICLE_ID, -1)
        val imageRes = intent.getIntExtra(EXTRA_VEHICLE_IMAGE, 0)
        val brand = intent.getStringExtra(EXTRA_VEHICLE_BRAND) ?: ""
        val modelName = intent.getStringExtra(EXTRA_VEHICLE_MODEL) ?: ""
        val type = intent.getStringExtra(EXTRA_VEHICLE_TYPE) ?: ""
        val transmission = intent.getStringExtra(EXTRA_VEHICLE_TRANSMISSION) ?: ""
        vehiclePrice = intent.getIntExtra(EXTRA_VEHICLE_PRICE, 0)
        val description = intent.getStringExtra(EXTRA_VEHICLE_DESCRIPTION) ?: ""

        if (vehicleId == -1) {
            Toast.makeText(
                this,
                "Vehicle data not found",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        if (imageRes != 0) {
            ivDetailVehicle.setImageResource(imageRes)
        }

        tvDetailBrand.text = brand
        tvDetailModelName.text = modelName
        tvDetailType.text = type
        tvDetailTransmission.text = transmission
        tvDetailPrice.text = formatRupiah(vehiclePrice)
        tvDetailDescription.text = description

        // Always get the latest rental status from SQLite.
        refreshVehicleRentalStatus()

        etRentalDays.addTextChangedListener {
            updateTotalPrice()
            updateRentButton()
        }

        btnRentVehicle.setOnClickListener {
            rentVehicle()
        }

        updateTotalPrice()
        updateRentButton()
    }

    private fun refreshVehicleRentalStatus() {
        vehicleIsRented = databaseHelper.isVehicleRented(vehicleId)

        if (vehicleIsRented) {
            layoutRentForm.visibility = View.GONE
            tvRentedOutNotice.visibility = View.VISIBLE
        } else {
            layoutRentForm.visibility = View.VISIBLE
            tvRentedOutNotice.visibility = View.GONE
        }

        updateRentButton()
    }

    private fun updateTotalPrice() {
        val duration = etRentalDays.text.toString().trim().toIntOrNull()

        if (duration != null && duration > 0) {
            val total = vehiclePrice.toLong() * duration
            tvDetailTotalPrice.text = "Total: ${formatRupiah(total)}"
        } else {
            tvDetailTotalPrice.text = "Total: Rp0"
        }
    }

    private fun updateRentButton() {
        val duration = etRentalDays.text.toString().trim().toIntOrNull()
        val isValid = duration != null && duration > 0 && !vehicleIsRented

        btnRentVehicle.isEnabled = isValid

        val backgroundColor = if (isValid) {
            R.color.kf_white
        } else {
            R.color.kf_surface_alt
        }

        val textColor = if (isValid) {
            R.color.kf_black
        } else {
            R.color.kf_text_label
        }

        btnRentVehicle.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, backgroundColor)
        )

        btnRentVehicle.setTextColor(
            ContextCompat.getColor(this, textColor)
        )

        btnRentVehicle.alpha = 1f
    }

    private fun rentVehicle() {
        val durationText = etRentalDays.text.toString().trim()

        if (durationText.isEmpty()) {
            etRentalDays.error = "Duration is required"
            return
        }

        val duration = durationText.toIntOrNull()

        if (duration == null || duration <= 0) {
            etRentalDays.error = "Enter a number greater than 0"
            return
        }

        // Get the currently logged-in user.
        val session = SessionManager(this)
        val userId = session.userId

        if (!session.isLoggedIn || userId <= 0) {
            Toast.makeText(
                this,
                "Please login before renting a vehicle",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Check availability again before inserting.
        if (databaseHelper.isVehicleRented(vehicleId)) {
            vehicleIsRented = true
            refreshVehicleRentalStatus()

            Toast.makeText(
                this,
                "This vehicle is already rented out",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val startDate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())

        val inserted = databaseHelper.insertTransaction(
            vehicleId,
            userId,
            duration,
            startDate
        )

        if (inserted) {
            Toast.makeText(
                this,
                "Vehicle rented successfully!",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        } else {
            refreshVehicleRentalStatus()

            Toast.makeText(
                this,
                "Failed to save rental transaction",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun formatRupiah(amount: Number): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp${formatter.format(amount)}"
    }
}
