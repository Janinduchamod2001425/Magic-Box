package com.example.magicboxgame.view_model

import androidx.lifecycle.ViewModel

class MagicBoxViewModel : ViewModel() {
    var bestTimeMillis = Long.MAX_VALUE
    var startTimeMillis = 0L
    var randomValue = 2
}