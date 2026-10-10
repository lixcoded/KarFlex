package com.example.karflex

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Home page: Browse, My Rental and Profile sections switched by bottom navigation.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.fragmentContainer).applySystemBarsPadding(top = true)

        bottomNav = findViewById(R.id.bottomNav)

        // Pad only for the navigation bar, so the bar stays put behind the keyboard
        // instead of riding on top of it.
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            view.updatePadding(
                bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            )
            insets
        }

        bottomNav.setOnItemSelectedListener { item ->
            showSection(item.itemId)
            true
        }

        if (savedInstanceState == null) {
            showSection(R.id.nav_browse)
        }
    }

    fun selectSection(@IdRes itemId: Int) {
        bottomNav.selectedItemId = itemId
    }

    // Keeps each section alive and only shows/hides it, so scroll position and
    // filters survive tab switches.
    private fun showSection(@IdRes itemId: Int) {
        val tag = sectionTag(itemId)
        val transaction = supportFragmentManager.beginTransaction()

        supportFragmentManager.fragments
            .filter { it.tag != tag }
            .forEach { transaction.hide(it) }

        val existing = supportFragmentManager.findFragmentByTag(tag)
        if (existing == null) {
            transaction.add(R.id.fragmentContainer, createSection(itemId), tag)
        } else {
            transaction.show(existing)
        }

        transaction.commit()
    }

    private fun createSection(@IdRes itemId: Int): Fragment = when (itemId) {
        R.id.nav_my_rental -> MyRentalFragment()
        R.id.nav_profile -> ProfileFragment()
        else -> BrowseFragment()
    }

    private fun sectionTag(@IdRes itemId: Int): String = when (itemId) {
        R.id.nav_my_rental -> "my_rental"
        R.id.nav_profile -> "profile"
        else -> "browse"
    }
}
