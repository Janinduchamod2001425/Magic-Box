package com.example.magicboxgame

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Instruction2Activity : AppCompatActivity() {

    private lateinit var buttonClickSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction2)

        buttonClickSound = MediaPlayer.create(this, R.raw.next1)

        val instructionButton = findViewById<Button>(R.id.nextIns3)
        instructionButton.setOnClickListener {
            playButtonClickSound()
            val intent = Intent(this, Instruction3Activity::class.java)
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