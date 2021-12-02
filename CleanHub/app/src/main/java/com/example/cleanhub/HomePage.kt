package com.example.cleanhub

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.customer_profile.*
import kotlinx.android.synthetic.main.dialog_password.*
import kotlinx.android.synthetic.main.personnel_card.view.*
import kotlinx.android.synthetic.main.worker_profile.*

class HomePage : AppCompatActivity() {
    companion object{
        var username = ""
        var userID = 0
        var customer = CustomerModel(0,"","","")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        username = intent.getStringExtra("User").toString() //GET INTENT FROM PREVIOUS ACTIVITY
        userID = getCustomerID(username) //GET ID USING WHERE CLAUSE QUERY
        customer = getCustomer() //RETURNS CUSTOMER DATA


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupWorkerList()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.profile_menu_customer,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.updateCustomerUser -> {
                customerProfile() //DISPLAY A DIALOG ABOUT CUSTOMER PROFILE
                return true
            }
            R.id.editBookingUser -> {
                val intent = Intent(this@HomePage,updateBookings::class.java)
                intent.putExtra("userId",userID.toString())
                startActivity(intent) //START ANOTHER ACTIVITY SHOWING THE BOOK LIST OF USER
                return true
            }
            android.R.id.home -> this.finish() // FINISH ACTIVITY IN A STACK MANNER
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getWorkItem():ArrayList<JWorkerModel>{
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val workList: ArrayList<JWorkerModel> = databaseHandler.innerJoinWorker() //GOING TO BE USED FOR ITEM ADAPTER
        return workList
    }

    private fun setupWorkerList(){
        //PUTTING DATA TO THE RECYCLER VIEW
        if(getWorkItem().size > 0) {
            workList.visibility = View.VISIBLE
            tvNoWorker.visibility = View.GONE
            val recyclerView = findViewById<RecyclerView>(R.id.workList)
            val layoutManager: LinearLayoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            val itemAdapter = ItemAdapter(this, getWorkItem())
            recyclerView.adapter = itemAdapter
        } else{
            workList.visibility = View.GONE
            tvNoWorker.visibility = View.VISIBLE
        }
    }

    private fun getCustomerID(username:String):Int{
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val custid: Int = databaseHandler.getCustomerID(username) //WE ALREADY KNOW THIS
        return custid
    }

     fun goToBooking(workerID:String,type:String,user:Int = userID){
        val intent = Intent(this@HomePage,Booking::class.java)
        intent.putExtra("WorkerID",workerID)
        intent.putExtra("CustID",user.toString())
        intent.putExtra("WorkType",type)
         //PUT EXTRA INTENT REQUIRED FOR NEXT ACTIVITY
        startActivity(intent)
    }

    fun customerProfile(){
        val customerDialog = Dialog(this,R.style.Theme_Dialog)
        customerDialog.setCancelable(false)
        customerDialog.setContentView(R.layout.customer_profile)
        // MANY LINES OF CODE
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)

        //JUST MAKING SURE THE VISIBILITY OF PASSWORD LINES AND SAVE BTN IS GONE
        customerDialog.etNewPasswordLineCustomer.visibility = View.GONE
        customerDialog.oldPasswordLineCustomer.visibility = View.GONE
        customerDialog.btnSaveCustomer.visibility = View.GONE

        //SET FETCH DATA TO THE TEXT INSIDE THE DIALOG
        customerDialog.tvCustomerName.setText(customer.name)
        customerDialog.tvCustomerEmail.setText(customer.Email)
        customerDialog.editEntryCustomerName.setText(customer.name)
        customerDialog.editEntryCustomerEmail.setText(customer.Email)

        customerDialog.btnEditCustomer.setOnClickListener(View.OnClickListener {
            //PULLS AN UPDATE VIEW OF THE DIALOG
            customerDialog.btnSaveCustomer.visibility = View.VISIBLE
            //REMOVE TEXT VIEW OR UNEDITABLE TEXT OUT OF THE WAY
            customerDialog.tvCustomerName.visibility = View.GONE
            customerDialog.tvCustomerEmail.visibility = View.GONE
            //PUTS ALL THE EDITABLE TEXT IN THE VIEW
            customerDialog.etNewPasswordLineCustomer.visibility = View.VISIBLE
            customerDialog.oldPasswordLineCustomer.visibility = View.VISIBLE
            customerDialog.editEntryCustomerName.visibility = View.VISIBLE
            customerDialog.editEntryCustomerEmail.visibility = View.VISIBLE

        })

        customerDialog.btnSaveCustomer.setOnClickListener(View.OnClickListener {

            val uname = customerDialog.editEntryCustomerName.text.toString()
            val email = customerDialog.editEntryCustomerEmail.text.toString()
            val pass = customerDialog.editEntryCustomerPassword.text.toString()
            val oldPass = customerDialog.editOldCustomerPassword.text.toString()
            val exist = databaseHandler.validateNewAcc(uname) // AGAIN THIS IS TO MAKE SURE NO DUPLICATION OF USERNAME
            val existMail = databaseHandler.validateNewEmail(email) // NO DUPLICATION OF EMAIL

            if(exist && uname != customer.name){
                //THIS WILL RUN IF USERNAME IS ALREADY EXISTED IN CUSTOMER DATA
                Toast.makeText(applicationContext, "Username Already Exists", Toast.LENGTH_LONG).show()
                customerDialog.editEntryCustomerName.text.clear()
            } else if (existMail && email != customer.Email){
                //THIS WILL RUN IF EMAIL IS ALREADY EXISTED IN CUSTOMER DATA
                Toast.makeText(applicationContext, "Email Already Exists", Toast.LENGTH_LONG).show()
                customerDialog.editEntryCustomerEmail.text.clear()
            } else if (oldPass != customer.password){
                //OLD PASSWORD SHOULD BE CORRECT
                Toast.makeText(applicationContext, "Old Password Invalid", Toast.LENGTH_LONG).show()
                customerDialog.editOldPassword.text.clear()
            } else if(!email.isValidEmail()){
                //IF EMAIL IS INVALID THIS WILL RUN
                Toast.makeText(applicationContext, "Invalid Email", Toast.LENGTH_LONG).show()
                customerDialog.editEntryCustomerEmail.text.clear()
            } else {
                if(!uname.isEmpty() && email.isValidEmail() && !pass.isEmpty()){
                    val status =
                        databaseHandler.updateCustomer(customer.id,uname,email,pass) //UPDATES CUSTOMER DATA
                    if(status > -1){
                        //MAKES A TOAST IF SUCCESS
                        Toast.makeText(applicationContext, "Update Success", Toast.LENGTH_LONG)
                            .show()
                        customerDialog.dismiss()
                    } else {
                        //IF NOT THIS WILL SHOW
                        Toast.makeText(
                            applicationContext,
                            "Entries Above Should Not be Blank",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })


        customerDialog.btnDeleteCustomer.setOnClickListener(View.OnClickListener {
            passwordDialog(customer)
        })

        customerDialog.btnExitCustomer.setOnClickListener(View.OnClickListener {
            customerDialog.dismiss()
        })

        customerDialog.show()
    }

    fun deleteCustomerDialog(customerModel: CustomerModel){
        //SIMPLE WARNING IF THE USER REALLY WANTS TO DELETE ITS ACCOUNT
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Customer")
        builder.setMessage("Are you sure you want to delete ${customerModel.name}?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)


        builder.setPositiveButton("Yes"){dialogInterface, which ->
            databaseHandler.onDeleteCustomer(customerModel.id)
            finish()
        }

        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val deleteDialog: AlertDialog = builder.create()
        deleteDialog.setCancelable(false)
        deleteDialog.show()
    }

    fun passwordDialog(customerModel: CustomerModel){
        //NEEDS PASSWORD RECOGNITION FOR DELETION
        val passDialog = Dialog(this,R.style.Theme_Dialog)
        passDialog.setCancelable(false)
        passDialog.setContentView(R.layout.dialog_password)


        passDialog.tvEnterDialog.setOnClickListener(View.OnClickListener {
            val pass = passDialog.etPasswordDialog.text.toString()
            if(customerModel.password == pass){
                deleteCustomerDialog(customerModel)
            } else {
                Toast.makeText(applicationContext,"Password does not match to the original",Toast.LENGTH_LONG).show()
                passDialog.etPasswordDialog.text.clear()
            }
        })


        passDialog.tvCancelDialog.setOnClickListener(View.OnClickListener {
            passDialog.dismiss()
        })

        passDialog.show()
    }

    fun getCustomer():CustomerModel{
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val customer = databaseHandler.getCustomer(userID)
        return customer
    }




    // CHECKS IF THE EMAIL HAS VALID PATTERN -- EXAMPLE @GMAIL.COM AND IS NOT EMPTY
    // RETURNS A BOOLEAN VALUE
    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

}