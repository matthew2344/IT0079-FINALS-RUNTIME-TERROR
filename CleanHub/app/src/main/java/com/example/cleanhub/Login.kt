package com.example.cleanhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener(){
            //LOGON SUCCESS IF USER BEING ENTERED IS SUCCESS
            if(logonSuccess()){
                val intent = Intent(this@Login,HomePage::class.java)
                intent.putExtra("User", etUsername.text.toString())
                Toast.makeText(this,"LOGIN SUCCESSFUL", Toast.LENGTH_LONG).show()
                startActivity(intent)
            } else{
                Toast.makeText(this,"Failed Login Invalid Username or Password", Toast.LENGTH_LONG).show()
            }
        }

        registerBtn.setOnClickListener(){
            //START A NEW REGISTER ACTIVITY FOR CUSTOMERS
            val intent = Intent(this@Login,Register::class.java)
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

    private fun logonSuccess():Boolean{
        //VALIDATES IF THE ENTRY BEING ENTERED EXISTS IN THE DATABASE AND ARE AUTHORIZED FOR LOGIN
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val etUsername = etUsername.text.toString()
        val etPassword = etPassword.text.toString()
        val success = databaseHandler.logonCustomer(etUsername,etPassword)

        return success
    }

}