package com.example.cleanhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login_for_workers.*

class LoginForWorkers : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_for_workers)

        loginButtonWorker.setOnClickListener(){
            //LOGON SUCCESS IF USER BEING ENTERED IS SUCCESS
            if(logonSuccessWorker()){
                val intent = Intent(this@LoginForWorkers,HomePageWorker::class.java)
                intent.putExtra("Worker", etUsernameWorker.text.toString())
                Toast.makeText(this,"LOGIN SUCCESSFUL", Toast.LENGTH_LONG).show()
                startActivity(intent)
            } else {
                Toast.makeText(this,"LOGIN UNSUCCESSFUL", Toast.LENGTH_LONG).show()
            }
        }
        registerBtnWorker.setOnClickListener(){
            //START A NEW REGISTER ACTIVITY FOR WORKERS
            val intent = Intent(this@LoginForWorkers,RegisterForWorkers::class.java)
            Toast.makeText(this, "New Account", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> this.finish() //GOES BACK TO ITS PREVIOUS STACK
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logonSuccessWorker():Boolean{
        //VALIDATES IF THE ENTRY BEING ENTERED EXISTS IN THE DATABASE AND ARE AUTHORIZED FOR LOGIN
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val etUsername = etUsernameWorker.text.toString()
        val etPassword = etPasswordWorker.text.toString()
        val success = databaseHandler.logonWorker(etUsername,etPassword)

        return success
    }

}