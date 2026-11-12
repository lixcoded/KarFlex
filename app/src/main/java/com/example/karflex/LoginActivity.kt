package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        setLightStatusBar(true)
        findViewById<View>(R.id.loginHero).applySystemBarsPadding(top = true)
        findViewById<View>(R.id.loginRoot).fitAboveKeyboard()

        databaseHelper = DatabaseHelper(this)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        btnLogin.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }

    private fun loginUser() {

        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString()

        // Validate username
        if (username.isEmpty()) {
            Toast.makeText(
                this,
                "Username must be filled.",
                Toast.LENGTH_SHORT
            ).show()

            etUsername.requestFocus()
            return
        }

        // Validate password
        if (password.isEmpty()) {
            Toast.makeText(
                this,
                "Password must be filled.",
                Toast.LENGTH_SHORT
            ).show()

            etPassword.requestFocus()
            return
        }

        // Check login
        val isValid = databaseHelper.checkLogin(username, password)

        if (!isValid) {
            Toast.makeText(
                this,
                "Username or password is incorrect.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Get user phone
        val phone = databaseHelper.getUserPhone(username)

        if (phone.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "User phone number could not be found.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Generate random 4-digit OTP
        val otp = Random.nextInt(1000, 10000).toString()

        // Save pending OTP
        val session = SessionManager(this)
        session.savePendingOtp(
            otp = otp,
            username = username,
            phone = phone
        )

        // Show OTP for emulator testing
        Toast.makeText(
            this,
            "SIMULATION OTP: $otp",
            Toast.LENGTH_LONG
        ).show()

        // Open OTP page
        startActivity(
            Intent(this, OtpActivity::class.java)
        )

        finish()
    }
}
