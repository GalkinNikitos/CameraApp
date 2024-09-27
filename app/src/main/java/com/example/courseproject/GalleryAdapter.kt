package com.example.courseproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.courseproject.databinding.ItemOfGalleryBinding
import java.io.File

class GalleryAdapter(private val fileArray: ArrayList<String>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemOfGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val mediaController = MediaController(binding.root.context)

        fun bind(file: File) {
            if (file.extension == "mp4") {
                binding.rowForImage.visibility = View.GONE
                binding.rowForImagePlay.visibility = View.VISIBLE
                binding.rowForImagePlay.setVideoPath(file.absolutePath)
                binding.rowForImagePlay.setMediaController(mediaController)
                mediaController.setAnchorView(binding.rowForImagePlay)
                binding.rowForImagePlay.start()
            } else {
                binding.rowForImagePlay.visibility = View.GONE
                binding.rowForImage.visibility = View.VISIBLE
                Glide.with(binding.root).load(file).into(binding.rowForImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemOfGalleryBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(File(fileArray[position]))
    }

    override fun getItemCount(): Int {
        return fileArray.size
    }
}