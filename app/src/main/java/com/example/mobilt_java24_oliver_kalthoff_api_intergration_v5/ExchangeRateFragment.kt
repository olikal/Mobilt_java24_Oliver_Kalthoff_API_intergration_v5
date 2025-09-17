package com.example.mobilt_java24_oliver_kalthoff_api_intergration_v5

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

// Exchange (sida 2). Använder Frankfurter API. Visar SEK till vald kurs
class ExchangeRateFragment : Fragment(R.layout.fragment_exchange_rate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Backknapp
        view.findViewById<Button>(R.id.btnBack)?.setOnClickListener {
            findNavController().popBackStack()
        }

        // Back to home-knapp. Rensar backstack
        view.findViewById<Button>(R.id.btnBackHome)?.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        // Textview för result
        val tv = view.findViewById<TextView>(R.id.tvExchange)
        tv.text = "Loading..."

        // Valuta man vill se kurs för
        val to = arguments?.getString("to") ?: "EUR"

        // GET request
        val url = "https://api.frankfurter.app/latest?from=SEK&to=$to"
        val queue = Volley.newRequestQueue(requireContext())
        val req = StringRequest(
            Request.Method.GET, url,
            { res ->
                // Parsar kursen och skriver ut
                val rate = JSONObject(res).getJSONObject("rates").getDouble(to)
                tv.text = "SEK → $to: $rate"
            },
            // Enkel feltext
            { err -> tv.text = "Error: ${err.message ?: err.toString()}" }
        )
        queue.add(req)
    }
}