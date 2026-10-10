package com.example.karflex

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

// TODO: page content not built yet (vehicle info + rent form).
class VehicleDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_VEHICLE_ID = "extra_vehicle_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)

        findViewById<View>(R.id.root).applySystemBarsPadding(top = true, bottom = true)

        findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = getString(R.string.title_vehicle_detail)
            setNavigationOnClickListener { finish() }
        }
    }
}
