package com.business.lawco.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.R
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.business.lawco.databinding.ItemUploadBinding
import java.net.URLEncoder

class ImageShowAdapter( var requireContext: Context , var list: MutableList<String>) :
    RecyclerView.Adapter<ImageShowAdapter.Holder>() {
    class Holder(val binding: ItemUploadBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemUploadBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(binding)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.imgCross.visibility = View.GONE
        holder.binding.hideUpload.visibility = View.GONE
        val item = list[position]

        if (item.endsWith(".pdf",true) || item.endsWith(".doc",true)|| item.endsWith(".docx",true)){
            val fileIcon = when {
                item.lowercase().endsWith(".pdf") -> com.business.lawco.R.drawable.pdficon
                item.lowercase().endsWith(".doc") || item.lowercase().endsWith(".docx") ->
                    com.business.lawco.R.drawable.docicon
                else ->
                    com.business.lawco.R.drawable.docicon
            }
            Glide.with(requireContext)
                .load(fileIcon)
                .placeholder(fileIcon)
                .error(fileIcon)
                .into(holder.binding.imageData)
        }else{
            Glide.with(requireContext)
                .load(item)
                .error(com.business.lawco.R.drawable.thumbnailicon)
                .placeholder(com.business.lawco.R.drawable.thumbnailicon)
                .into(holder.binding.imageData)
        }


        holder.itemView.setOnClickListener {
            downloadImage(item)
        }

    }


    private fun downloadImage(url: String) {
        val fullScreenDialog = Dialog(requireContext, com.business.lawco.R.style.FullScreenDialog)
        fullScreenDialog.setContentView(com.business.lawco.R.layout.imagepdfalert)
        fullScreenDialog.setCancelable(true)
        fullScreenDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val arrowWhite: ImageView = fullScreenDialog.findViewById(com.business.lawco.R.id.arrowWhite)
        val imgDownload: ImageView = fullScreenDialog.findViewById(com.business.lawco.R.id.imgDownload)
        val img: ImageView = fullScreenDialog.findViewById(com.business.lawco.R.id.img)
        val webView: WebView = fullScreenDialog.findViewById(com.business.lawco.R.id.webView)
        if (url.endsWith(".pdf",true) || url.endsWith(".doc",true)|| url.endsWith(".docx",true)){
            webView.visibility = View.VISIBLE
            img.visibility = View.GONE

            val settings = webView.settings
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }
            }
            val encodedUrl = URLEncoder.encode(url, "UTF-8")
            val googleViewer = "https://docs.google.com/gview?embedded=true&url=$encodedUrl"
            webView.loadUrl(googleViewer)

        }else{
            webView.visibility = View.GONE
            img.visibility = View.VISIBLE
            Glide.with(requireContext)
                .load(url)
                .error(com.business.lawco.R.drawable.thumbnailicon)
                .placeholder(com.business.lawco.R.drawable.thumbnailicon)
                .into(img)
        }
        imgDownload.setOnClickListener {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle("Image Download")
                .setDescription("Downloading image...")
                .setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "downloaded_image.jpg"
                )

            val downloadManager = requireContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(requireContext, "Download started", Toast.LENGTH_SHORT).show()
        }

        arrowWhite.setOnClickListener {
            fullScreenDialog.dismiss()
        }
        fullScreenDialog.show()
    }

}