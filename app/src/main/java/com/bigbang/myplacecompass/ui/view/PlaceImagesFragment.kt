package com.bigbang.myplacecompass.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.bigbang.myplacecompass.R
import com.bigbang.myplacecompass.model.data.Result
import com.bigbang.myplacecompass.ui.adapter.PlaceImageAdapter
import kotlinx.android.synthetic.main.place_details_fragment_layout.*

class PlaceImagesFragment : Fragment() {

    companion object {
        const val PLACE_KEY = "place.key"
    }

    private val imageAdapter: PlaceImageAdapter = PlaceImageAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.place_details_fragment_layout, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_recyclerview.adapter = imageAdapter
        image_recyclerview.layoutManager = LinearLayoutManager(context).also {
            it.orientation = LinearLayoutManager.HORIZONTAL
        }

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(image_recyclerview)

        arguments?.getSerializable(PLACE_KEY)?.let { place ->
            val images = (place as Result).photos

            open_maps_button.setOnClickListener {
                val locationURI =
                    Uri.parse("geo:${place.geometry.location.lat},${place.geometry.location.lng}")
                val intent = Intent(Intent.ACTION_VIEW, locationURI)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }

            if (images?.isNotEmpty() == true) {
                imageAdapter.imageList = images
                imageAdapter.notifyDataSetChanged()
            } else {
                makeToast(getString(R.string.no_photos))
            }

        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}