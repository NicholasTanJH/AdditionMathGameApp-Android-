package com.example.maingame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.maingame.databinding.ActivityMainGameBinding
import kotlinx.coroutines.launch
import java.util.*

class MainGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainGameBinding

    private var num1 = 0
    private var num2 = 0
    private var answer: Int = 0

    private val startTimerInMillis: Long = 20000

    private lateinit var stopWatchServiceIntent: Intent

    private lateinit var viewModel: MainViewModel

    private var isSubmitedAnswer: Boolean = false
    private var isAllLifeLost: Boolean = false

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val updatedTime: Long = (intent.getLongExtra(MainTimerService.UPDATED_TIME, 0)) / 1000
            binding.tvTime.text = String.format(Locale.getDefault(), "%02d", updatedTime)
        }
    }

    private val updateTextTimeRunOut: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val timesUp: Boolean = intent.getBooleanExtra(MainTimerService.TIMES_UP, true)
            if (timesUp) {
                loseOnTime() //change question text, resetTimer, Life--
            }
        }
    }

    init {
        collectViewModelData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        //timer
        stopWatchServiceIntent = Intent(this, MainTimerService::class.java)
        stopWatchServiceIntent.putExtra(MainTimerService.MAX_TIME, startTimerInMillis)
        startService(stopWatchServiceIntent) //start timer
        registerReceiver(updateTime, IntentFilter(MainTimerService.UPDATED_TIME))
        registerReceiver(updateTextTimeRunOut, IntentFilter(MainTimerService.TIMES_UP))


        binding.btnNext.setOnClickListener {
            viewModel.newQuestion()
            binding.etAnswer.setText("")
            stopTimer()
            if(!isSubmitedAnswer){
                viewModel.loseLife()
            }
            isSubmitedAnswer = false
            if (!isAllLifeLost) {
                startService(stopWatchServiceIntent)
            } else {
                val endScreenIntent = Intent(this,MainEndScreenActivity::class.java)
                endScreenIntent.putExtra("score",viewModel.score.value)
                startActivity(endScreenIntent)
                finish()
            }
        }

        binding.btnEnter.setOnClickListener {
            if (!isSubmitedAnswer) {
                //get input, check input, configure score and life, display result
                val userAnswer = binding.etAnswer.text.toString()
                if (userAnswer == "") {
                    Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
                } else {
                    binding.tvQuestion.apply {
                        if (userAnswer.toInt() == answer) {
                            text = "Correct!"
                            viewModel.plusScore(true)
                        } else {
                            text = "Wrong! $answer is the answer"
                            viewModel.plusScore(false)
                            viewModel.loseLife()
                        }
                    }
                    isSubmitedAnswer = true
                    stopTimer()
                }
            }
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //num1 question
                launch {
                    viewModel.num1.collect {
                        num1 = it
                        binding.tvQuestion.text =
                            "${viewModel.num1.value} + ${viewModel.num2.value}"
                        answer = num1 + num2
                    }
                }
                launch {
                    viewModel.num2.collect {
                        num2 = it
                        binding.tvQuestion.text =
                            "${viewModel.num1.value} + ${viewModel.num2.value}"
                        answer = num1 + num2
                    }
                }
                //collect
                launch {
                    viewModel.score.collect {
                        binding.tvScore.text = it.toString()
                    }
                }
                //collect life
                launch {
                    viewModel.life.collect {
                        binding.tvLife.text = it.toString()
                        if (it == 0) {
                            isAllLifeLost = true
                        }
                    }
                }
                //collect score
                launch {
                    viewModel.time.collect {
                        binding.tvTime.text = it.toString()
                    }
                }
            }
        }
    }//onCreate

    private fun stopTimer() {
        stopService(stopWatchServiceIntent)
        resetTimer()
    }

    private fun resetTimer() {
        val time = (startTimerInMillis / 1000)
        binding.tvTime.text = String.format("%02d", time)
    }

    fun loseOnTime() {
        stopTimer()
        viewModel.loseLife()
        binding.tvQuestion.text = "Times Up!"
    }
}