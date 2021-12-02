package com.example.cleanhub

import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_booking.*
import kotlinx.android.synthetic.main.dialog_invoice.*

class Booking : AppCompatActivity() {
    companion object{
        var userID = ""
        var workerID = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
         userID = intent.getStringExtra("CustID").toString()
         workerID = intent.getStringExtra("WorkerID").toString()
         val workType = intent.getStringExtra("WorkType").toString()


        if(workType == "Maintenance"){
            WorkerImage.visibility = View.VISIBLE
            JanitorImage.visibility = View.GONE
        } else {
            WorkerImage.visibility = View.GONE
            JanitorImage.visibility = View.VISIBLE
        }
        Toast.makeText(applicationContext, "User ID: $userID", Toast.LENGTH_LONG).show()
        Toast.makeText(applicationContext, "Worker ID: $workerID", Toast.LENGTH_LONG).show()

        validateBook()

        setter(workerID)

        bookBtn.setOnClickListener{
            bookThis(userID,workerID)
        }

        invoiceBtn.setOnClickListener{
            InvoiceDialog()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setter(worker:String){
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val worker: JWorkerModel = databaseHandler.innerJoinWorker(worker)
        val price = "$${worker.workprice}"
        WorkerName.text = worker.workname
        jobTitle.text = worker.worktype
        bookPrice.text = price
    }

    private fun bookThis(user:String, worker: String){
        val address = entryAddress.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val price = bookPrice.text.toString().filter { it.isDigit() || it.isWhitespace() }
        if(!address.isEmpty()){
            val status =
                databaseHandler.bookService(BookingModel(0,price.toInt(),address,user.toInt(),worker.toInt()))
            if(status > -1){
                entryAddress.text.clear()
                validateBook()
                Toast.makeText(applicationContext,"Registration Success", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(
                    applicationContext,
                    "Entries Above Should Not be Blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun validateBook(user: String = userID,worker:String = workerID){
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val result = databaseHandler.alreadyBooked(user.toInt(),worker.toInt())
        val gInvoice = databaseHandler.ifAccepted(user.toInt(),worker.toInt())
        if(result && !gInvoice){
            bookBtn.visibility = View.VISIBLE
            invoiceBtn.visibility = View.GONE
            entryAddress.visibility = View.GONE
            bookBtn.isEnabled = false
            bookBtn.isClickable = false
            bookBtn.setText("ALREADY BOOKED")
        }else if(gInvoice){
            bookBtn.visibility = View.GONE
            entryAddress.visibility = View.GONE
            invoiceBtn.visibility = View.VISIBLE
            invoiceBtn.setBackgroundColor(Color.parseColor("#3f9dff"))
        } else {
            entryAddress.visibility = View.VISIBLE
            bookBtn.visibility = View.VISIBLE
            invoiceBtn.visibility = View.GONE
            bookBtn.isEnabled = true
            bookBtn.isClickable = true
            bookBtn.setBackgroundColor(Color.parseColor("#3f9dff"))
            bookBtn.setText("BOOK NOW")
        }
    }

    fun InvoiceDialog(user:String = userID, worker:String = workerID){
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val workAccept = databaseHandler.getInvoice(user.toInt(),workerID.toInt())
        val invoice = Dialog(this,R.style.Theme_Dialog)
        var textService = ""
        invoice.setCancelable(false)
        invoice.setContentView(R.layout.dialog_invoice)
        if(workAccept.workTitle == "Maintenance"){
            textService = "Repair Service"
            invoice.InvoiceWorker.visibility = View.VISIBLE
            invoice.InvoiceCleaner.visibility = View.GONE
        } else {
            invoice.InvoiceWorker.visibility = View.GONE
            invoice.InvoiceCleaner.visibility = View.VISIBLE
            textService = "Cleaning Service"
        }

        invoice.workerUserName.text = workAccept.workname
        invoice.workType.text = textService
        var price = "$${workAccept.workPrice}"
        invoice.dialogTotalCost.text = price

        invoice.Pay.setOnClickListener(View.OnClickListener {
            databaseHandler.addPayment(FinishModel(workAccept.workPrice,workAccept.workId))
            databaseHandler.invoicePay(workAccept.bookID)
            Toast.makeText(this,"pay",Toast.LENGTH_LONG).show()
            validateBook()
            invoice.dismiss()
        })

        invoice.btnCancelInvoice.setOnClickListener(View.OnClickListener {
            invoice.dismiss()
        })

        invoice.show()
    }




}
