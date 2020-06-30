package com.bigbang.myplacecompass.ui.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bigbang.myplacecompass.R
import com.bigbang.myplacecompass.model.Reminder
import com.bigbang.myplacecompass.model.data.Result
import com.bigbang.myplacecompass.ui.view.PlaceImagesFragment.Companion.PLACE_KEY
import com.bigbang.myplacecompass.ui.adapter.PlaceAdapter
import com.bigbang.myplacecompass.util.PlaceLocationListener
import com.bigbang.myplacecompass.util.showReminderInMap
import com.bigbang.myplacecompass.viewmodel.CompassVMFactory
import com.bigbang.myplacecompass.viewmodel.CompassViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_new_reminder.*

class HomeActivity : BaseActivity(), OnMapReadyCallback, PopupMenu.OnMenuItemClickListener,
    PlaceLocationListener.LocationDelegate, PlaceAdapter.PlaceClickListener,
    GoogleMap.OnMarkerClickListener{

    companion object {
        private const val MY_LOCATION_REQUEST_CODE = 329
        private const val NEW_REMINDER_REQUEST_CODE = 330
        private const val EXTRA_LAT_LNG = "EXTRA_LAT_LNG"

        fun newIntent(context: Context, latLng: LatLng): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra(EXTRA_LAT_LNG, latLng)
            return intent
        }
    }

    private lateinit var mMap: GoogleMap

    private val placeAdapter: PlaceAdapter = PlaceAdapter(mutableListOf(), this)

    private val placeImageFragment: PlaceImagesFragment =
        PlaceImagesFragment()

    private val placeLocationListener: PlaceLocationListener = PlaceLocationListener(this)
    private val compassViewModel: CompassViewModel by viewModels<CompassViewModel>(
        factoryProducer = { CompassVMFactory() })

    private var radius = 1100

    lateinit var placeObserver: Observer<List<Result>>

    private lateinit var locationManager: LocationManager

    private fun displayResults(resultList: List<Result>?) {
        Log.d("TAG_X", "${resultList?.size}")
        resultList?.let { results ->
            placeAdapter.placeList = resultList
            placeAdapter.notifyDataSetChanged()

            drawOnMap(results)
        }
    }

    private fun drawOnMap(results: List<Result>) {

        results.forEach { placeItem ->

            val latLng = LatLng(placeItem.geometry.location.lat, placeItem.geometry.location.lng)
            mMap.addMarker(
                MarkerOptions().position(latLng).title(placeItem.name).icon(
                    BitmapDescriptorFactory.defaultMarker(150f)
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        recyclerView.adapter = placeAdapter
        recyclerView.layoutManager = LinearLayoutManager(this).also {
            it.orientation = LinearLayoutManager.HORIZONTAL
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if((recyclerView.adapter as PlaceAdapter).itemCount > 0)
                        scrollToPosition((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition())
                }
            }
        })
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //newReminder.visibility = View.GONE
        //currentLocation.visibility = View.GONE
        newReminder.setOnClickListener {
            mMap?.run {
                val intent = NewReminderActivity.newIntent(
                    this@HomeActivity,
                    cameraPosition.target,
                    cameraPosition.zoom)
                startActivityForResult(intent, NEW_REMINDER_REQUEST_CODE)
            }
        }

        map_menu_imageview.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            val inflater = popupMenu.menuInflater
            inflater.inflate(R.menu.place_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        my_location_imageview.setOnClickListener {
            setLocation(placeLocationListener.locationLatLng)
        }

        placeObserver = Observer<List<Result>> { resultList ->
            displayResults(resultList)
        }
        compassViewModel.placesMutableData.observe(this, placeObserver)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            200,
            5f,
            placeLocationListener
        )
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
    }

    fun scrollToPosition(position: Int) {
        placeAdapter.placeList[position].let {
            val latLng = LatLng(it.geometry.location.lat, it.geometry.location.lng)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.run {
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isMapToolbarEnabled = false
            setOnMarkerClickListener(this@HomeActivity)
        }

        onMapAndPermissionReady()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val placeType = when (item.itemId) {
            R.id.zoo_item -> "zoo"
            R.id.hospital_item -> "hospital"
            R.id.laundry_item -> "laundry"
            R.id.school_item -> "school"
            R.id.park_item -> "park"
            R.id.police_item -> "police"
            R.id.cafe_item -> "cafe"
            R.id.gym_item -> "gym"
            else -> ""
        }
        Log.d("TAG_X", "Loacation String "+placeLocationListener.locationString)
        compassViewModel.getGetNearbyPlaces(placeLocationListener.locationString, radius, placeType)
        return true
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun setLocation(location: Location) {

        var icon = BitmapFactory.decodeResource(resources, R.drawable.me_icon)
        icon = Bitmap.createScaledBitmap(icon, 300, 300, false)
        val currentLocation = LatLng(location.latitude, location.longitude)
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.addMarker(
            MarkerOptions().position(currentLocation).title("This is you!").icon(
                BitmapDescriptorFactory.fromBitmap(icon)
            )
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

        mMap.addCircle(
            CircleOptions().center(currentLocation).radius(radius.toDouble())
                .fillColor(resources.getColor(R.color.blue_alpha75, resources.newTheme()))
        )

    }

    override fun selectPlace(place: Result) {
        supportFragmentManager.beginTransaction()
            .add(R.id.place_frame, placeImageFragment.also {
                it.arguments = Bundle().also { bundle ->
                    bundle.putSerializable(PLACE_KEY, place)
                }
            })
            .addToBackStack(placeImageFragment.tag)
            .commit()
        supportFragmentManager.executePendingTransactions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NEW_REMINDER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            showReminders()

            val reminder = getRepository().getLast()
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(reminder?.latLng, 15f))

            Toast.makeText(this, R.string.reminder_added_success, Toast.LENGTH_LONG).show()
           // Snackbar.make(main, R.string.reminder_added_success, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            onMapAndPermissionReady()
        }
    }

    private fun onMapAndPermissionReady() {
        if (mMap != null
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap?.isMyLocationEnabled = true
            newReminder.visibility = View.VISIBLE
            currentLocation.visibility = View.VISIBLE

            currentLocation.setOnClickListener {
                val bestProvider = locationManager.getBestProvider(Criteria(), false)
                val location = locationManager.getLastKnownLocation(bestProvider)
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }

            showReminders()

            centerCamera()
        }
    }

    private fun centerCamera() {
        if (intent.extras != null && intent.extras!!.containsKey(EXTRA_LAT_LNG)) {
            val latLng = intent.extras!!.get(EXTRA_LAT_LNG) as LatLng
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    private fun showReminders() {
        mMap?.run {
            clear()
            for (reminder in getRepository().getAll()) {
                showReminderInMap(this@HomeActivity, this, reminder)
            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        val reminder = getRepository().get(marker?.tag as String)

        if (reminder != null) {
            showReminderRemoveAlert(reminder)
        }

        return true
    }

    private fun showReminderRemoveAlert(reminder: Reminder) {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.run {
            setMessage(getString(R.string.reminder_removal_alert))
            setButton(AlertDialog.BUTTON_POSITIVE,
                getString(R.string.reminder_removal_alert_positive)) { dialog, _ ->
                removeReminder(reminder)
                dialog.dismiss()
            }
            setButton(AlertDialog.BUTTON_NEGATIVE,
                getString(R.string.reminder_removal_alert_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun removeReminder(reminder: Reminder) {
        getRepository().remove(
            reminder,
            success = {
                showReminders()
                Snackbar.make(main, R.string.reminder_removed_success, Snackbar.LENGTH_LONG).show()
            },
            failure = {
                Snackbar.make(main, it, Snackbar.LENGTH_LONG).show()
            })
    }

}
