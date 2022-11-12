package com.rangga.workoutapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rangga.workoutapp.databinding.ActivityExerciseBinding
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    private var exerciseTime: CountDownTimer? = null
    private var exerciseProgress = 0

    private var exercicseTimerDuration: Long = 2

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var binding: ActivityExerciseBinding? = null
    private var tts: TextToSpeech? = null

    private var player: MediaPlayer? = null

    private var exerciseAdapater: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        tts = TextToSpeech(this, this)

        exerciseList = Constants.defaultExerciseList()

        setRestView()

        setupExerciseRV()
    }

    private fun setupExerciseRV() {
        binding?.rvExercise?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapater = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExercise?.adapter = exerciseAdapater
    }

    private fun setRestView() {
        try {
            val soundURI =
                Uri.parse("android.resource://com.rangga.workoutapp/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.rvExercise?.visibility = View.INVISIBLE
        binding?.flExercise?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE

        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        setRestProgressBar()
    }

    private fun setRestProgressBar() {
        binding?.progressBar?.progress = restProgress

        restTimer = object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].isSelected = true
                exerciseAdapater?.notifyDataSetChanged()
                setUpExerciseView()
            }

        }.start()
    }

    private fun setUpExerciseView() {
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.rvExercise?.visibility = View.VISIBLE
        binding?.flExercise?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE

        if (exerciseTime != null) {
            exerciseTime?.cancel()
            exerciseProgress = 0
        }
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].image)
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].name
        setExerciseProgressBar()
    }

    private fun setExerciseProgressBar() {
        binding?.progressBarExercise?.progress = exerciseProgress
        exerciseTime = object : CountDownTimer(exercicseTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress =
                    exercicseTimerDuration.toInt() - exerciseProgress
                binding?.tvTimerExercise?.text =
                    (exercicseTimerDuration.toInt() - exerciseProgress).toString()
            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExercisePosition].isSelected = false
                    exerciseList!![currentExercisePosition].isCompleted = true
                    exerciseAdapater?.notifyDataSetChanged()
                    setRestView()
                }else{
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }

        }.start()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "TTS NOT SUPPORTED")
            }
        } else {
            Log.e("TTS FAIL", "TTS FAILED")
        }
    }
}