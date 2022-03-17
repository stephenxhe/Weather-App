package com.example.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private var latitudeLabel: String? = null
    private var longitudeLabel: String? = null
    private var latitudeText: TextView? = null
    private var longitudeText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        latitudeLabel = resources.getString(R.string.latitudeBabel)
        longitudeLabel = resources.getString(R.string.longitudeBabel)
        latitudeText = findViewById<View>(R.id.latitudeText) as TextView
        longitudeText = findViewById<View>(R.id.longitudeText) as TextView
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    public override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions() // TODO implement
            }
        }
        else {
            getLastLocation() // TODO implement
        }
    }

    private fun showSnackbar(mainTextStringId: String, actionStringId: String, listener: View.OnClickListener) {
        Toast.makeText(this@MainActivity, mainTextStringId, Toast.LENGTH_LONG).show()
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar("Location permission is needed for core functionality", "Okay",
                View.OnClickListener {
                    startLocationPermissionRequest()
                })
        }
        else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        fusedLocationClient?.lastLocation!!.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result
                latitudeText!!.text = latitudeLabel + ": " + (lastLocation)!!.latitude
                longitudeText!!.text = longitudeLabel + ": " + (lastLocation)!!.longitude
            }
            else {
                Log.w(TAG, "getLastLocation:exception", task.exception)
                showMessage("No location detected. Make sure location is enabled on the device.")
            }
        }
    }

    private fun showMessage(string: String) {
        val container = findViewById<View>(R.id.body)
        if (container != null) {
            Toast.makeText(this@MainActivity, string, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }
}