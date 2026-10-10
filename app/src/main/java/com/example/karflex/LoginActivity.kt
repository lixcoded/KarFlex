package com.example.karflex

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    private var pendingUsername = ""
    private var pendingPhone = ""
    private var pendingOtp = ""

    companion object {
        private const val SMS_PERMISSION_REQUEST = 100
    }

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
            val intent = Intent(
                this,
                RegisterActivity::class.java
            )

            startActivity(intent)
        }
    }

    private fun loginUser() {

        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString()

        // =========================
        // VALIDATE USERNAME
        // =========================

        if (username.isEmpty()) {

            Toast.makeText(
                this,
                "Username must be filled.",
                Toast.LENGTH_SHORT
            ).show()

            etUsername.requestFocus()

            return
        }

        // =========================
        // VALIDATE PASSWORD
        // =========================

        if (password.isEmpty()) {

            Toast.makeText(
                this,
                "Password must be filled.",
                Toast.LENGTH_SHORT
            ).show()

            etPassword.requestFocus()

            return
        }

        // =========================
        // CHECK LOGIN
        // =========================

        val isValid = databaseHelper.checkLogin(
            username,
            password
        )

        if (!isValid) {

            Toast.makeText(
                this,
                "Username or password is incorrect.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =========================
        // GET USER PHONE
        // =========================

        val phone = databaseHelper.getUserPhone(username)

        if (phone == null) {

            Toast.makeText(
                this,
                "User phone number could not be found.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =========================
        // GENERATE RANDOM 4-DIGIT OTP
        // =========================

        val otp = Random.nextInt(
            from = 1000,
            until = 10000
        ).toString()

        pendingUsername = username
        pendingPhone = phone
        pendingOtp = otp

        // Save OTP temporarily
        val preferences = getSharedPreferences(
            "KarFlexSession",
            MODE_PRIVATE
        )

        preferences.edit()
            .putString("otp_code", otp)
            .putString("otp_username", username)
            .putString("otp_phone", phone)
            .apply()

        // =========================
        // SEND SMS
        // =========================

        sendOtpSms()
    }

    private fun sendOtpSms() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                SMS_PERMISSION_REQUEST
            )

            return
        }

        try {

            val smsManager = SmsManager.getDefault()

            val message =
                "Your KarFlex OTP code is $pendingOtp. " +
                        "Do not share this code with anyone."

            smsManager.sendTextMessage(
                pendingPhone,
                null,
                message,
                null,
                null
            )

            Toast.makeText(
                this,
                "OTP has been sent to your phone.",
                Toast.LENGTH_SHORT
            ).show()

            openOtpPage()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Failed to send OTP SMS.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == SMS_PERMISSION_REQUEST) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                sendOtpSms()

            } else {

                Toast.makeText(
                    this,
                    "SMS permission is required to receive OTP.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openOtpPage() {

        val intent = Intent(
            this,
            OtpActivity::class.java
        )

        startActivity(intent)

        finish()
    }
}