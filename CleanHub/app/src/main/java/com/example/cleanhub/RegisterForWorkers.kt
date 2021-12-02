package com.example.cleanhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register_for_workers.*


class RegisterForWorkers : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_for_workers)
        var type: Int = 0

        regBtnWorker.setOnClickListener(){
            newWorker(type)
        }


        radioTypeGroup.setOnCheckedChangeListener{ group, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            if(radio == rdMaintenance){
                type = 1
                //maintenance is chosen
            } else if (radio == rdCleaner){
                type = 2
                //cleaner is chosen
            } else{
                type = 0
                //will be used for if condition and Toast a warning
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun newWorker(type:Int){
        //GET USER INPUTS
        val user = regUserWorker.text.toString()
        val email = regEmailWorker.text.toString()
        val password = regPasswordWorker.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val exist = databaseHandler.validateNewAccWorker(user) //VALIDATION IF USERNAME ALREADY EXISTS
        val existMail = databaseHandler.validateNewEmailWorker(email) //VALIDATION IF EMAIL ALREADY EXISTS
        if(exist){
            //databaseHandler.validateNewAccWorker(user) WILL RETURN A BOOLEAN
                // IF TRUE THIS WILL RUN AND TELL THE USER TO CHOOSE DIFFERENT USERNAME
            Toast.makeText(applicationContext, "Username Already Exists", Toast.LENGTH_LONG).show()
            regUserWorker.text.clear()
            regEmailWorker.text.clear()
            regPasswordWorker.text.clear()
        }else if(user.length > 16){
            //YEAH I DON'T LIKE LONG USERNAME SO...
            Toast.makeText(applicationContext, "Username too long",Toast.LENGTH_LONG).show()
        } else if(!email.isValidEmail()){
            //IF EMAIL IS NOT VALID THIS WILL RUN AND TELL THE USER THAT THE EMAIL IS INVALID
            Toast.makeText(applicationContext, "Email is Invalid", Toast.LENGTH_LONG).show()
        } else if(existMail){
            //databaseHandler.validateNewEmailWorker(email) WILL RETURN A BOOLEAN
                // IF TRUE THIS WILL RUN AND TELL THE USER TO CHOOSE DIFFERENT EMAIL
            Toast.makeText(applicationContext, "Email Already Exists", Toast.LENGTH_LONG).show()
            regUserWorker.text.clear()
            regEmailWorker.text.clear()
            regPasswordWorker.text.clear()
        }else if(type == 0){
            //IF USER DID NOT CHOOSE A WORK TYPE THIS WILL HAPPEN
                // TELLS USER TO CHOOSE A WORK TYPE
            Toast.makeText(applicationContext, "Please Chose Work type", Toast.LENGTH_LONG).show()
            regUserWorker.text.clear()
            regEmailWorker.text.clear()
            regPasswordWorker.text.clear()
        } else {
            //IF THE ABOVE CONDITION IS NOT TRIGGERED THEN THIS WILL RUN
            if (!user.isEmpty() && email.isValidEmail() && !password.isEmpty()) {
                //CHECKS IF ALL ENTRIES ARE NOT EMPTY
                val status =
                    databaseHandler.addWorker(WorkerModel(0,user,email,password,type))
                //ADDS USER TO THE DATABASE SQLITE
                if (status > -1) {
                    //IF STATUS RETURNED SUCCESSFUL, SYSTEM WILL TOAST REGISTRATION SUCCESS
                    Toast.makeText(applicationContext, "Registration Success", Toast.LENGTH_LONG).show()
                    regUserWorker.text.clear()
                    regEmailWorker.text.clear()
                    regPasswordWorker.text.clear()
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