package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        val username = session.username ?: getString(R.string.guest)

        view.findViewById<TextView>(R.id.tvProfileAvatar).text = username.take(1).uppercase()
        view.findViewById<TextView>(R.id.tvProfileUsername).text = username
        view.findViewById<TextView>(R.id.tvProfilePhone).text = session.phone ?: "-"

        view.findViewById<MaterialButton>(R.id.btnRentalHistory).setOnClickListener {
            startActivity(Intent(requireContext(), RentalHistoryActivity::class.java))
        }

        view.findViewById<MaterialButton>(R.id.btnStoreLocation).setOnClickListener {
            startActivity(Intent(requireContext(), LocationActivity::class.java))
        }

        view.findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            logout(session)
        }
    }

    private fun logout(session: SessionManager) {
        session.logout()

        Toast.makeText(
            requireContext(),
            R.string.logout_success,
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
