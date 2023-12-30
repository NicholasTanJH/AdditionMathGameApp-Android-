package com.example.maingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.maingame.databinding.ActivityMainEndScreenBinding

class MainEndScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainEndScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainEndScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val finalScore = intent.getIntExtra("score",0)
        binding.tvFinalScore.text = finalScore.toString()

        val restartIntent = Intent(this,MainActivity::class.java)
        binding.btnRestart.setOnClickListener {
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(restartIntent)

        }

        //exit the app
        binding.btnExit.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}