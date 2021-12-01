package com.example.aizhanlibrary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.aizhanlibrary.databinding.RowBookBinding
import java.util.ArrayList

class AdapterBook : Adapter<AdapterBook.HolderBook>{

    private var context: Context

    private var bookArrayList: ArrayList<ModelPdf>


    private lateinit var binding:RowBookBinding

    constructor(context: Context, bookArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.bookArrayList = bookArrayList
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderBook {
        binding = RowBookBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderBook(binding.root)
    }

    override fun onBindViewHolder(holder: HolderBook, position: Int) {
        //get data
        val model = bookArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp

        //lets create
        val formattedDate = MyApplication.formatTimeStamp(timestamp)
        holder.descriptionTv.text = description
        holder.titleTv.text = title
        holder.dateTv.text = formattedDate


        MyApplication.loadCategory(categoryId, holder.categoryTv)

        MyApplication.loadBookFromUrlSinglePage(pdfUrl, title, holder.pdfView, holder.progressBar, null)

        MyApplication.loadBookSize(pdfUrl, title, holder.sizeTv)
    }

    override fun getItemCount(): Int {
        return bookArrayList.size
    }

    inner class HolderBook(itemView: View): ViewHolder(itemView){
        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val moreBtn = binding.moreBtn
    }
}