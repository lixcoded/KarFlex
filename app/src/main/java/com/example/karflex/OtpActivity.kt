package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class OtpActivity : AppCompatActivity() {

    private lateinit var otpFields: List<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        findViewById<View>(R.id.root).applySystemBarsPadding(
            top = true,
            bottom = true
        )

        val session = SessionManager(this)

        findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = getString(R.string.title_otp)
            setNavigationOnClickListener {
                finish()
            }
        }

        otpFields = listOf(
            findViewById(R.id.etOtp1),
            findViewById(R.id.etOtp2),
            findViewById(R.id.etOtp3),
            findViewById(R.id.etOtp4)
        )

        findViewById<TextView>(R.id.tvOtpPhone).text =
            session.pendingPhone ?: "Your registered phone number"

        setupOtpInputs()

        findViewById<MaterialButton>(R.id.btnContinue).setOnClickListener {
            verifyOtp()
        }

        otpFields.first().requestFocus()
    }

    private fun setupOtpInputs() {

        otpFields.forEachIndexed { index, editText ->

            editText.addTextChangedListener {
                if (!it.isNullOrEmpty() && index < otpFields.lastIndex) {
                    otpFields[index + 1].requestFocus()
                }
            }

            editText.setOnKeyListener { _, keyCode, event ->
                if (
                    keyCode == KeyEvent.KEYCODE_DEL &&
                    event.action == KeyEvent.ACTION_DOWN &&
                    editText.text.isEmpty() &&
                    index > 0
                ) {
                    otpFields[index - 1].requestFocus()
                    otpFields[index - 1].text.clear()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun verifyOtp() {

        val enteredOtp = otpFields.joinToString("") {
            it.text.toString()
        }

        // Validate empty OTP
        if (enteredOtp.length != 4) {
            Toast.makeText(
                this,
                "Please enter all 4 digits.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val session = SessionManager(this)
        val correctOtp = session.pendingOtp
        val username = session.pendingUsername

        if (username.isNullOrEmpty() || correctOtp.isNullOrEmpty()) {
            Toast.makeText(
                this,
                R.string.error_login_first,
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        // Validate OTP code
        if (enteredOtp != correctOtp) {

            Toast.makeText(
                this,
                "Invalid OTP Code. Please try again.",
                Toast.LENGTH_SHORT
            ).show()

            otpFields.forEach {
                it.text.clear()
            }

            otpFields.first().requestFocus()
            return
        }

        // OTP is correct
        openHome(session, username)
    }

    private fun openHome(
        session: SessionManager,
        username: String
    ) {

        val userId = DatabaseHelper(this).getUserId(username)

        if (userId <= 0) {
            Toast.makeText(
                this,
                R.string.error_login_first,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        session.login(
            userId,
            username,
            session.pendingPhone.orEmpty()
        )

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }
}
