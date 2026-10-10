package com.example.karflex

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

// TODO: page content not built yet (search filter + rental history list).
class RentalHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rental_history)

        findViewById<View>(R.id.root).applySystemBarsPadding(top = true, bottom = true)

        findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = getString(R.string.title_rental_history)
            setNavigationOnClickListener { finish() }
        }
    }
}
