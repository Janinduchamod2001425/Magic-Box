package com.example.magicboxgame

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class GameOverActivity : AppCompatActivity() {

    private lateinit var buttonClickSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        buttonClickSound = MediaPlayer.create(this, R.raw.go)

        val finishingTime = intent.getLongExtra("finishing_time", 0)

        val finishingTimeTextView = findViewById<TextView>(R.id.finishing_time_text)
        finishingTimeTextView.text = "Finishing Time: ${formatTime(finishingTime)}"

        val replayButton = findViewById<Button>(R.id.replay_button)
        replayButton.setOnClickListener {
            playButtonClickSound()
            // Start MagicBoxActivity again (replay the game)
            val intent = Intent(this, MagicBoxActivity::class.java)
            startActivity(intent)
        }

        val exitButton = findViewById<Button>(R.id.exit_button)
        exitButton.setOnClickListener {
            // Navigate back to the home screen (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finishAffinity() // Close all activities in the stack
        }

        val magicCubeImageView = findViewById<ImageView>(R.id.magic_cube_imageview)

        // Create a rotate animation
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 2000
            repeatCount = Animation.INFINITE
        }

        magicCubeImageView.apply {
            startAnimation(rotateAnimation)

            // Set click listener to stop animation and navigate to MainActivity
            setOnClickListener {
                clearAnimation()
                context.startActivity(Intent(context, MainActivity::class.java)) // Navigate to MainActivity
            }
        }
    }

    private fun formatTime(timeMillis: Long): String {
        val minutes = (timeMillis / 1000) / 60
        val seconds = (timeMillis / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        buttonClickSound.release()
    }

    private fun playButtonClickSound() {
        buttonClickSound.seekTo(0)
        buttonClickSound.start()
    }
}