package com.example.cleanhub

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_update_bookings_worker.*
import kotlinx.android.synthetic.main.view_accepted_book.*

class updateBookingsWorker : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_bookings_worker)

        setupListOfDataIntoRecyclerView()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getBookItem(): ArrayList<LwBookModel>{
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val book = databaseHandler.bookViewWorker(HomePageWorker.workerID)
        return book
    }

    private fun setupListOfDataIntoRecyclerView(){
        if(getBookItem().size > 0){
            rvAcceptedBook.visibility = View.VISIBLE
            tvNoBookWork.visibility = View.GONE

            rvAcceptedBook.layoutManager = LinearLayoutManager(this)
            val itemAdapter = fourthAdapter(this,getBookItem())
            rvAcceptedBook.adapter = itemAdapter
        } else {
            rvAcceptedBook.visibility = View.GONE
            tvNoBookWork.visibility = View.VISIBLE
        }
    }

    fun acceptedBook(lwBookModel: LwBookModel){
        val aBook = Dialog(this,R.style.Theme_Dialog)
        aBook.setCancelable(false)
        aBook.setContentView(R.layout.view_accepted_book)

        aBook.customerName.text = lwBookModel.custName
        aBook.wViewAddressText.text = lwBookModel.bookAddress

        aBook.continueBtn.setOnClickListener(View.OnClickListener {
            aBook.dismiss()
        })

        aBook.withdrawBtn.setOnClickListener(View.OnClickListener {
            deleteAcceptedBook(lwBookModel)
            aBook.dismiss()
            setupListOfDataIntoRecyclerView()
        })

        aBook.wExitViewBook.setOnClickListener(View.OnClickListener {
            aBook.dismiss()
        })

        aBook.show()

    }

    fun deleteAcceptedBook(lwBookModel: LwBookModel){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Withdraw Accepted Book")
        builder.setMessage("Are you sure you want to withdraw accepted book id: ${lwBookModel.bookID}?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)


        builder.setPositiveButton("Yes"){dialogInterface, which ->
            databaseHandler.withdrawAcceptedBooking(lwBookModel.acceptID)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> this.finish()
        }
        return super.onOptionsItemSelected(item)
    }



}