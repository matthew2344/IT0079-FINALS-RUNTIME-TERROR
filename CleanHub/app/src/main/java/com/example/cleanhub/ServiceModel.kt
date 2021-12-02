package com.example.cleanhub


class CustomerModel(val id: Int, val name: String, val Email: String, val password: String)
class BookingModel(val bookID: Int, val bookPay : Int, val bookAddress: String, val custID: Int, val workID: Int)
class WorkerModel(val workid: Int, val workname: String, val workemail: String, val workpassword: String, val worktype: Int)
class JWorkerModel(val workId: Int, val workname: String, val worktype: String,val workprice: Int)
class BookedModel(val workId: Int, val custId: Int, val bookId: Int, val custName: String, val address: String)
class InvoiceModel(val acceptID: Int,val bookID: Int,val workname: String, val workTitle: String, val workPrice: Int, val workId: Int)
class FinishModel(val bayad:Int,val workId: Int)
class lBookModel(val bookID: Int, val bookPay : Int, val bookAddress: String, val custID: Int, val workID: Int,val acceptID: Int, val worktype:String)
class LwBookModel(val bookID: Int, val bookPay: Int, val bookAddress: String, val custID: Int, val workId: Int, val acceptID: Int, val custName: String)
class EarnPreview(val workCount: Int, val totalEarn: Int)