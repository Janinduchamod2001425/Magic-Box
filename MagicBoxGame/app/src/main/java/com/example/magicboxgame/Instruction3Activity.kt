package com.example.magicboxgame

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class Instruction3Activity : AppCompatActivity() {

    private lateinit var buttonClickSound: MediaPlayer
    private lateinit var animator: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction3)

        buttonClickSound = MediaPlayer.create(this, R.raw.go)

        val instructionButton = findViewById<Button>(R.id.nextIns4)
        instructionButton.setOnClickListener {
            playButtonClickSound()
            val intent = Intent(this, MagicBoxActivity::class.java)
            startActivity(intent)
        }

        // Animate the Home button up and down
        val homeBtn = findViewById<ImageView>(R.id.back_to_home)
        animator = ObjectAnimator.ofFloat(homeBtn, "translationY", -20f, 20f).apply {
            duration = 500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        homeBtn.setOnClickListener {
            animator.cancel()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
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