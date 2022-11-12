package com.rangga.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.rangga.workoutapp.databinding.ActivityFinishBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FinishActivity : AppCompatActivity() {
    private var binding: ActivityFinishBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarFinishActivity)
        supportActionBar?.let { supportActionBar?.setDisplayHomeAsUpEnabled(true) }

        binding?.toolbarFinishActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnFinish?.setOnClickListener { finish() }

        val dao = (application as WorkoutApp).db.historyDao()
        addToDatabase(dao)
    }

    private fun addToDatabase(dao: HistoryDao) {
        val c = Calendar.getInstance()

        val datetime = c.time

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = sdf.format(datetime)

        lifecycleScope.launch {
            dao.insert(HistoryEntity(date))
        }
    }
}