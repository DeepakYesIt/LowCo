package com.business.lawco.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.business.lawco.R
import com.business.lawco.databinding.ItemUploadBinding
import com.business.lawco.utility.OnItemSelectListener

class ImageShowAdapter( var requireContext: Context) :
    RecyclerView.Adapter<ImageShowAdapter.Holder>() {
    class Holder(val binding: ItemUploadBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemUploadBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(binding)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(uriList: MutableList<Uri>) {

        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.binding.imgCross.visibility = View.GONE
        holder.binding.hideUpload.visibility = View.VISIBLE
    }

    private fun isPdf(uri: Uri): Boolean {
        val mimeType = requireContext.contentResolver.getType(uri)
        return mimeType.equals("application/pdf", ignoreCase = true)
    }
}