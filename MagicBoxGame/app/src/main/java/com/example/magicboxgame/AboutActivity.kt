package com.example.magicboxgame

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class AboutActivity : AppCompatActivity() {

    private lateinit var buttonClickSound: MediaPlayer
    private lateinit var linkedInImageView: ImageView
    private lateinit var githubImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        buttonClickSound = MediaPlayer.create(this, R.raw.next1)

        val backButton = findViewById<Button>(R.id.back_to_home_button)
        backButton.setOnClickListener {
            playButtonClickSound()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        linkedInImageView = findViewById(R.id.linkedln)

        linkedInImageView.setOnClickListener {
            val linkedInUrl = "https://www.linkedin.com/in/janinduchamod" // Replace with your actual LinkedIn URL
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(linkedInUrl))
            startActivity(intent)
        }

        githubImageView = findViewById(R.id.github)

        githubImageView.setOnClickListener {
            val githubUrl = "https://github.com/Janinduchamod2001425" // Replace with your actual GitHub username
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(githubUrl))
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