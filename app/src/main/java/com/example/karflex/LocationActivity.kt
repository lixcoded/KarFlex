package com.example.karflex

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

// TODO: page content not built yet (Google Maps with the KarFlex marker).
class LocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        findViewById<View>(R.id.root).applySystemBarsPadding(top = true, bottom = true)

        findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = getString(R.string.title_store_location)
            setNavigationOnClickListener { finish() }
        }
    }
}
