package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var tvBack: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)

        databaseHelper = DatabaseHelper(this)

        etUsername = findViewById(R.id.etRegisterUsername)
        etPassword = findViewById(R.id.etRegisterPassword)
        etPhone = findViewById(R.id.etRegisterPhone)

        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        tvBack = findViewById(R.id.tvBack)

        btnRegister.setOnClickListener {
            registerUser()
        }

        tvLogin.setOnClickListener {
            goToLogin()
        }

        tvBack.setOnClickListener {
            goToLogin()
        }
    }

    private fun registerUser() {

        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString()
        val phone = etPhone.text.toString().trim()

        // =========================
        // VALIDATE ALL FIELDS
        // =========================

        if (
            username.isEmpty() ||
            password.isEmpty() ||
            phone.isEmpty()
        ) {

            Toast.makeText(
                this,
                "All fields must be filled.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =========================
        // VALIDATE USERNAME
        // =========================

        if (databaseHelper.isUsernameExists(username)) {

            Toast.makeText(
                this,
                "Username is already registered.",
                Toast.LENGTH_SHORT
            ).show()

            etUsername.requestFocus()

            return
        }

        // =========================
        // VALIDATE PASSWORD
        // =========================

        if (password.length < 6) {

            Toast.makeText(
                this,
                "Password must be at least 6 characters.",
                Toast.LENGTH_SHORT
            ).show()

            etPassword.requestFocus()

            return
        }

        // =========================
        // VALIDATE PHONE
        // =========================

        if (phone.length < 10) {

            Toast.makeText(
                this,
                "Phone number must be at least 10 digits.",
                Toast.LENGTH_SHORT
            ).show()

            etPhone.requestFocus()

            return
        }

        // =========================
        // INSERT USER
        // =========================

        val success = databaseHelper.registerUser(
            username,
            password,
            phone
        )

        if (success) {

            Toast.makeText(
                this,
                "Registration successful.",
                Toast.LENGTH_SHORT
            ).show()

            goToLogin()

        } else {

            Toast.makeText(
                this,
                "Registration failed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun goToLogin() {

        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)

        finish()
    }
}