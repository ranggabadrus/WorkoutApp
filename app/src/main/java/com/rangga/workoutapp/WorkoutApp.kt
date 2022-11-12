package com.rangga.workoutapp

import android.app.Application
// create the application class
class WorkoutApp: Application() {

    val db:HistoryDatabase by lazy {
        HistoryDatabase.getInstance(this)
    }
}