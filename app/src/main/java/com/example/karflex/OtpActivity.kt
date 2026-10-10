package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

// TODO: OTP form is not built yet (EditText + validate against SessionManager.pendingOtp).
class OtpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        findViewById<View>(R.id.root).applySystemBarsPadding(top = true, bottom = true)

        findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = getString(R.string.title_otp)
            setNavigationOnClickListener { finish() }
        }

        // Temporary shortcut to the Home page until OTP validation exists
        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            openHome()
        }
    }

    private fun openHome() {
        val session = SessionManager(this)
        val username = session.pendingUsername

        if (username == null) {
            Toast.makeText(this, R.string.error_login_first, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = DatabaseHelper(this).getUserId(username)
        session.login(userId, username, session.pendingPhone.orEmpty())

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
