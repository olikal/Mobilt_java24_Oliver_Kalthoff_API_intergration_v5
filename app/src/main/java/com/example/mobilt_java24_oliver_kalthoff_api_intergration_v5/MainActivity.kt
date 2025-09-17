package com.example.mobilt_java24_oliver_kalthoff_api_intergration_v5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
// Host activity som bara laddar activity_main med navhostfragment. All navigation sker i fragmenten
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}