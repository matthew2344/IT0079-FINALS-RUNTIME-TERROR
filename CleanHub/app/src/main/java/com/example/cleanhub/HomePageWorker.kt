package com.example.cleanhub

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home_page_worker.*
import kotlinx.android.synthetic.main.dialog_accept.*
import kotlinx.android.synthetic.main.dialog_password.*
import kotlinx.android.synthetic.main.view_worker_salary.*
import kotlinx.android.synthetic.main.worker_profile.*

class HomePageWorker : AppCompatActivity() {
    companion object{
        var workerUser = ""
        var workerID = 0
        var worker: WorkerModel = WorkerModel(0,"","","",0)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page_worker)
        workerUser = intent.getStringExtra("Worker").toString()
        workerID = getWorkerID(workerUser)
        worker = getWorkerHomePage()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupListofDataIntoRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.profile_menu_worker,menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        setupListofDataIntoRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        setupListofDataIntoRecyclerView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.updateProfileUser -> {
                updateProfile()
                return true
            }
            R.id.viewAcceptedBook -> {
                val intent = Intent(this@HomePageWorker,updateBookingsWorker::class.java)
                startActivity(intent)
                this.onPause()
            }
            R.id.ViewSalary -> {
                earningDialog()
            }
            android.R.id.home -> this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val profile = menu?.findItem(R.id.workerProfile)
        if(worker.worktype == 1) {
            profile?.setIcon(ContextCompat.getDrawable(this, R.drawable.worker))
        } else {
            profile?.setIcon(ContextCompat.getDrawable(this,R.drawable.janitor))
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun getWorkerID(workerName: String = workerUser): Int{
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val workerID = databaseHandler.getWorkerID(workerName)
        return workerID
    }

    private fun getItemsList(): ArrayList<BookedModel> {
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val workbook: ArrayList<BookedModel> = databaseHandler.workerViewBook(getWorkerID())

        return workbook
    }

    private fun setupListofDataIntoRecyclerView() {

        if (getItemsList().size > 0) {

            rvItemsList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            rvItemsList.layoutManager = LinearLayoutManager(this)
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = SecondAdapter(this, getItemsList())
            // adapter instance is set to the recyclerview to inflate the items.
            rvItemsList.adapter = itemAdapter
        } else {

            rvItemsList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    fun acceptBookDialog(bookedModel: BookedModel){
        //DIALOG FOR ACCEPTING BOOKED SERVICES FROM CUSTOMER
        val bookDialog = Dialog(this,R.style.Theme_Dialog)
        bookDialog.setCancelable(false)
        bookDialog.setContentView(R.layout.dialog_accept)

        bookDialog.tvAddress.setText(bookedModel.address)
        bookDialog.tvName.setText(bookedModel.custName)
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        bookDialog.tvAccept.setOnClickListener(View.OnClickListener {
            databaseHandler.acceptBookService(bookedModel)
            setupListofDataIntoRecyclerView()
            bookDialog.dismiss()
        })

        bookDialog.tvReject.setOnClickListener(View.OnClickListener {
            deleteRecordAlertDialog(bookedModel)
            bookDialog.dismiss()
        })

        bookDialog.tvCancel.setOnClickListener(View.OnClickListener {
            bookDialog.dismiss()
        })


        bookDialog.show()
    }

    fun deleteRecordAlertDialog(bookedModel: BookedModel){
        //DELETION WARNING
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")

        builder.setMessage("Reject booking service of ${bookedModel.custName}?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes"){dialogInterface, which ->
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)

            val status = databaseHandler.rejectBookService(bookedModel)
            if(status > -1){
                Toast.makeText(applicationContext, "Reject Successful", Toast.LENGTH_LONG).show()
                setupListofDataIntoRecyclerView()
            }
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun updateProfile(){
        val updateProfile = Dialog(this,R.style.Theme_Dialog)
        updateProfile.setCancelable(false)
        updateProfile.setContentView(R.layout.worker_profile)


        val databaseHandler: DatabaseHandler = DatabaseHandler(this)


        updateProfile.etNewPasswordLine.visibility = View.GONE
        updateProfile.oldPasswordLine.visibility = View.GONE
        updateProfile.btnSave.visibility = View.GONE
        if(worker.worktype == 1){
            updateProfile.workerImageProfile.visibility = View.VISIBLE
            updateProfile.janitorImageProfile.visibility = View.GONE
        } else {
            updateProfile.workerImageProfile.visibility = View.GONE
            updateProfile.janitorImageProfile.visibility = View.VISIBLE
        }

        updateProfile.tvWorkerName.setText(worker.workname)
        updateProfile.tvWorkerEmail.setText(worker.workemail)
        updateProfile.editEntryWorkerName.setText(worker.workname)
        updateProfile.editEntryWorkerEmail.setText(worker.workemail)

        updateProfile.btnEdit.setOnClickListener(View.OnClickListener {
            //CHANGE VIEW TEXT INTO EDIT TEXT FOR UPDATE
            updateProfile.btnSave.visibility = View.VISIBLE

            updateProfile.tvWorkerName.visibility = View.GONE
            updateProfile.tvWorkerEmail.visibility = View.GONE

            updateProfile.etNewPasswordLine.visibility = View.VISIBLE
            updateProfile.editEntryWorkerName.visibility = View.VISIBLE
            updateProfile.editEntryWorkerEmail.visibility = View.VISIBLE
            updateProfile.editEntryWorkerPassword.visibility = View.VISIBLE
            updateProfile.oldPasswordLine.visibility = View.VISIBLE

        })

        updateProfile.btnSave.setOnClickListener(View.OnClickListener {
            val uname = updateProfile.editEntryWorkerName.text.toString()
            val email = updateProfile.editEntryWorkerEmail.text.toString()
            val pass = updateProfile.editEntryWorkerPassword.text.toString()
            val oldPass = updateProfile.editOldPassword.text.toString()
            val exist = databaseHandler.validateNewAccWorker(uname) //FOR NO DUPLICATION OF USERNAME DATA
            val existMail = databaseHandler.validateNewEmailWorker(email) //FOR NO DUPLICATION OF EMAIL DATA

            if(exist && uname != worker.workname){
                //IF USERNAME EXISTS IN WORKER TABLE THEN THIS WILL RUN
                Toast.makeText(applicationContext, "Username Already Exists", Toast.LENGTH_LONG).show()
                updateProfile.editEntryWorkerName.text.clear()
            } else if (existMail && email != worker.workemail){
                //IF EMAIL EXISTS IN WORKER TABLE THEN THIS WILL RUN
                Toast.makeText(applicationContext, "Email Already Exists", Toast.LENGTH_LONG).show()
                updateProfile.editEntryWorkerEmail.text.clear()
            } else if(oldPass != worker.workpassword){
                //PASSWORD SHOULD MATCH ITS CURRENT PASSWORD
                Toast.makeText(applicationContext, "Old Password Invalid", Toast.LENGTH_LONG).show()
                updateProfile.editOldPassword.text.clear()
            } else if(!email.isValidEmail()){
                //INVALID EMAIL NOT ALLOWED
                Toast.makeText(applicationContext, "Invalid Email", Toast.LENGTH_LONG).show()
                updateProfile.editEntryWorkerEmail.text.clear()
            } else {
                if (!uname.isEmpty() && email.isValidEmail() && !pass.isEmpty()) {
                    val status =
                        databaseHandler.updateWorker(worker.workid, uname, email, pass) // UPDATE WORKER INFO
                    if (status > -1) {
                        Toast.makeText(applicationContext, "Update Success", Toast.LENGTH_LONG)
                            .show()
                        updateProfile.dismiss()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Entries Above Should Not be Blank",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        })

        updateProfile.btnExit.setOnClickListener(View.OnClickListener {
            updateProfile.dismiss()
        })

        updateProfile.btnDelete.setOnClickListener(View.OnClickListener {
           passwordDialog(worker)
        })

        updateProfile.show()

    }

    fun deleteWorkerDialog(workerModel: WorkerModel){
        //DELETION WARNING
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Worker")
        builder.setMessage("Are you sure you want to delete ${workerModel.workname}?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)


        builder.setPositiveButton("Yes"){dialogInterface, which ->
            databaseHandler.onDeleteWorker(workerModel.workid)
            finish()
        }

        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val deleteDialog: AlertDialog = builder.create()
        deleteDialog.setCancelable(false)
        deleteDialog.show()
    }

    fun passwordDialog(workerModel: WorkerModel){
        //PASSWORD RECOGNITION FOR DELETION
        val passDialog = Dialog(this,R.style.Theme_Dialog)
        passDialog.setCancelable(false)
        passDialog.setContentView(R.layout.dialog_password)


        passDialog.tvEnterDialog.setOnClickListener(View.OnClickListener {
            val pass = passDialog.etPasswordDialog.text.toString()
            if(workerModel.workpassword == pass){
                deleteWorkerDialog(workerModel)
            } else {
                Toast.makeText(applicationContext,"Password does not match to the orginal",Toast.LENGTH_LONG).show()
                passDialog.etPasswordDialog.text.clear()
            }
        })


        passDialog.tvCancelDialog.setOnClickListener(View.OnClickListener {
            passDialog.dismiss()
        })

        passDialog.show()
    }

    fun getWorkerHomePage():WorkerModel{
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)

        val worker = databaseHandler.getWorker(workerID)
        return worker
    }

    fun earningDialog(){
        //SHOWS TOTAL EARNINGS AND WORK COUNTS
        val eDialog = Dialog(this,R.style.Theme_Dialog)
        eDialog.setCancelable(false)
        eDialog.setContentView(R.layout.view_worker_salary)
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val earnObject = databaseHandler.getTotalEarnings(workerID)
        var tempEarnings = "$${earnObject.totalEarn}"
        eDialog.tvTotalEarnings.text = tempEarnings
        eDialog.tvTotalFinishWorks.text = earnObject.workCount.toString()
        if(earnObject.totalEarn == 0){
            eDialog.lineEarnings.visibility = View.GONE
            eDialog.lineWorkDone.visibility = View.GONE
            eDialog.noWorkDone.visibility = View.VISIBLE
        } else{
            eDialog.lineEarnings.visibility = View.VISIBLE
            eDialog.lineWorkDone.visibility = View.VISIBLE
            eDialog.noWorkDone.visibility = View.GONE
        }

        eDialog.exitEarningView.setOnClickListener(View.OnClickListener {
            eDialog.dismiss()
        })
        eDialog.show()
    }



    // CHECKS IF THE EMAIL HAS VALID PATTERN -- EXAMPLE @GMAIL.COM AND IS NOT EMPTY
    // RETURNS A BOOLEAN VALUE
    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

