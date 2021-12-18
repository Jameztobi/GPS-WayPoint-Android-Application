package com.example.assignment_3

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var _rotation: TextView
    private lateinit var _sm: SensorManager
    private var  _rotation_matrix: FloatArray = FloatArray(16){0f}
    private var _orientation_values:FloatArray = FloatArray(4){0f}
    private lateinit var compassView: CustomView
    private lateinit var _linear_layout: RelativeLayout
    private var hasSensor = false
    private var hasSensor2 = false
    private lateinit var _lm: LocationManager
    private lateinit var _handler: ThreadHandler
    private lateinit var _thread: UpdateThread
    private var latitude: Float = 0.0f
    private var longitude: Float = 0.0f
    private lateinit var locationLis: LocationListener
    private lateinit var wayPointBtn: Button
    private lateinit var clearWayPointBtn: Button
    private lateinit var btnSelect: Button
    private lateinit var btnStart: Button
    private lateinit var btnEnd: Button
    private var wayPointList: ArrayList<String> = ArrayList()
    private val sharedPrefFile = "kotlinsharedpreferences"
    private lateinit var sharedPreferences: SharedPreferences
    private var wayPointCounter = 0
    private var temp = 0
    private var count = 0
    private var temp_key = 0
    private var retrieveWayPointList: ArrayList<String> = ArrayList()
    private var wayPointSelected: Boolean = false
    private lateinit var leftBtn: Button
    private lateinit var rightBtn: Button
    private lateinit var currentWayPoint: TextView
    private lateinit var distancetext: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compassView = findViewById<CustomView>(R.id.compass_view)
        _linear_layout = findViewById<RelativeLayout>(R.id.linear_layout)
         wayPointBtn = findViewById<Button>(R.id.addWayPointBtn)
        clearWayPointBtn = findViewById(R.id.clearWayPointBtn)
        leftBtn = findViewById(R.id.btn_left)
        currentWayPoint = findViewById(R.id.text)
        rightBtn = findViewById(R.id.btn_right)
        btnSelect = findViewById(R.id.btnSelect)
        distancetext = findViewById(R.id.distance)
        btnStart = findViewById(R.id.addStartTracking)
        btnEnd = findViewById(R.id.addEndTracking)

        //disable button view
        wayPointBtn.visibility=Button.GONE
        clearWayPointBtn.visibility=Button.GONE


        sharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        _sm = getSystemService(SENSOR_SERVICE) as SensorManager
        // get access to the location manager
        _lm = getSystemService(LOCATION_SERVICE) as LocationManager
        setLocationListener()
        settingValues()
        leftBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
               leftBtnController()
            }
        })
        rightBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                rightBtnController()
            }
        })
        wayPointBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var locationList: ArrayList<String> = ArrayList()
                locationList.add(getLatitude().toString())
                locationList.add(getLongitude().toString())
                SharedPreferencesUtil.addList(
                    sharedPreferences,
                    locationList,
                    wayPointCounter.toString()
                )
                temp = SharedPreferencesUtil.getSize(sharedPreferences) / 3
                wayPointList.clear()
                settingValues()
            }
        })

        clearWayPointBtn.setOnClickListener{
            if(currentWayPoint.text.equals("No wayPoint")){
                showMessages("You have not added any waypoints")
            }
            else{
                alertDialog()
            }

        }

        btnSelect.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if(currentWayPoint.text=="No wayPoint"){
                    showMessages("You have not saved any wayPoint")
                    return
                }else{
                    var retrieveList: ArrayList<String> = ArrayList()
                    var textString = currentWayPoint.text
                    var len = textString.replace("[^0-9]".toRegex(), "")
                    var tempKey = len.toInt() - 1
                    var currentRetrieveWayPointList =
                        SharedPreferencesUtil.retrieveSharedList(sharedPreferences, tempKey.toString())
                    retrieveWayPointList = currentRetrieveWayPointList
                    temp_key=tempKey
                    getDestinationDegree()
                    setDistance()
                    compassView.invalidate()
                }
            }
        })

        btnStart.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                addLocationListener()
                wayPointBtn.visibility=Button.VISIBLE
                clearWayPointBtn.visibility=Button.VISIBLE
            }
        })

        btnEnd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                removeLocationListener()
                wayPointBtn.visibility=Button.GONE
                clearWayPointBtn.visibility=Button.GONE
            }
        })


    }





    private fun retrieveDistanceInDegrees(text: ArrayList<String>): Float {
        var latitudePoint: Double = text[0].toDouble()
        var longitudePoint: Double = text[1].toDouble()
        var destinationLocation = Location(LocationManager.GPS_PROVIDER)
        destinationLocation.latitude = latitudePoint
        destinationLocation.longitude = longitudePoint
        var currentLocation = Location(LocationManager.GPS_PROVIDER)
        currentLocation.latitude=latitude.toDouble()
        currentLocation.longitude=longitude.toDouble()
        return currentLocation.bearingTo(destinationLocation)
    }

    private fun retrieveDistanceInMetres(text: ArrayList<String>): Float{
        var latitudePoint: Double = text[0].toDouble()
        var longitudePoint: Double = text[1].toDouble()
        var destinationLocation = Location(LocationManager.GPS_PROVIDER)
        destinationLocation.latitude = latitudePoint
        destinationLocation.longitude = longitudePoint
        var currentLocation = Location(LocationManager.GPS_PROVIDER)
        currentLocation.latitude=getLatitude().toDouble()
        currentLocation.longitude=getLongitude().toDouble()
        return currentLocation.distanceTo(destinationLocation)
    }



    private fun settingValues() {
        when {
            SharedPreferencesUtil.getSize(sharedPreferences)==0 -> {
                currentWayPoint.setText("No wayPoint")
            }
            SharedPreferencesUtil.getSize(sharedPreferences) > 0 -> {
                val compass_view = findViewById<CustomView>(R.id.compass_view)
                compass_view.invalidate()
                wayPointCounter = SharedPreferencesUtil.getSize(sharedPreferences)/3
                setDefaultWayPoint()
                getWayPoints()
                setWayPointText()
            }

        }
    }

    private fun setLocationListener() {
        addRotationListener()
        locationLis = object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                // update the textviews with the current location
                latitude = p0.latitude.toFloat()
                longitude = p0.longitude.toFloat()
                Log.d("Latitude", getLatitude().toString())
                Log.d("Longitude", getLongitude().toString())
                setWayPointCompass()
            }
        }
    }

    private fun leftBtnController(){
         var textString = currentWayPoint.text
        if(textString.equals("No wayPoint")){
            showMessages("You have not added any waypoints")

        }
        else{
            var len = textString.replace("[^0-9]".toRegex(), "")
            count = len.toInt()-1
            if (count-1 >= 0) {
                currentWayPoint.text = wayPointList[count-1]
            }
        }


    }


    private fun rightBtnController(){
        var textString = currentWayPoint.text
        if(textString.equals("No wayPoint")){
            showMessages("You have not added any waypoints")

        }
        else{
            var len = textString.replace("[^0-9]".toRegex(), "")
            count = len.toInt()
            if(count<wayPointList.size){
                currentWayPoint.text=wayPointList[count]
            }
        }

    }

    private fun setWayPointText() {
        temp=SharedPreferencesUtil.getSize(sharedPreferences)/3
        currentWayPoint.text = wayPointList[temp-1]
    }

    private fun setDistance() {
        var distanceCovered=retrieveDistanceInMetres(retrieveWayPointList)
        Log.d("distance", distanceCovered.toString())
        distancetext.text=distanceCovered.toString()
    }


    private fun setDefaultWayPoint(){
        if(wayPointCounter==0){
            currentWayPoint.setText("No wayPoint")
            return
        }

    }

    private fun getWayPoints(){
        if(wayPointCounter>0){
            for (i in 0 until wayPointCounter){
                wayPointList.add("wayPoint"+(i+1))
            }
        }
    }


    private fun addRotationListener(){
        if(_sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)!=null){
            _sm.registerListener(object: SensorEventListener{
                override fun onSensorChanged(p0: SensorEvent?) {
                    SensorManager.getRotationMatrixFromVector(_rotation_matrix, p0!!.values)
                    SensorManager.getOrientation(_rotation_matrix, _orientation_values)
                    _orientation_values[0] = Math.toDegrees(_orientation_values[0].toDouble()).toFloat()
                    _orientation_values[1] = Math.toDegrees(_orientation_values[1].toDouble()).toFloat()
                    _orientation_values[2] = Math.toDegrees(_orientation_values[2].toDouble()).toFloat()
                    getDegree()
                    compassView.invalidate()
                }

                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

                }

            }, _sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun setWayPointCompass(){
        if(wayPointCounter>0){
            val compass_view = findViewById<CustomView>(R.id.compass_view)
            compass_view.invalidate()
            var distanceArray: ArrayList<Float> = ArrayList()
            var meterArray: ArrayList<Float> = ArrayList()
            for(i in 0 until wayPointCounter){
                var value = SharedPreferencesUtil.retrieveSharedList(sharedPreferences, i.toString())
                if(retrieveDistanceInMetres(value)>500.0F){
                    meterArray.add(500.0F)
                }
                else{
                    meterArray.add(retrieveDistanceInMetres(value))
                }

                distanceArray.add(retrieveDistanceInDegrees(value))
            }
            compassView.setWayPointOnView( meterArray, distanceArray)
        }

    }


    fun getDegree(){
        compassView.setDegree(_orientation_values[0])

    }

    fun getDestinationDegree() {
        compassView.setWayPointDegree(retrieveDistanceInDegrees(retrieveWayPointList), temp_key)

    }

    private fun getLongitude(): Float{
        return longitude
    }

    private  fun getLatitude(): Float{
        return latitude
    }



    private fun addLocationListener() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
                // return after the call to request permissions as we don't know if the user has allowed it
            return
        }
        // private function that will add a location listener that will update every 5 seconds
        _lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, locationLis)

    }

    //remove the location listener
    private fun removeLocationListener(){
        _lm.removeUpdates(locationLis)
    }

    private fun showMessages(message: String){
        var snackbar: Snackbar = Snackbar.make(_linear_layout, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // call the super class version of his first
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // check to see what request code we have
        if(requestCode == 1) {
        // if we have been denied permissions then throw a snack bar message indicating that
            // we need them
            if(grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                var snackbar: Snackbar = Snackbar.make(_linear_layout, "App will not work without location permissions", Snackbar.LENGTH_LONG)
                snackbar.show()
            }
            else {
                var snackbar: Snackbar = Snackbar.make(_linear_layout, "location permissions granted", Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        }
    }

    private fun alertDialog(){
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Deletion Action")
        builder.setMessage("Are you sure you will like to clear all your way points")
        builder.setPositiveButton("Yes", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                SharedPreferencesUtil.clearList(sharedPreferences)
                wayPointCounter=0
                temp=0
                currentWayPoint.setText("No wayPoint")
                wayPointList.removeAll(wayPointList)
                distancetext.text=""
                compassView.clearWayPoints()
            }
        })

        builder.setNegativeButton("No", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                showMessages("Action Canceled")
            }
        })

        var dialog: AlertDialog = builder.create()
        dialog.show()
    }



}

