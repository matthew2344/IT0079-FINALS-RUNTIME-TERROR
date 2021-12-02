package com.example.cleanhub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.booking_list.view.*
import kotlinx.android.synthetic.main.booking_list_worker.view.*
import kotlinx.android.synthetic.main.item_list.view.*
import kotlinx.android.synthetic.main.personnel_card.view.*

class ItemAdapter(val context: Context, val items: ArrayList<JWorkerModel>):
    RecyclerView.Adapter<ItemAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.personnel_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.jobTitle.text = item.worktype
        var price = "$${item.workprice}"
        holder.workerName.text = item.workname
        holder.bookPrice.text = price

        if(item.worktype == "Maintenance"){
            holder.workerImage.visibility = View.VISIBLE
            holder.janitorImage.visibility = View.GONE
        }else{
            holder.janitorImage.visibility = View.VISIBLE
            holder.workerImage.visibility = View.GONE
        }



        holder.bookThis.setOnClickListener {
            if (context is HomePage){
                context.goToBooking(item.workId.toString(),item.worktype)
            }
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val jobTitle = view.jobTitle
        val workerName = view.WorkerName
        val bookThis = view.bookThis
        val bookPrice = view.bookPrice
        val workerImage = view.WorkerImage
        val janitorImage = view.JanitorImage
    }

}
class SecondAdapter(val context: Context, val items: ArrayList<BookedModel>):
    RecyclerView.Adapter<SecondAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        var text = "${item.custName} Booked Your Service!"
        holder.tvText.text = text

        holder.bookItem.setOnClickListener{
            if(context is HomePageWorker){
                context.acceptBookDialog(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvText = view.tvText
        val bookItem = view.bookItem
    }

}
class thirdAdapter(val context: Context, val items: ArrayList<lBookModel>):
        RecyclerView.Adapter<thirdAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.booking_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        var bookIdText = "BOOK ID ${item.bookID}:"
        holder.tvBookId.text = bookIdText
        if(item.acceptID == 0){
            holder.tvStatus.text = "Pending"
        }else{
            holder.tvStatus.text = "Accepted"
        }

        holder.bookList.setOnClickListener{
            if(context is updateBookings){
                context.bookListDialog(item)
            }
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val tvBookId = view.tvBookIdCustomer
        val tvStatus = view.tvStatusCustomer
        val bookList = view.bookList
    }
}
class fourthAdapter(val context: Context, val items: ArrayList<LwBookModel>):
        RecyclerView.Adapter<fourthAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.booking_list_worker,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        var bookIdText = "BOOK ID ${item.bookID}:"
        holder.tvBookIdWorker.text = bookIdText

        holder.bookListWorker.setOnClickListener{
            if(context is updateBookingsWorker){
                context.acceptedBook(item)
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val bookListWorker = view.bookListWorker
        val tvBookIdWorker = view.tvBookIdWorker
    }


}
