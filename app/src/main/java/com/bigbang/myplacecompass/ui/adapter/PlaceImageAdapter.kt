package com.bigbang.myplacecompass.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigbang.myplacecompass.R
import com.bigbang.myplacecompass.model.data.Photo
import com.bigbang.myplacecompass.model.data.Result
import com.bigbang.myplacecompass.util.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.image_item_layout.view.*
import kotlinx.android.synthetic.main.location_item_layout.view.*

class PlaceImageAdapter(var imageList: List<Photo>) :
    RecyclerView.Adapter<PlaceImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item_layout, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        val photoSource =
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${imageList[position].photoReference}&key=${Constants.API_KEY}"
        Log.d("TAG_X", photoSource)
        Glide.with(holder.itemView.context)
            .applyDefaultRequestOptions(RequestOptions().centerCrop())
            .load(photoSource)
            .into(holder.itemView.place_image)
    }
}