package com.example.mobilt_java24_oliver_kalthoff_api_intergration_v5

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

// Login (sida 0)
class LoginFragment : Fragment(R.layout.fragment_login) {

    // Hårdkodade credentials
    companion object {
        private const val USER = "abc"
        private const val PASS = "123"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etUser  = view.findViewById<EditText>(R.id.etUser)
        val etPass  = view.findViewById<EditText>(R.id.etPass)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)

        // Kontrollerar inlogg och navigerar
        btnLogin.setOnClickListener {
            val u = etUser.text.toString().trim()
            val p = etPass.text.toString()

            if (u == USER && p == PASS) {
                // Om credentials stämme, logga in och gå vidare till sida 1
                findNavController().navigate(R.id.action_home_to_hub)
            } else {
                // Vid fel, stanna kvar och visa toast med felmeddelande
                Toast.makeText(requireContext(), "Wrong username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}