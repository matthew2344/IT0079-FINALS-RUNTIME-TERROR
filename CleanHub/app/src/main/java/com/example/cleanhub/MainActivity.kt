package com.example.cleanhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener(){
            //START ACTIVITY CUSTOMER LOGIN
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
        }

        startWork.setOnClickListener(){
            //START ACTIVITY WORKER LOGIN
            val intent = Intent(this@MainActivity, LoginForWorkers::class.java)
            startActivity(intent)
        }

    }



}