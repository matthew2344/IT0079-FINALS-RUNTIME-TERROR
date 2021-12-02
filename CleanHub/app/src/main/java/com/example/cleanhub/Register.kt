package com.example.cleanhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        rgButton.setOnClickListener(){
            newCustomer()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun newCustomer(){
        val user = regUser.text.toString()
        val email = regEmail.text.toString()
        val password = regPassword.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val exist = databaseHandler.validateNewAcc(user)
        val existMail = databaseHandler.validateNewEmail(email)
        if(exist){
            // databaseHandler.validateNewAcc(user) WILL RETURN A BOOLEAN
            // IF TRUE THIS WILL RUN AND TELL THE USER TO CHOOSE DIFFERENT USERNAME
            Toast.makeText(applicationContext, "Username Already Exists", Toast.LENGTH_LONG).show()
            regUser.text.clear()
            regEmail.text.clear()
            regPassword.text.clear()
        } else if(user.length > 16){
            // LIMIT USERNAME LENGTH
            Toast.makeText(applicationContext, "Username too long",Toast.LENGTH_LONG).show()
        }
        else if(!email.isValidEmail()){
            //IF EMAIL IS NOT VALID THIS WILL RUN AND TELL THE USER THAT THE EMAIL IS INVALID
            Toast.makeText(applicationContext, "Email is not Valid",Toast.LENGTH_LONG).show()
        } else if(existMail){
            //databaseHandler.validateNewEmail(email) WILL RETURN A BOOLEAN
            // IF TRUE THIS WILL RUN AND TELL THE USER TO CHOOSE DIFFERENT EMAIL
            Toast.makeText(applicationContext, "Email Already Exists", Toast.LENGTH_LONG).show()
            regUser.text.clear()
            regEmail.text.clear()
            regPassword.text.clear()
        } else {
            //IF THE ABOVE CONDITION IS NOT TRIGGERED THEN THIS WILL RUN
            if (!user.isEmpty() && email.isValidEmail() && !password.isEmpty()) {
                val status =
                    databaseHandler.addCustomer(CustomerModel(0, user, email, password))
                //ADDS USER TO THE DATABASE SQLITE
                if (status > -1) {
                    //IF STATUS RETURNED SUCCESSFUL, SYSTEM WILL TOAST REGISTRATION SUCCESS
                    Toast.makeText(applicationContext, "Registration Success", Toast.LENGTH_LONG)
                        .show()
                    regUser.text.clear()
                    regEmail.text.clear()
                    regPassword.text.clear()
                } else {
                    //IF UNSUCCESSFUL THIS WILL SHOW
                    Toast.makeText(
                        applicationContext,
                        "Entries Above Should Not be Blank",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }


    // CHECKS IF THE EMAIL HAS VALID PATTERN -- EXAMPLE @GMAIL.COM AND IS NOT EMPTY
    // RETURNS A BOOLEAN VALUE
    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

}

