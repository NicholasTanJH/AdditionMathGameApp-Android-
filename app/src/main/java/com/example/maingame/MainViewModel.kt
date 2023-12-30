package com.example.maingame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random


class MainViewModel : ViewModel() {
    private val _score = MutableStateFlow<Int>(0)
    val score = _score as StateFlow<Int>
    private val _life = MutableStateFlow<Int>(3)
    val life = _life as StateFlow<Int>
    private val _time = MutableSharedFlow<Int>()
    val time = _time as SharedFlow<Int>

    val randomNum = Random(System.currentTimeMillis())


    private val _num1 = MutableStateFlow<Int>(randomNum.nextInt(0,100))
    val num1 = _num1 as StateFlow<Int>
    private val _num2 = MutableStateFlow<Int>(randomNum.nextInt(0,100))
    val num2 = _num2 as StateFlow<Int>

    fun plusScore(plus: Boolean) {
        if (plus) {
            _score.value += 10
        } else {
            _score.value -= 10
        }
    }

    fun loseLife() {
        _life.value--
    }

    fun startTimer() {
        viewModelScope.launch {
            repeat(60) {
                _time.emit(60 - it)
                delay(1000)
            }
        }
    }

    fun newQuestion() {
        _num1.value = randomNum.nextInt(0,100)
        _num2.value = randomNum.nextInt(0,100)
    }
}
