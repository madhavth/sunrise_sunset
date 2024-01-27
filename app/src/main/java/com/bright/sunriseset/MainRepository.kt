package com.bright.sunriseset

import android.icu.text.SimpleDateFormat
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

class MainRepository {
    private var cachedResults: JSONObject? = null
    fun fetchSunriseSunsetResults(): JSONObject? {
        if (cachedResults != null) {
            return cachedResults!!
        }

        val apiUrl =
            URL("https://api.sunrise-sunset.org/json?lat=37.7749&lng=-122.4194&formatted=0")
        val urlConnection: HttpURLConnection = apiUrl.openConnection() as HttpURLConnection
        try {
            val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            cachedResults = JSONObject(response.toString())
        } finally {
            urlConnection.disconnect()
        }
        return cachedResults
    }

    // Coroutine function to fetch sunrise or sunset time from the Sunrise-Sunset API
    fun fetchTime(jsonResponse: JSONObject?, type: String,locale:String?=null): LocalDateTime? {
        return try {
            if(jsonResponse==null) return null
            val timeUTC = jsonResponse.getJSONObject("results").getString(type)
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX",
                Locale(locale?: Locale.getDefault().language)
            )
            val dateTime = formatter.parse(timeUTC)
            val formattedTime = LocalDateTime.ofInstant(dateTime.toInstant(), ZoneId.systemDefault())
            formattedTime
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}