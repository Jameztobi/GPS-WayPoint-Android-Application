package com.example.assignment_3

import android.Manifest
import android.content.DialogInterface
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
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar

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
    private var latitude: String = ""
    private var longitude: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compassView = findViewById<CustomView>(R.id.compass_view)
        _linear_layout = findViewById<RelativeLayout>(R.id.linear_layout)
        //_rotation = findViewById<TextView>(R.id.test)
        _sm = getSystemService(SENSOR_SERVICE) as SensorManager
        // get access to the location manager
        _lm = getSystemService(LOCATION_SERVICE) as LocationManager
        addRotationListener()
        _handler = ThreadHandler(this)
        _thread = UpdateThread(_handler)
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
                    //_rotation.setText("Rotation (degrees) bearing(z), pitch(x), roll(y): " + _orientation_values[0] + ", " + _orientation_values[1] + ", " + _orientation_values[2])
                }

                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

                }

            }, _sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_UI)
        }

    }


    public fun getDegree(){
        compassView.getDegree(_orientation_values[0])
    }

        // function responsible for inflating and attaching our menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // call the inflator for the options menu and attach it to the menu that has been passed // in here
        menuInflater.inflate(R.menu.menu, menu)
         // it is a requirement to call this at the end of this function
        return super.onCreateOptionsMenu(menu)
    }

    // function that will react to an event on any of hte menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean { // determine which menu item was selected
        when(item.itemId)
        {
            R.id.startTracking -> {
                addLocationListener()
                return true
            }
            R.id.stopTracking -> {
                return true
            }
        }
       // it is a requirement to call this at the end of this function if the event has not been // handled
        return super.onOptionsItemSelected(item)
    }


    // private function that will add a location listener that will update every 5 seconds
    private fun addLocationListener() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // we will request the permissions for the fine and coarse location. like setOnActivityResult we have to add a request code
                // here as in larger applications there may be multiple permission requests to deal with.
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
                // return after the call to request permissions as we don't know if the user has allowed it
            return
        }
        _lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f,
            object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                // update the textviews with the current location
                latitude=p0.latitude.toString()
                longitude=p0.longitude.toString()
                Log.d("Latitude", latitude)
                Log.d("Longitude", longitude)

            }
        })
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


}

