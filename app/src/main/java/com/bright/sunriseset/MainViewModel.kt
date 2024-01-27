package com.bright.sunriseset

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale


class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    val locales: List<String> = listOf(Locale.CHINESE.language, Locale.ENGLISH.language)
    private val repository = MainRepository()
    private val _sunsetSunriseStateState = MutableLiveData<SunsetSunriseState>()
    val sunsetSunriseStateState: LiveData<SunsetSunriseState> = _sunsetSunriseStateState

    private val _selectedLocale = MutableLiveData<String>(locales[0])
    val selectedLocale: LiveData<String> = _selectedLocale

    /**
     * Retrieves a localized time string based on the user's preferred language.
     *
     * @param time The LocalDateTime to be formatted.
     * @param context The application context to access resources and preferences.
     * @return A string representation of the localized time.
     */
    private fun getLocalizedTime(
        time: LocalDateTime,
        context: Context,
        locale: String? = null
    ): String {
        // Retrieve the user's preferred language from the device settings
        val userPreferredLanguage = locale ?: Locale.getDefault().language

        // Create a SimpleDateFormat with the user's preferred language
        val sdf = SimpleDateFormat("hh:mm a", Locale(userPreferredLanguage))

        // Format the LocalDateTime into a string using the specified SimpleDateFormat
        return sdf.format(
            time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    fun fetchSunriseTime(locale: String?) {
        // Asynchronously fetch sunrise and sunset times
        viewModelScope.launch(Dispatchers.IO) {
            val apiResponse = repository.fetchSunriseSunsetResults()

            // parse the fetched time
            val sunriseTime = repository.fetchTime(apiResponse, "sunrise", locale)
            val sunsetTime = repository.fetchTime(apiResponse, "sunset", locale)

            // If both sunrise and sunset times are available, localize and display them in Chinese
            if (sunriseTime != null && sunsetTime != null) {
                // Localize sunrise and sunset times
                val localizedSunrise =
                    getLocalizedTime(sunriseTime, application.applicationContext,locale)
                val localizedSunset = getLocalizedTime(sunsetTime, application.applicationContext,locale)

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

    fun onLocaleChanged(locale: String= locales[0]) {
        _selectedLocale.postValue(locale)
    }

}