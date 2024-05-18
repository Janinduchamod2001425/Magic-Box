package com.example.magicboxgame

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Instruction1Activity : AppCompatActivity() {

    private lateinit var buttonClickSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction1)

        buttonClickSound = MediaPlayer.create(this, R.raw.next2)

        val instructionButton = findViewById<Button>(R.id.nextIns2)
        instructionButton.setOnClickListener {
            playButtonClickSound()
            val intent = Intent(this, Instruction2Activity::class.java)
            startActivity(intent)
            finish()
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