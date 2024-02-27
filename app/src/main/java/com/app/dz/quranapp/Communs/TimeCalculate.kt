package com.app.dz.quranapp.Communs

import com.azan.Azan
import com.azan.Method
import com.azan.Time
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import org.json.JSONArray

class TimeCalculate {

    var value = "hello"

    fun getazan(today :SimpleDate,latitude :Double,longtitude :Double):Azan {
        val location = Location(latitude,longtitude, 1.0, 0)
        val azan = Azan(location,Method.EGYPT_SURVEY)
        //val prayerTimes = azan.getPrayerTimes(today)
        //val imsaak = azan.getImsaak(today)




/*
        val JsonData: JSONArray = response.getJSONArray (name: "data")
        val timings = JsonData.getJSONObject( index: 0)
        val tim = timings.getJSONObject( name: "timings")
        val fajr = tim.getString( name: "Fajr"))
        zuhr?.setText (tim.getString( name: "Dhuhr"))
        Asar?.setText (tim.getString( name: "Asr"))
        Maghrib?.setText(SimpleDateFormat.format(simpleDateFormat.parse(tim.getString( name: "Maghrib")))
        Isha?.setText (simpleDateFormat.format(simpleDateFormat.parse(tim.getString(name: "Isha"))))*/

        return azan

    }

/*
    println("----------------results------------------------")
    println("date ---> " + today.day + " / " + today.month + " / " + today.year)
    println("imsaak ---> $imsaak") println("Fajr ---> " + prayerTimes.fajr())
    println("sunrise --->" + prayerTimes.shuruq())
    println("Zuhr --->" + prayerTimes.thuhr())
    println("Asr --->" + prayerTimes.assr())
    println("Maghrib --->" + prayerTimes.maghrib())
    println("ISHA  --->" + prayerTimes.ishaa())
    println("----------------------------------------")*/


}