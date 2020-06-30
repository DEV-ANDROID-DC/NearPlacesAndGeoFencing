package com.bigbang.myplacecompass.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigbang.myplacecompass.R
import com.bigbang.myplacecompass.model.data.Result
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.location_item_layout.view.*

class PlaceAdapter(var placeList: List<Result>, val placeClicker: PlaceClickListener) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_item_layout, parent, false)
        return PlaceViewHolder(itemView)
    }

    override fun getItemCount(): Int = placeList.size

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        placeList[position].let { place ->
            holder.itemView.apply {

                this.setOnClickListener {
                    placeClicker.selectPlace(place)
                }

                place_name_textview.text = place.name
                place_address_textview.text = place.vicinity
                price_rating_textview.text = place.priceLevel?.toString()?: "N/A"
                rating_textview.text = null ?: "N/A"

                Glide.with(this.context)
                    .load(place.icon)
                    .into(icon_imageview)

            }
        }
    }

    interface PlaceClickListener{
        fun selectPlace(place: Result)
    }
}