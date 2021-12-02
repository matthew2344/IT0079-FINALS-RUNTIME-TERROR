package com.example.cleanhub

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context):
        SQLiteOpenHelper(context, DATABASE_NAME, null ,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "CLEANHUB"


        //DATA ENTITY CUSTOMER
        private val TABLE_CUST = "customer"
        private val CUST_ID = "ID"
        private val CUST_NAME = "username"
        private val CUST_EMAIL = "email"
        private val CUST_PASS = "password"
        //-------------------------------------

        //DATA ENTITY WORKER
        private val TABLE_WORKER = "worker"
        private val WORK_ID = "workID"
        private val WORK_NAME = "workName"
        private val WORK_EMAIL = "workEmail"
        private val WORK_PASS = "workPass"
        private val WORK_TYPE = "titleID"


        //DATA ENTITY BOOK
        private val TABLE_BOOK = "booking"
        private val BOOK_ID = "bookingID"
        private val BOOK_PAY = "bookingPrice"
        private val BOOK_ADDRESS = "bookingAddress"
        private val BOOK_CUST_ID = "ID"
        private val BOOK_WORK_ID = "workID"


        //DATA ENTITY ACCEPT
        private val TABLE_ACCEPT = "accept"
        private val ACCEPT_ID = "acceptID"
        private val ACCEPTED_BOOK = "bookingID"


        //DATA ENTITY FINISHED WORK
        private val TABLE_FINISH = "finish"
        private val FINISH_ID = "finishID"
        private val FINISH_BAYAD = "bayad"
        private val FIN_WORKER_ID = "workID"



        //DATA ENTITY JOB TITLE
        private val TABLE_TITLE = "jobTitle"
        private val TITLE_ID = "titleID"
        private val TITLE_NAME = "titleName"
        private val TITLE_PRICE = "titlePrice"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        //CREATION OF TABLE CUSTOMER
        val CREATE_CUSTOMER = ("CREATE TABLE $TABLE_CUST (" +
                "$CUST_ID INTEGER PRIMARY KEY, $CUST_NAME TEXT," +
                "$CUST_EMAIL TEXT, $CUST_PASS TEXT)")
        //CREATION OF TABLE TITLE
        val CREATE_TITLE = ("CREATE TABLE $TABLE_TITLE (" +
                "$TITLE_ID INTEGER PRIMARY KEY, $TITLE_NAME TEXT," +
                "$TITLE_PRICE INTEGER)")
        //CREATION OF TABLE WORKER
        val CREATE_WORKER = ("CREATE TABLE $TABLE_WORKER (" +
                "$WORK_ID INTEGER PRIMARY KEY, $WORK_NAME TEXT," +
                "$WORK_EMAIL TEXT, $WORK_PASS TEXT, $WORK_TYPE INTEGER," +
                "FOREIGN KEY ($WORK_TYPE) REFERENCES $TABLE_TITLE ($TITLE_ID))")
        //CREATION OF TABLE BOOK
        val CREATE_BOOK = ("CREATE TABLE $TABLE_BOOK (" +
                "$BOOK_ID INTEGER PRIMARY KEY,$BOOK_PAY INTEGER," +
                "$BOOK_ADDRESS TEXT, $BOOK_CUST_ID INTEGER,$BOOK_WORK_ID INTEGER," +
                "FOREIGN KEY ($BOOK_CUST_ID) REFERENCES $TABLE_CUST ($CUST_ID)," +
                "FOREIGN KEY ($BOOK_WORK_ID) REFERENCES $TABLE_WORKER ($WORK_ID))")
        //CREATION OF TABLE FINISH
        val CREATE_FINISH = ("CREATE TABLE $TABLE_FINISH (" +
                "$FINISH_ID INTEGER PRIMARY KEY, $FINISH_BAYAD INTEGER," +
                "$FIN_WORKER_ID INTEGER, FOREIGN KEY ($FIN_WORKER_ID) REFERENCES " +
                "$TABLE_WORKER ($WORK_ID))")
        //CREATION OF TABLE ACCEPT
        val CREATE_ACCEPT = ("CREATE TABLE $TABLE_ACCEPT (" +
                "$ACCEPT_ID INTEGER PRIMARY KEY, $ACCEPTED_BOOK INTEGER," +
                "FOREIGN KEY ($ACCEPTED_BOOK) REFERENCES $TABLE_BOOK ($BOOK_ID))")

        // PRE-POPULATE TITLES MAINTENANCE
        val PRE_POPULATE_TITLE_MAINTENANCE = ("INSERT INTO $TABLE_TITLE ($TITLE_ID,$TITLE_NAME,$TITLE_PRICE) " +
                "VALUES (1,'Maintenance',12); ")
        // PRE-POPULATE TITLE CLEANER DATA
        val PRE_POPULATE_TITLE_CLEANER = ("INSERT INTO $TABLE_TITLE ($TITLE_ID,$TITLE_NAME,$TITLE_PRICE) " +
                "VALUES (2,'Cleaner',10); ")

        db?.execSQL(CREATE_CUSTOMER)
        db?.execSQL(CREATE_TITLE)
        db?.execSQL(CREATE_BOOK)
        db?.execSQL(CREATE_ACCEPT)
        db?.execSQL(CREATE_WORKER)
        db?.execSQL(CREATE_FINISH)
        db?.execSQL(PRE_POPULATE_TITLE_MAINTENANCE)
        db?.execSQL(PRE_POPULATE_TITLE_CLEANER)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //DROPS EXISTING TABLE WHEN MIGRATING TO A NEWER VERSION OF DATABASE
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CUST")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_TITLE")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_WORKER")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_ACCEPT")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_BOOK")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_FINISH")
        onCreate(db)
    }


    fun viewWorker():ArrayList<WorkerModel>{
        //GETS WORKER LIST IN ARRAY
        val workList: ArrayList<WorkerModel> = ArrayList<WorkerModel>()

        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_WORKER"
        var cursor:Cursor? = null

        try{
            cursor = db.rawQuery(selectQuery,null)
        } catch(e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var workid: Int
        var workName: String
        var workEmail: String
        var workPassword: String
        var type: Int

        if (cursor.moveToFirst()) {
            do {
                workid = cursor.getInt(cursor.getColumnIndex(WORK_ID))
                workName = cursor.getString(cursor.getColumnIndex(WORK_NAME))
                workEmail = cursor.getString(cursor.getColumnIndex(WORK_EMAIL))
                workPassword = cursor.getString(cursor.getColumnIndex(WORK_PASS))
                type = cursor.getInt(cursor.getColumnIndex(WORK_TYPE))

                val worker = WorkerModel(workid = workid, workname = workName, workemail = workEmail, workpassword = workPassword, worktype = type)
                workList.add(worker)
            } while (cursor.moveToNext())
        }

        return workList
    }

    fun bookService(bookingModel: BookingModel):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(BOOK_PAY, bookingModel.bookPay)
        contentValues.put(BOOK_ADDRESS, bookingModel.bookAddress)
        contentValues.put(BOOK_CUST_ID, bookingModel.custID)
        contentValues.put(BOOK_WORK_ID, bookingModel.workID)
        //INSERT A NEW BOOK SERVICE
        val success = db.insert(TABLE_BOOK,null,contentValues)
        db.close()
        return success
    }


    fun addWorker(workerModel: WorkerModel):Long{
        val db = this.writableDatabase
        val contentValues =  ContentValues()
        contentValues.put(WORK_NAME, workerModel.workname)
        contentValues.put(WORK_EMAIL, workerModel.workemail)
        contentValues.put(WORK_TYPE, workerModel.worktype)
        contentValues.put(WORK_PASS, workerModel.workpassword)
        //INSERT NEW WORKER
        val success = db.insert(TABLE_WORKER,null, contentValues)

        db.close()
        return success
    }


    fun addCustomer(customerModel: CustomerModel):Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CUST_NAME, customerModel.name)
        contentValues.put(CUST_EMAIL, customerModel.Email)
        contentValues.put(CUST_PASS, customerModel.password)
        // INSERT NEW CUSTOMER
        val success = db.insert(TABLE_CUST, null, contentValues)

        db.close()
        return success
    }


    fun logonCustomer(customerName: String, customerPass: String): Boolean{
        //FOR LOGIN VALIDATION
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_CUST WHERE $CUST_NAME = ? AND $CUST_PASS = ?"
        val selectionArgs = arrayOf(customerName, customerPass)
        var cursor: Cursor? = null

        cursor = db.rawQuery(selectQuery,selectionArgs)
        val count = cursor.getCount()
        cursor.close()
        db.close()

        if (count >= 1) {
            return true
        }

        return false
    }

    fun getCustomerID(customerName:String):Int{
        //GETS WORKER ID
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_CUST WHERE $CUST_NAME = ?"
        val selectionArgs = arrayOf(customerName)
        var cursor: Cursor? = null
        cursor = db.rawQuery(selectQuery,selectionArgs)
        var custID = 0
        if(cursor.moveToFirst()){
            custID = cursor.getInt(cursor.getColumnIndex(CUST_ID))
        }

        return custID
    }

    fun getWorkerID(workerName: String):Int{
        //GETS WORKER ID
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_WORKER WHERE $WORK_NAME = ?"
        val selectionArgs = arrayOf(workerName)
        var cursor: Cursor? = null
        cursor = db.rawQuery(selectQuery,selectionArgs)
        var workID = 0

        if(cursor.moveToFirst()){
            workID = cursor.getInt(cursor.getColumnIndex(WORK_ID))
        }

        return workID
    }

    fun logonWorker(workerName:String,workerPass:String):Boolean{
        //FOR LOGIN VALIDATION
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_WORKER WHERE $WORK_NAME = ? AND $WORK_PASS = ?"
        val selectionArgs = arrayOf(workerName, workerPass)
        var cursor: Cursor? = null


        cursor = db.rawQuery(selectQuery,selectionArgs)
        val count = cursor.getCount()
        cursor.close()
        db.close()

        if (count >= 1) {
            return true
        }

        return false
    }


    fun validateNewEmail(email: String): Boolean{
        //VALIDATE IF THE EMAIL ALREADY EXISTS
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_CUST WHERE $CUST_EMAIL = ?"
        var cursor: Cursor? = null
        cursor = db.rawQuery(selectQuery, arrayOf(email))

        val count =  cursor.getCount()
        cursor.close()
        db.close()

        if (count > 0) {
            return true
        }

        return false
    }

    fun validateNewAcc(customerName: String): Boolean{
        //VALIDATE IF THE USERNAME ALREADY EXISTS
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_CUST WHERE $CUST_NAME = ?"
        var cursor: Cursor? = null
        cursor = db.rawQuery(selectQuery, arrayOf(customerName))

        val count =  cursor.getCount()
        cursor.close()
        db.close()

        if (count > 0) {
            return true
        }

        return false
    }

    fun validateNewAccWorker(workerName: String): Boolean{
        //VALIDATE IF THE USERNAME ALREADY EXISTS
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_WORKER WHERE $WORK_NAME = ?"
        var cursor: Cursor? = null
        cursor = db.rawQuery(selectQuery, arrayOf(workerName))

        val count =  cursor.getCount()
        cursor.close()
        db.close()

        if (count > 0) {
            return true
        }

        return false
    }

    fun validateNewEmailWorker(email: String): Boolean{
        //VALIDATE IF THE EMAIL ALREADY EXISTS
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_WORKER WHERE $WORK_EMAIL = ?"
        var cursor: Cursor? = null
        cursor = db.rawQuery(selectQuery, arrayOf(email))

        val count =  cursor.getCount()
        cursor.close()
        db.close()


        if (count > 0) {
            return true
        }

        return false
    }

    fun updateWorker(workID:Int,workname:String,workemail:String,workpass:String):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(WORK_NAME, workname)
        contentValues.put(WORK_EMAIL, workemail)
        contentValues.put(WORK_PASS, workpass)

        //UPDATES WORKER INFO
        val success = db.update(TABLE_WORKER,contentValues, "$WORK_ID = $workID", null)
        db.close()
        return success
    }

    fun deleteWorker(workerModel: WorkerModel): Int{
        //DELETE A WORKER IN THE TABLE
        val db = this.writableDatabase

        val success = db.delete(TABLE_WORKER,"$WORK_ID = ${workerModel.workid}", null)
        db.close()
        return success
    }


    fun updateCustomer(custID:Int,custName:String,custEmail:String,custPass:String): Int{
        //UPDATE CUSTOMER INFO
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CUST_NAME, custName)
        contentValues.put(CUST_EMAIL, custEmail)
        contentValues.put(CUST_PASS, custPass)

        val success = db.update(TABLE_CUST, contentValues, "$CUST_ID = $custID", null)
        db.close()
        return success
    }

    fun deleteCustomer(customerModel: CustomerModel): Int{
        //DELETE A CUSTOMER IN THE TABLE
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CUST_ID, customerModel.id)

        val success = db.delete(TABLE_CUST, "$CUST_ID = ${customerModel.id}", null)
        db.close()
        return success
    }

    fun viewCustomer(): ArrayList<CustomerModel>{
        //USED FROM THE EARLIER STAGE OF THE PROGRAM
        //CURRENTLY NOT USING IT NOW FOR NEWER ONE
        val customerList: ArrayList<CustomerModel> = ArrayList<CustomerModel>()

        val selectQuery = "SELECT * FROM $TABLE_CUST"

        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var email: String
        var password: String


        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(CUST_ID))
                name = cursor.getString(cursor.getColumnIndex(CUST_NAME))
                email = cursor.getString(cursor.getColumnIndex(CUST_EMAIL))
                password = cursor.getString(cursor.getColumnIndex(CUST_PASS))

                val cust = CustomerModel(id = id, name = name, Email = email, password = password)
                customerList.add(cust)
            } while (cursor.moveToNext())
        }
        return customerList
    }

    fun getCustomer(customerId: Int):CustomerModel{
        //GET INDIVIDUAL CUSTOMER DATA
        val db = this.readableDatabase
        var id: Int = 0
        var name: String = ""
        var email: String = ""
        var password: String = ""

        var cursor: Cursor? = null
        val selectQuery = "SELECT * FROM $TABLE_CUST WHERE $CUST_ID = $customerId"

        cursor = db.rawQuery(selectQuery,null)

        if (cursor.moveToFirst()){
            id = cursor.getInt(cursor.getColumnIndex(CUST_ID))
            name = cursor.getString(cursor.getColumnIndex(CUST_NAME))
            email = cursor.getString(cursor.getColumnIndex(CUST_EMAIL))
            password = cursor.getString(cursor.getColumnIndex(CUST_PASS))
        }

        return CustomerModel(
            id = id,
            name = name,
            Email = email,
            password = password
        )
    }

    fun getWorker(workId: Int): WorkerModel {
        //GET INDIVIDUAL WORKER DATA
        val db = this.readableDatabase
        var id: Int = 0
        var name: String = ""
        var email: String = ""
        var password: String = ""
        var type: Int = 0

        var cursor: Cursor? = null
        val selectQuery = "SELECT * FROM $TABLE_WORKER WHERE $WORK_ID = $workId"


        cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(WORK_ID))
            name = cursor.getString(cursor.getColumnIndex(WORK_NAME))
            email = cursor.getString(cursor.getColumnIndex(WORK_EMAIL))
            password = cursor.getString(cursor.getColumnIndex(WORK_PASS))
            type = cursor.getInt(cursor.getColumnIndex(WORK_TYPE))
        }

        return WorkerModel(
            workid = id,
            workname = name,
            workemail = email,
            workpassword = password,
            worktype = type
        )
    }

    fun innerJoinWorker():ArrayList<JWorkerModel>{
        //RETURNS AN ARRAYLIST OF INNER JOIN WORKER LIST WITH TABLE TITLE
        val jWorkerList: ArrayList<JWorkerModel> = ArrayList<JWorkerModel>()
        val db = this.readableDatabase
        val selectQuery = "SELECT w.$WORK_ID AS WORKID, w.$WORK_NAME AS WORKNAME, t.$TITLE_NAME AS JTITLE, t.$TITLE_PRICE AS TPRICE " +
                "FROM $TABLE_WORKER w INNER JOIN $TABLE_TITLE t ON t.$TITLE_ID = w.$WORK_TYPE"

        var cursor: Cursor? = null

        try{
            cursor = db.rawQuery(selectQuery,null)
        } catch(e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var workerId:Int
        var workerName: String
        var jobTitle: String
        var pricePay: Int

        if(cursor.moveToFirst()){
            do
            {
                workerId = cursor.getInt(cursor.getColumnIndex("WORKID"))
                workerName = cursor.getString(cursor.getColumnIndex("WORKNAME"))
                jobTitle = cursor.getString(cursor.getColumnIndex("JTITLE"))
                pricePay = cursor.getInt(cursor.getColumnIndex("TPRICE"))

                val worker = JWorkerModel(workId = workerId,
                    workname = workerName,
                    worktype = jobTitle,
                    workprice = pricePay)
                jWorkerList.add(worker)
            }while(cursor.moveToNext())
        }

        return jWorkerList
    }

    fun innerJoinWorker(workid: String):JWorkerModel{
        //GET AND RETURN AN INDIVIDUAL DATA OF THE INNER JOIN WORKER AND TITLE
        val db = this.readableDatabase
        val selectQuery = "SELECT w.$WORK_ID AS WORKID, w.$WORK_NAME AS WORKNAME, t.$TITLE_NAME AS JTITLE, t.$TITLE_PRICE AS TPRICE " +
                "FROM $TABLE_WORKER w INNER JOIN $TABLE_TITLE t ON t.$TITLE_ID = w.$WORK_TYPE " +
                "WHERE w.$WORK_ID = $workid"

        var cursor: Cursor? = null
        cursor = db.rawQuery(selectQuery,null)

        var workerId:Int = 0
        var workerName: String = ""
        var jobTitle: String = ""
        var pricePay: Int = 0

        if(cursor.moveToFirst()){
            workerId = cursor.getInt(cursor.getColumnIndex("WORKID"))
            workerName = cursor.getString(cursor.getColumnIndex("WORKNAME"))
            jobTitle = cursor.getString(cursor.getColumnIndex("JTITLE"))
            pricePay = cursor.getInt(cursor.getColumnIndex("TPRICE"))
        }

        return JWorkerModel(workId = workerId,
            workname = workerName,
            worktype = jobTitle,
            workprice = pricePay)
    }

    fun workerViewBook(workid:Int):ArrayList<BookedModel>{
        //QUERY FOR GETTING THE LIST OF CUSTOMER BOOK FOR WORKER
        val bookedService : ArrayList<BookedModel> = ArrayList<BookedModel>()
        val db = this.readableDatabase
        val selectQuery = "SELECT b.$BOOK_ID AS bookedID,b.$BOOK_ADDRESS AS bookedAddress, c.$CUST_ID AS custID, c.$CUST_NAME AS custName," +
                "w.$WORK_ID AS workerID FROM $TABLE_BOOK b " +
                "INNER JOIN $TABLE_CUST c ON c.$CUST_ID = b.$BOOK_CUST_ID " +
                "INNER JOIN $TABLE_WORKER w ON w.$WORK_ID = b.$BOOK_WORK_ID " +
                "WHERE b.$BOOK_ID NOT IN (SELECT $ACCEPTED_BOOK FROM $TABLE_ACCEPT) " +
                "AND w.$WORK_ID = $workid"

        var cursor: Cursor? = null

        try{
            cursor = db.rawQuery(selectQuery,null)
        } catch(e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var bookID: Int
        var custID: Int
        var custName: String
        var workerID: Int
        var bookAddress: String

        if(cursor.moveToFirst()){
            do
            {
                bookID = cursor.getInt(cursor.getColumnIndex("bookedID"))
                custID = cursor.getInt(cursor.getColumnIndex("custID"))
                custName = cursor.getString(cursor.getColumnIndex("custName"))
                workerID = cursor.getInt(cursor.getColumnIndex("workerID"))
                bookAddress = cursor.getString(cursor.getColumnIndex("bookedAddress"))

                val book = BookedModel(workId = workerID, custId = custID, bookId = bookID, custName = custName, address = bookAddress)
                bookedService.add(book)
            }while(cursor.moveToNext())
        }
    return bookedService
    }

    fun rejectBookService(bookedModel: BookedModel):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(BOOK_ID,bookedModel.bookId)
        //DELETES THE SELECTED BOOK THAT THE WORKER REJECTED
        val success = db.delete(TABLE_BOOK, "$BOOK_ID = ${bookedModel.bookId}", null)
        db.close()
        return success
    }


    fun acceptBookService(bookedModel: BookedModel): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ACCEPTED_BOOK, bookedModel.bookId)

        //STORES DATA OF THE ACCEPTED BOOK
        val success = db.insert(TABLE_ACCEPT, null, contentValues)
        db.close()
        return success
    }

    fun alreadyBooked(custid:Int,workid: Int):Boolean{
        //VALIDATES IF THE PERSON BEING BOOKED IS ALREADY BEING PROCESSED
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_BOOK WHERE $BOOK_CUST_ID = $custid AND $BOOK_WORK_ID = $workid"
        var cursor: Cursor? = null


        cursor = db.rawQuery(selectQuery,null)
        val count = cursor.getCount()
        cursor.close()
        db.close()

        if (count >= 1) {
            return true
        }

        return false
    }

    fun ifAccepted(custID:Int,workID: Int): Boolean{
        //VALIDATES IF IT IS ACCEPTED AND READY FOR THE INVOICE
        val db = this.readableDatabase
        val selectQuery = "SELECT a.* FROM $TABLE_ACCEPT a INNER JOIN $TABLE_BOOK b ON b.$BOOK_ID = a.$ACCEPTED_BOOK " +
                "WHERE b.$BOOK_CUST_ID = $custID AND b.$BOOK_WORK_ID = $workID"
        var cursor: Cursor? = null



        cursor = db.rawQuery(selectQuery,null)
        val count = cursor.getCount()
        cursor.close()
        db.close()

        if(count >= 1){
            return true
        }
        return false
    }

    fun getInvoice(custID:Int,workID:Int):InvoiceModel{
        //FETCH A BOOKING DATA WITH AN ACCEPTED ID NUMBER
        val db = this.readableDatabase
        val selectQuery = "SELECT a.$ACCEPT_ID AS AID, b.$BOOK_ID AS BID, w.$WORK_NAME AS WNAME, w.$WORK_ID AS WID," +
                "t.$TITLE_NAME AS TNAME, t.$TITLE_PRICE AS TPRICE FROM " +
                "$TABLE_ACCEPT a INNER JOIN $TABLE_BOOK b ON b.$BOOK_ID = a.$ACCEPTED_BOOK " +
                "INNER JOIN $TABLE_WORKER w ON w.$WORK_ID = b.$BOOK_WORK_ID " +
                "INNER JOIN $TABLE_TITLE t ON t.$TITLE_ID = w.$WORK_TYPE " +
                "WHERE b.$BOOK_CUST_ID = $custID AND b.$BOOK_WORK_ID = $workID"

        var cursor: Cursor? = null

        cursor = db.rawQuery(selectQuery,null)

        var acceptId = 0
        var bookId = 0
        var wname = ""
        var titleName = ""
        var titlePrice = 0
        var workerid = 0

        if(cursor.moveToFirst()){
            acceptId = cursor.getInt(cursor.getColumnIndex("AID"))
            bookId = cursor.getInt(cursor.getColumnIndex("BID"))
            wname = cursor.getString(cursor.getColumnIndex("WNAME"))
            titleName = cursor.getString(cursor.getColumnIndex("TNAME"))
            titlePrice = cursor.getInt(cursor.getColumnIndex("TPRICE"))
            workerid = cursor.getInt(cursor.getColumnIndex("WID"))
        }

        return InvoiceModel(acceptId,bookId,wname,titleName,titlePrice,workerid)
    }

    fun addPayment(finishModel: FinishModel): Long{
        //INSERTS THE PAYMENT OF THE CUSTOMER FOR THE WORKER
        val db = this.readableDatabase
        val contentValues = ContentValues()
        contentValues.put(FINISH_BAYAD, finishModel.bayad)
        contentValues.put(FIN_WORKER_ID, finishModel.workId)

        val success = db.insert(TABLE_FINISH, null,contentValues)
        db.close()
        return success
    }

    fun invoicePay(bookId:Int){
        //DELETE THE BOOKINGS THAT HAS BEEN DONE
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_ACCEPT WHERE $ACCEPTED_BOOK = $bookId")
        db.execSQL("DELETE FROM $TABLE_BOOK WHERE $BOOK_ID = $bookId")

        db.close()
    }

    fun onDeleteCustomer(customerId:Int){
        //DELETE CUSTOMERS AND THEIR FOREIGN KEY CONSTRAINT COUNTERPART
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_ACCEPT WHERE EXISTS" +
                "(SELECT * FROM $TABLE_BOOK b WHERE b.$BOOK_CUST_ID = $customerId AND " +
                "b.$BOOK_ID = $TABLE_ACCEPT.$ACCEPTED_BOOK)")
        db.execSQL("DELETE FROM $TABLE_BOOK WHERE $BOOK_CUST_ID = $customerId")
        db.execSQL("DELETE FROM $TABLE_CUST WHERE $CUST_ID = $customerId")

        db.close()
    }

    fun onDeleteWorker(workerId:Int){
        //DELETE WORKER AND THEIR FOREIGN KEY CONSTRAINT COUNTERPART
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_FINISH WHERE $FIN_WORKER_ID = $workerId")
        db.execSQL("DELETE FROM $TABLE_ACCEPT WHERE EXISTS" +
                "(SELECT * FROM $TABLE_BOOK b WHERE b.$BOOK_WORK_ID = $workerId AND " +
                "b.$BOOK_ID = $TABLE_ACCEPT.$ACCEPTED_BOOK)")
        db.execSQL("DELETE FROM $TABLE_BOOK WHERE $BOOK_WORK_ID = $workerId")
        db.execSQL("DELETE FROM $TABLE_WORKER WHERE $WORK_ID = $workerId")

        db.close()
    }

    fun bookViewCustomer(custID: Int): ArrayList<lBookModel>{
        //QUERY LIST OF BOOKINGS OF A CUSTOMER
        var bookList: ArrayList<lBookModel> = ArrayList<lBookModel>()
        val db = this.readableDatabase
        val selectQuery = "SELECT b.*, a.$ACCEPT_ID, t.$TITLE_NAME, c.$CUST_ID FROM $TABLE_BOOK b LEFT JOIN $TABLE_ACCEPT a " +
                "ON a.$ACCEPTED_BOOK = b.$BOOK_ID " +
                "INNER JOIN $TABLE_WORKER w ON w.$WORK_ID = b.$BOOK_WORK_ID " +
                "INNER JOIN $TABLE_TITLE t ON t.$TITLE_ID = w.$WORK_TYPE " +
                "INNER JOIN $TABLE_CUST c ON c.$CUST_ID = b.$BOOK_CUST_ID " +
                "WHERE c.$CUST_ID = $custID"
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var bID: Int
        var bPay: Int
        var bAddress: String
        var cID: Int
        var wID: Int
        var aID: Int
        var workType: String

        if(cursor.moveToFirst()){
            do{
                bID = cursor.getInt(cursor.getColumnIndex("b.$BOOK_ID"))
                bPay = cursor.getInt(cursor.getColumnIndex("b.$BOOK_PAY"))
                bAddress = cursor.getString(cursor.getColumnIndex("b.$BOOK_ADDRESS"))
                cID = cursor.getInt(cursor.getColumnIndex("b.$BOOK_CUST_ID"))
                wID = cursor.getInt(cursor.getColumnIndex("b.$BOOK_WORK_ID"))
                if(cursor.isNull(cursor.getColumnIndex("a.$ACCEPT_ID"))){
                    aID = 0
                } else {
                    aID = cursor.getInt(cursor.getColumnIndex("a.$ACCEPT_ID"))
                }
                workType = cursor.getString(cursor.getColumnIndex("t.$TITLE_NAME"))

                var book = lBookModel(bID,bPay,bAddress,cID,wID,aID,workType)
                bookList.add(book)
            }while(cursor.moveToNext())
        }

        return bookList
    }

    fun bookViewWorker(workID: Int): ArrayList<LwBookModel>{
        //QUERY LIST OF BOOKINGS REQUEST OF A WORKER
        var bookList: ArrayList<LwBookModel> = ArrayList<LwBookModel>()
        val db = this.readableDatabase
        val selectQuery = "SELECT b.*, a.$ACCEPT_ID, c.$CUST_NAME FROM $TABLE_BOOK b INNER JOIN $TABLE_ACCEPT a " +
                "ON a.$ACCEPTED_BOOK = b.$BOOK_ID " +
                "INNER JOIN $TABLE_WORKER w ON w.$WORK_ID = b.$BOOK_WORK_ID " +
                "INNER JOIN $TABLE_CUST c ON c.$CUST_ID = b.$BOOK_CUST_ID " +
                "WHERE w.$WORK_ID = $workID"

        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var bID: Int
        var bPay: Int
        var bAddress: String
        var cID: Int
        var wID: Int
        var aID: Int
        var custName:String


        if(cursor.moveToFirst()){
            do{
                bID = cursor.getInt(cursor.getColumnIndex("b.$BOOK_ID"))
                bPay = cursor.getInt(cursor.getColumnIndex("b.$BOOK_PAY"))
                bAddress = cursor.getString(cursor.getColumnIndex("b.$BOOK_ADDRESS"))
                cID = cursor.getInt(cursor.getColumnIndex("b.$BOOK_CUST_ID"))
                wID = cursor.getInt(cursor.getColumnIndex("b.$BOOK_WORK_ID"))
                aID = cursor.getInt(cursor.getColumnIndex("a.$ACCEPT_ID"))
                custName = cursor.getString(cursor.getColumnIndex("c.$CUST_NAME"))

                var book = LwBookModel(bID,bPay,bAddress,cID,wID,aID,custName)
                bookList.add(book)
            }while(cursor.moveToNext())
        }

        return bookList
    }


    fun updateBooking(bookId: Int,bookaddress:String):Int{
        //UPDATES THE BOOKING INFO/ADDRESS
        val db = this.readableDatabase
        val contentValues = ContentValues()
        contentValues.put(BOOK_ADDRESS, bookaddress)


        val success = db.update(TABLE_BOOK, contentValues, "$BOOK_ID = $bookId", null)
        db.close()
        return success
    }

    fun deleteBooking(bookId: Int){
        //DELETE BOOKING ALONG WITH ITS KEY CONSTRAINTS
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_ACCEPT WHERE $ACCEPTED_BOOK = $bookId")
        db.execSQL("DELETE FROM $TABLE_BOOK WHERE $BOOK_CUST_ID = $bookId")

        db.close()
    }

    fun withdrawAcceptedBooking(acceptId:Int):Int{
        //DELETE ACCEPTED BOOK
        val db = this.writableDatabase
        val contentValues = ContentValues()

        val success = db.delete(TABLE_ACCEPT,"$ACCEPT_ID = ${acceptId}", null)
        db.close()
        return success
    }

    fun getTotalEarnings(workId:Int):EarnPreview{
        //FETCH DATA ON TOTAL EARNINGS AND TOTAL COUNT OF A WORKER
        val db = this.readableDatabase
        val selectQuery = "SELECT COUNT($FINISH_ID) AS TOTALCOUNT, SUM($FINISH_BAYAD) TOTALEARN " +
                "FROM $TABLE_FINISH WHERE $TABLE_FINISH.$FIN_WORKER_ID = $workId"

        var cursor: Cursor? = null

        cursor = db.rawQuery(selectQuery,null)

        var totalCount = 0
        var totalEarnings = 0

        if(cursor.moveToFirst()){
            totalCount = cursor.getInt(cursor.getColumnIndex("TOTALCOUNT"))
            totalEarnings = cursor.getInt(cursor.getColumnIndex("TOTALEARN"))
        }

        return EarnPreview(totalCount,totalEarnings)
    }









}