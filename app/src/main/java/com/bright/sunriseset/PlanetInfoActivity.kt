package com.bright.sunriseset

import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bright.sunriseset.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

class PlanetInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    /**
     * Called when the activity is first created. This function initializes the activity, inflates the layout,
     * retrieves the current time, and asynchronously fetches sunrise and sunset times from an API.
     * The fetched times are then dynamically localized and displayed in Chinese on TextViews.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the current time
        val currentTime = LocalDateTime.now()
        bindViews()
        bindObservers()
    }

    private fun bindObservers() {
        viewModel.sunsetSunriseStateState.observe(this) { sunsetSunriseState ->
            updateUI(sunsetSunriseState)
        }

    }

    private fun updateUI(sunsetSunriseState: SunsetSunriseState?) {
        if (sunsetSunriseState == null) return

        binding.textViewSunrise.text =
            "${getString(R.string.SunriseTime)} ${sunsetSunriseState?.sunriseTime}"
        binding.textViewSunset.text =
            "${getString(R.string.SunsetTime)} ${sunsetSunriseState?.sunsetTime}"

    }

    private fun bindViews() {

    }

}