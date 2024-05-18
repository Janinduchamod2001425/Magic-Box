package com.example.magicboxgame

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.magicboxgame.view_model.MagicBoxViewModel

class MagicBoxActivity : AppCompatActivity() {

    private lateinit var grid: Array<Array<EditText>>
    private lateinit var submitButton: Button
    private lateinit var timerTextView: TextView
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var success: MediaPlayer
    private lateinit var unsuccess: MediaPlayer
    private lateinit var timeout: MediaPlayer

    // private var bestTimeMillis = Long.MAX_VALUE
    // private var startTimeMillis = 0L

    // use view model to store game related data
    private lateinit var viewModel: MagicBoxViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magic_box)

        viewModel = ViewModelProvider(this).get(MagicBoxViewModel::class.java)

        // Use 3x3 grid
        grid = Array(3) { row ->
            Array(3) { col ->
                findViewById(resources.getIdentifier("et_${row * 3 + col + 1}", "id", packageName))
            }
        }

        // Animate the text inputs
        animateEditTexts()

        // Fill number 2 as a random number in random place
        fillRandomNumber()

        submitButton = findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            validateGrid()
        }

        timerTextView = findViewById(R.id.timer_text_view)

        sharedPreferences = getSharedPreferences("MagicBoxPrefs", Context.MODE_PRIVATE)

        // Load best time from shared preferences
        viewModel.bestTimeMillis = sharedPreferences.getLong("best_time", Long.MAX_VALUE)

        // Initialize and start the countdown timer (2 minutes = 120,000 milliseconds)
        startTimer(60000)
    }

    // Function of input texts
    private fun animateEditTexts() {
        val initialY = -500f
        val finalY = 0f

        for (i in 0 until grid.size) {
            for (j in 0 until grid[i].size) {
                val editText = grid[i][j]
                editText.translationY = initialY
                val interpolator = AccelerateDecelerateInterpolator()
                ObjectAnimator.ofFloat(editText, "translationY", finalY).apply {
                    duration = 500
                    startDelay = (i * grid.size + j) * 100L
                    this.interpolator = interpolator
                    start()
                }
            }
        }
    }

    private fun playButtonClickSoundSuccess() {
        success.seekTo(0)
        success.start()
    }

    private fun playButtonClickSoundUnsuccess() {
        unsuccess.seekTo(0)
        unsuccess.start()
    }

    private fun playButtonClickSoundTimeout() {
        timeout.seekTo(0)
        timeout.start()
    }

    // Random number placing function
    private fun fillRandomNumber() {
        val randomValue = viewModel.randomValue
        val cornerPositions = listOf(0, 2, 6, 8) // Corner positions (1st, 3rd, 7th, 9th)

        // Randomly select a corner position
        val randomPosition = cornerPositions.random()

        // Calculate the row and column based on the selected position in the grid
        val row = randomPosition / 3
        val col = randomPosition % 3

        // Set the random value (2) in the selected corner position
        grid[row][col].setText(randomValue.toString())
    }

    // Start the Count down timer
    private fun startTimer(millisInFuture: Long) {
        viewModel.startTimeMillis = System.currentTimeMillis()

        countDownTimer = object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                val formattedTime = String.format("%02d:%02d", minutes, seconds)

                timerTextView.text = "Time remaining: $formattedTime"

                if(millisUntilFinished <= 10000) {
                    timerTextView.setTextColor(ContextCompat.getColor(this@MagicBoxActivity, R.color.colorTimerWarning))
                } else {
                    timerTextView.setTextColor(ContextCompat.getColor(this@MagicBoxActivity, R.color.colorTimerDefault))
                }
            }

            override fun onFinish() {
                showGameOver()
            }
        }
        countDownTimer.start()
    }

    // Validate function to check the corrections
    private fun validateGrid() {
        val sums = mutableListOf<Int>()

        // Calculate row sums
        for (i in 0 until 3) {
            var sum = 0
            for (j in 0 until 3) {
                val num = grid[i][j].text.toString().toIntOrNull() ?: 0
                sum += num
            }
            sums.add(sum)
        }

        // Calculate column sums
        for (j in 0 until 3) {
            var sum = 0
            for (i in 0 until 3) {
                val num = grid[i][j].text.toString().toIntOrNull() ?: 0
                sum += num
            }
            sums.add(sum)
        }

        // Calculate diagonal sums
        val diag1Sum = (0 until 3).sumOf { grid[it][it].text.toString().toIntOrNull() ?: 0 }
        val diag2Sum = (0 until 3).sumOf { grid[it][2 - it].text.toString().toIntOrNull() ?: 0 }
        sums.add(diag1Sum)
        sums.add(diag2Sum)

        // Check if all sums are 15
        val isValid = sums.all { it == 15 }

        if (isValid) {
            Toast.makeText(this, "Congratulations! You solved the Magic Box!", Toast.LENGTH_SHORT).show()

            // Calculate completion time
            val currentTimeMillis = System.currentTimeMillis()
            val elapsedTimeMillis = currentTimeMillis - viewModel.startTimeMillis

            // Update best time if the current completion time is lower
            if (elapsedTimeMillis < viewModel.bestTimeMillis) {
                viewModel.bestTimeMillis = elapsedTimeMillis
                saveBestTime(viewModel.bestTimeMillis)
            }

            // If player win they navigate to GameOverActivity page with success music
            success = MediaPlayer.create(this, R.raw.finish)
            playButtonClickSoundSuccess()
            val intent = Intent(this, GameOverActivity::class.java)
            intent.putExtra("finishing_time", elapsedTimeMillis)
            startActivity(intent)

        } else {
            Toast.makeText(this, "Oooopz. Try Again!", Toast.LENGTH_SHORT).show()

            // // If player loss they navigate to GameOverActivity page with unsuccess music
            unsuccess = MediaPlayer.create(this, R.raw.gameover)
            playButtonClickSoundUnsuccess()
            val intent = Intent(this, GameOverActivity::class.java)
            startActivity(intent)
        }
    }

    // Use shared preferences to save the best time time to time
    private fun saveBestTime(timeMillis: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong("best_time", timeMillis)
        editor.apply()

        // Convert milliseconds to minutes and seconds
        val minutes = (timeMillis / 1000) / 60
        val seconds = (timeMillis / 1000) % 60

        // Format the time as a readable string (e.g., "2:30" for 2 minutes and 30 seconds)
        val formattedTime = String.format("%d:%02d", minutes, seconds)

        // Display best time in a toast message
        Toast.makeText(this, "New Best Time: $formattedTime", Toast.LENGTH_SHORT).show()
    }

    // Game over function
    private fun showGameOver() {
        Toast.makeText(this, "Time's up! Game over.", Toast.LENGTH_SHORT).show()
        timeout = MediaPlayer.create(this, R.raw.timeout)
        playButtonClickSoundTimeout()
        // Start NextActivity
        val intent = Intent(this, GameOverActivity::class.java)
        startActivity(intent)
    }

    // The onDestroy() function is a lifecycle method of an Activity in Android.
    override fun onDestroy() {
        super.onDestroy()
        // Cancel the countdown timer to avoid memory leaks
        countDownTimer.cancel()
        success.release()
        unsuccess.release()
    }
}
