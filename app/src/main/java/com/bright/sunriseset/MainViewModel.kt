package com.bright.sunriseset

import android.app.Application
import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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

class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository()
    private val _sunsetSunriseStateState = MutableLiveData<SunsetSunriseState>()
    val sunsetSunriseStateState: LiveData<SunsetSunriseState> = _sunsetSunriseStateState

    /**
     * Retrieves a localized time string based on the user's preferred language.
     *
     * @param time The LocalDateTime to be formatted.
     * @param context The application context to access resources and preferences.
     * @return A string representation of the localized time.
     */
    private fun getLocalizedTime(time: LocalDateTime, context: Context): String {
        // Retrieve the user's preferred language from the device settings
        val userPreferredLanguage = Locale.getDefault().language

        // Create a SimpleDateFormat with the user's preferred language
        val sdf = SimpleDateFormat("hh:mm a", Locale(userPreferredLanguage))

        // Format the LocalDateTime into a string using the specified SimpleDateFormat
        return sdf.format(
            time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    fun fetchSunriseTime() {
        // Asynchronously fetch sunrise and sunset times
        viewModelScope.launch(Dispatchers.IO) {
            val sunriseDeferred = async { repository.fetchTime("sunrise") }
            val sunsetDeferred = async { repository.fetchTime("sunset") }

            // Await the results of asynchronous tasks
            val sunriseTime = sunriseDeferred.await()
            val sunsetTime = sunsetDeferred.await()

            // If both sunrise and sunset times are available, localize and display them in Chinese
            if (sunriseTime != null && sunsetTime != null) {
                // Localize sunrise and sunset times
                val localizedSunrise = getLocalizedTime(sunriseTime, application.applicationContext)
                val localizedSunset = getLocalizedTime(sunsetTime, application.applicationContext)

                // Display localized times on TextViews
                _sunsetSunriseStateState.postValue(
                    SunsetSunriseState(
                        localizedSunrise,
                        localizedSunset
                    )
                )
            }
        }
    }
}