package com.example.maingame

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log

class MainTimerService() : Service() {
    private var startTimerInMillis: Long = 20000
    private val timer = object : CountDownTimer(startTimerInMillis, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            Log.i("LOG", "$millisUntilFinished")
            val intent = Intent(UPDATED_TIME)
            intent.putExtra(UPDATED_TIME, millisUntilFinished)
            sendBroadcast(intent)
        }

        override fun onFinish() {
            val timesUpIntent = Intent(TIMES_UP)
            timesUpIntent.putExtra(TIMES_UP,true)
            sendBroadcast(timesUpIntent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("start Service")
        startTimerInMillis = intent!!.getLongExtra(MAX_TIME, 0)
        timer.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    companion object {
        const val MAX_TIME = "maximum time"
        const val UPDATED_TIME = "updated time"
        const val TIMES_UP = "times up"
    }
}