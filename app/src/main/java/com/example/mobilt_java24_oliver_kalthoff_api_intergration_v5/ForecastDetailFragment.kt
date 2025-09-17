package com.example.mobilt_java24_oliver_kalthoff_api_intergration_v5

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

// Forecast detail (sida 2).  Visar vädret för vald stad
class ForecastDetailFragment : Fragment(R.layout.fragment_forecast_detail) {

    // Permission för notiser
    private val requestNotifPerm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hämtar argument från Hub
        val temp = arguments?.getDouble("temp", Double.NaN) ?: Double.NaN
        val prob = arguments?.getInt("prob", -1) ?: -1
        val iso  = arguments?.getString("time").orEmpty()
        val city = arguments?.getString("city").orEmpty()

        // Formaterar till HH:mm från iso-string
        val hhmm    = iso.drop(11).take(5).ifEmpty { "--:--" }
        val tempTxt = if (temp.isNaN()) "?" else String.format("%.1f", temp)
        val probTxt = if (prob >= 0) "$prob" else "?"

        // Visar i UI
        view.findViewById<TextView>(R.id.tvDetail)?.text =
            "City: $city\nTime: $hhmm\nTemp: $tempTxt°C\nRain prob: $probTxt%"

        // Notis om rain prob är över 50%
        if (prob >= 50) {
            notifyNow("Weather alert", "High chance of rain in $city ($prob%).")
        }

        // Back-knapp
        view.findViewById<Button>(R.id.btnBack)?.setOnClickListener {
            findNavController().popBackStack()
        }

        // Back to home-knapp. Rensar backstack
        view.findViewById<Button>(R.id.btnBackHome)?.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }


    // Om true så kan vi posta notiser. Annars ber vi om permission.
    private fun askForNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        val granted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (!granted) requestNotifPerm.launch(Manifest.permission.POST_NOTIFICATIONS)
        return granted
    }

    // Bygger och postar notis
    private fun notifyNow(title: String, body: String) {
        if (!askForNotifications()) return

        val channelId = "api_alerts"
        val nm = NotificationManagerCompat.from(requireContext())
        val channel = NotificationChannelCompat.Builder(
            channelId, NotificationManagerCompat.IMPORTANCE_DEFAULT
        ).setName("API Alerts").build()
        nm.createNotificationChannel(channel)

        val n = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()

        nm.notify(1001, n)
    }
}