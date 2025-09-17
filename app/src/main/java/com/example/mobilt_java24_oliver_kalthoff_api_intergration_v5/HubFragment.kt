package com.example.mobilt_java24_oliver_kalthoff_api_intergration_v5

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.net.URLEncoder

// Hub (sida 1) sök stad för att visa väder / välj valuta för valuta-kurs mot SEK
class HubFragment : Fragment(R.layout.fragment_hub) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hitta views
        val etCity = view.findViewById<EditText>(R.id.etCity)
        val sp     = view.findViewById<Spinner>(R.id.spCurrency)
        val btnW   = view.findViewById<Button>(R.id.btnShowWeather)
        val btnR   = view.findViewById<Button>(R.id.btnShowRate)

        // Valutor
        val codes = arrayOf("EUR", "USD", "GBP", "JPY", "NOK", "DKK")
        sp.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            codes
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Visa kurs SEK till valfri
        btnR.setOnClickListener {
            val to = sp.selectedItem.toString()
            val args = Bundle().apply { putString("to", to) }
            findNavController().navigate(R.id.action_hub_to_exchange, args)
        }

        // Visa väder för vald stad. Malmö är default
        btnW.setOnClickListener {
            val city = etCity.text.toString().trim().ifEmpty { "Malmö" }
            fetchCityThenWeather(city)
        }
    }

    // Använder meteos geocoding för lat/long. Väljer forecast för nuvarande timme. Visar i detail
    private fun fetchCityThenWeather(city: String) {
        val queue  = Volley.newRequestQueue(requireContext())
        val geoUrl = "https://geocoding-api.open-meteo.com/v1/search" +
                "?name=${URLEncoder.encode(city, "UTF-8")}&count=1&language=sv&format=json"

        // Geokodning
        val geoReq = StringRequest(Request.Method.GET, geoUrl, { res ->
            val results = JSONObject(res).optJSONArray("results")
            if (results == null || results.length() == 0) {
                Toast.makeText(requireContext(), "Couldn't find city", Toast.LENGTH_SHORT).show()
                return@StringRequest
            }

            val first = results.getJSONObject(0)
            val lat = first.getDouble("latitude")
            val lon = first.getDouble("longitude")
            val niceCity = first.optString("name", city)

            // Forecast temperatur och rain probability
            val weatherUrl = "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=$lat&longitude=$lon" +
                    "&hourly=temperature_2m,precipitation_probability" +
                    "&forecast_days=1&timezone=Europe%2FStockholm"

            // GET-request
            val wReq = StringRequest(Request.Method.GET, weatherUrl, { wRes ->

                // Parsar svaret
                val json   = JSONObject(wRes)
                val hourly = json.getJSONObject("hourly")
                val temps  = hourly.getJSONArray("temperature_2m")
                val probs  = hourly.getJSONArray("precipitation_probability")
                val times  = hourly.getJSONArray("time")

                // Hittar index för nuvarande timme
                val hourFormatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:00", java.util.Locale.US).apply {
                    timeZone = java.util.TimeZone.getTimeZone("Europe/Stockholm")
                }
                val formattedTime = hourFormatter.format(java.util.Date())
                val idx = (0 until times.length()).lastOrNull { times.getString(it) <= formattedTime } ?: 0

                // Plockar ut värden för nu
                val temp = temps.optDouble(idx, Double.NaN)
                val prob = probs.optInt(idx, -1)
                val time = times.optString(idx, "")

                // packar data i bundle
                val args = Bundle().apply {
                    putDouble("temp", temp)
                    putInt("prob",  prob)
                    putString("time", time)
                    putString("city", niceCity)
                }
                findNavController().navigate(R.id.action_hub_to_detail, args)
            }, { err ->
                // Felhantering
                Toast.makeText(requireContext(), "Weather Error: ${err.message}", Toast.LENGTH_SHORT).show()
            })

            queue.add(wReq)
        }, { err ->
            Toast.makeText(requireContext(), "Geocode Error: ${err.message}", Toast.LENGTH_SHORT).show()
        })

        // Kör requests
        queue.add(geoReq)
    }
}