package com.example.assignment.adapters

/**
 * This class contains the adapter for the recycler view on the main app page.
 */
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.example.assignment.models.ImageModel
import com.example.assignment.singletons.NetworkUtils
import com.squareup.picasso.Picasso


class ImageAdapter(private var images: List<ImageModel>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val textView: TextView = itemView.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ImageViewHolder(view)
    }

    var picassoErrorDetected = false
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentItem = images[position]
        holder.textView.text = currentItem.author

        Picasso.get()
            .load(currentItem.download_url)
            .resize(800,400)
            .centerCrop()
            .into(holder.imageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    picassoErrorDetected = false
                }
                override fun onError(e: Exception) {
                    if (!picassoErrorDetected){
                        NetworkUtils.networkStatus.postValue(false)
                        picassoErrorDetected = true
                    }
                }
            })
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setData(newData: List<ImageModel>) {
        this.images = newData
        notifyDataSetChanged()
    }
}
