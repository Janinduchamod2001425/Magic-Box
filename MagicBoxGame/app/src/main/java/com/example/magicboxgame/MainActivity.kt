package com.example.magicboxgame

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var highScoreTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var buttonClickSound: MediaPlayer

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonClickSound = MediaPlayer.create(this, R.raw.button)
        startButton = findViewById(R.id.start_button)

        startButton.setOnClickListener {
            playButtonClickSound()
            startGame()
        }

        // Use Shared preferences to get the Best time of the player
        sharedPreferences = getSharedPreferences("MagicBoxPrefs", Context.MODE_PRIVATE)
        val highScoreMillis = sharedPreferences.getLong("best_time", Long.MAX_VALUE)
        highScoreTextView = findViewById(R.id.high_score_text_view)

        // Convert best time from milliseconds to readable format (seconds or minutes)
        if (highScoreMillis < Long.MAX_VALUE) {
            val timeUnit = getTimeUnit(highScoreMillis)
            val timeValue = convertTime(highScoreMillis, timeUnit)
            val formattedTime = String.format("Best Time: %d %s", timeValue, timeUnit)
            highScoreTextView.text = formattedTime
            highScoreTextView.visibility = View.VISIBLE
        } else {
            highScoreTextView.visibility = View.GONE
        }

        // Rotate Magic Box
        val magicCubeImage = findViewById<ImageView>(R.id.magicBoxImage)

        // Create a rotate animation
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 2000
            repeatCount = Animation.INFINITE
        }

        // Start the magic box animation
        magicCubeImage.startAnimation(rotateAnimation)

        // Initialize MediaPlayer with background music
        mediaPlayer = MediaPlayer.create(this, R.raw.start)
        mediaPlayer.isLooping = true // Loop the background music

        // Start playing background music
        mediaPlayer.start()

        val aboutButton = findViewById<Button>(R.id.about)
        aboutButton.setOnClickListener {
            playButtonClickSound()
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        // Stop and release MediaPlayer when activity is stopped
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    private fun playButtonClickSound() {
        // Reset the MediaPlayer to start playback from the beginning each time
        buttonClickSound.seekTo(0)
        buttonClickSound.start()
    }

    private fun startGame() {
        val intent = Intent(this, InstructionsActivity::class.java)
        startActivity(intent)
    }

    private fun getTimeUnit(timeMillis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis)
        return if (minutes > 0) "minutes" else "seconds"
    }

    private fun convertTime(timeMillis: Long, timeUnit: String): Long {
        return when (timeUnit) {
            "minutes" -> TimeUnit.MILLISECONDS.toMinutes(timeMillis)
            else -> TimeUnit.MILLISECONDS.toSeconds(timeMillis)
        }
    }
}
