package com.example.karflex

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// The app targets SDK 37, so it always draws edge-to-edge. These helpers keep
// content clear of the status bar, navigation bar and keyboard.
fun View.applySystemBarsPadding(top: Boolean = false, bottom: Boolean = false) {
    val initialTop = paddingTop
    val initialBottom = paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        )
        view.updatePadding(
            top = initialTop + if (top) bars.top else 0,
            bottom = initialBottom + if (bottom) bars.bottom else 0
        )
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

// For scrolling forms (Login / Register): lifts the view above the navigation bar
// and keyboard with a margin. Unlike padding, this resizes the ScrollView, which
// makes it scroll the focused field into view when the keyboard opens.
fun View.fitAboveKeyboard() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bottom = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        ).bottom
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = bottom }
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

// Dark status bar icons for screens with a white header (Login / Register).
fun Activity.setLightStatusBar(light: Boolean) {
    WindowCompat.getInsetsController(window, window.decorView)
        .isAppearanceLightStatusBars = light
}

private val rupiahFormat =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).apply {
        maximumFractionDigits = 0
    }

fun Int.toRupiah(): String = rupiahFormat.format(this)

private val displayDateFormat =
    DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

// StartDate is stored as yyyy-MM-dd (varchar in the ERD).
fun String.toDisplayDate(): String =
    runCatching { LocalDate.parse(this).format(displayDateFormat) }.getOrDefault(this)
