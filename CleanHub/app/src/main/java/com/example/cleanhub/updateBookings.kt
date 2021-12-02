package com.example.cleanhub

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_update_bookings.*
import kotlinx.android.synthetic.main.update_book.*

class updateBookings : AppCompatActivity() {
    companion object{
        var userString = ""
        var userId = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_bookings)
        userString = intent.getStringExtra("userId").toString()
        userId = userString.toInt()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupListOfDataIntoRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        setupListOfDataIntoRecyclerView() //UPDATES THE SCREEN WHEN RESUMED
    }

    override fun onStart() {
        super.onStart()
        setupListOfDataIntoRecyclerView() //UPDATES THE SCREEN WHEN STARTED
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> this.finish() //RETURNS TO THE PREVIOUS STACK
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getBookItem(): ArrayList<lBookModel>{
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val book = databaseHandler.bookViewCustomer(userId)
        return book
    }

    private fun setupListOfDataIntoRecyclerView(){
        if(getBookItem().size > 0){
            rvBookItem.visibility = View.VISIBLE
            tvNoBookingAvailable.visibility = View.GONE

            rvBookItem.layoutManager = LinearLayoutManager(this)
            val itemAdapter = thirdAdapter(this,getBookItem())
            rvBookItem.adapter = itemAdapter
        } else {
            rvBookItem.visibility = View.GONE
            tvNoBookingAvailable.visibility = View.VISIBLE
        }
    }

    fun bookListDialog(book: lBookModel){
        val bookInfo = Dialog(this,R.style.Theme_Dialog)
        bookInfo.setCancelable(false)
        bookInfo.setContentView(R.layout.update_book)
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        bookInfo.viewAddressText.text = book.bookAddress
        bookInfo.editAddress.setText(book.bookAddress)

        if(book.acceptID == 0){
            bookInfo.goToInvoiceBtn.visibility = View.GONE
        } else {
            bookInfo.goToInvoiceBtn.visibility = View.VISIBLE
        }

        bookInfo.editBookCustomer.setOnClickListener(View.OnClickListener {
            bookInfo.viewBookingAddressLine.visibility = View.GONE
            bookInfo.bookingAddressEditLine.visibility = View.VISIBLE
            bookInfo.saveBookCustomer.visibility = View.VISIBLE
        })

        bookInfo.saveBookCustomer.setOnClickListener(View.OnClickListener {
            val newAddress = bookInfo.editAddress.text.toString()
            if(!newAddress.isEmpty()){
                val status = databaseHandler.updateBooking(book.bookID,newAddress)
                if(status > -1){
                    Toast.makeText(applicationContext, "Update Success", Toast.LENGTH_LONG)
                        .show()
                    bookInfo.dismiss()
                    setupListOfDataIntoRecyclerView()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Entries Above Should Not be Blank",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

        bookInfo.exitBookInfo.setOnClickListener(View.OnClickListener {
            bookInfo.dismiss()
        })

        bookInfo.goToInvoiceBtn.setOnClickListener(View.OnClickListener {
            goToBooking(book.workID,book.worktype,book.custID)
        })

        bookInfo.deleteBookCustomer.setOnClickListener(View.OnClickListener {
            deleteBookDialog(book)
            bookInfo.dismiss()
        })


        bookInfo.show()
    }


    fun goToBooking(workerID:Int,type:String,user:Int) {
        val intent = Intent(this@updateBookings,Booking::class.java)
        intent.putExtra("WorkerID",workerID.toString())
        intent.putExtra("CustID",user.toString())
        intent.putExtra("WorkType",type)
        startActivity(intent)
    }

    fun deleteBookDialog(book: lBookModel){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Booked Service")
        builder.setMessage("Are you sure you want to delete book id: ${book.bookID}?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)


        builder.setPositiveButton("Yes"){dialogInterface, which ->
            databaseHandler.deleteBooking(book.bookID)
            setupListOfDataIntoRecyclerView()
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val deleteDialog: AlertDialog = builder.create()
        deleteDialog.setCancelable(false)
        deleteDialog.show()
    }
}