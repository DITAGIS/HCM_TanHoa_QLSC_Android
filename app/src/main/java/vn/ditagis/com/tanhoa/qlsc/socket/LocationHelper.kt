package vn.ditagis.com.tanhoa.qlsc.socket

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.AsyncTask
import android.util.Log

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

import java.util.ArrayList

/**
 * Created by ThanLe on 4/16/2018.
 */
@SuppressLint("StaticFieldLeak")
class LocationHelper(private val mContext: Context, delegate: AsyncResponse) : AsyncTask<Void, Any, Void>(), PermissionUtils.PermissionResultCallback {
    private val currentActivity: Activity

    private var isPermissionGranted: Boolean = false

    private var mLastLocation: Location? = null

    // Google client to interact with Google API

    /**
     * Method used to get the GoogleApiClient
     */
    private var googleApiCLient: GoogleApiClient? = null
        private set

    // list of permissions

    private val permissions = ArrayList<String>()
    private val permissionUtils: PermissionUtils

    private var delegate: AsyncResponse? = null

    /**
     * Method to display the location on UI
     */

    val location: Location?
        get() {

            if (isPermissionGranted) {

                try {
                    mLastLocation = LocationServices.FusedLocationApi
                            .getLastLocation(googleApiCLient)

                    return mLastLocation
                } catch (e: SecurityException) {
                    e.printStackTrace()

                }

            }

            return null

        }

    // All location settings are satisfied. The client can initialize location requests here
    // Show the dialog by calling startResolutionForResult(),
    // and check the result in onActivityResult().
    // Ignore the error.
//    val stateLocation: Boolean
//        @SuppressLint("RestrictedApi")
//        get() {
//            val mLocationRequest = LocationRequest()
//            mLocationRequest.interval = 10000
//            mLocationRequest.fastestInterval = 5000
//            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//
//            val builder = LocationSettingsRequest.Builder()
//                    .addLocationRequest(mLocationRequest)
//            val result = LocationServices.SettingsApi.checkLocationSettings(googleApiCLient, builder.build())
//
//            result.setResultCallback { locationSettingsResult ->
//                val status = locationSettingsResult.status
//
//                when (status.statusCode) {
//                    LocationSettingsStatusCodes.SUCCESS -> mLastLocation = location
//                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
//                        status.startResolutionForResult(currentActivity, REQUEST_CHECK_SETTINGS)
//
//                    } catch (e: IntentSender.SendIntentException) {
//                    }
//
//                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//                    }
//                }
//            }
//            return false
//        }

    interface AsyncResponse {
        fun processFinish(longtitude: Double, latitude: Double)
    }

    init {
        this.delegate = delegate
        this.currentActivity = mContext as Activity

        permissionUtils = PermissionUtils(mContext, this)

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

    }

    /**
     * Method to check the availability of location permissions
     */

    fun checkPermission() {
        permissionUtils.checkPermission(permissions, "Need GPS permission for getting your location", 1)
    }

    /**
     * Method to verify google play services on the device
     */

//    fun checkPlayServices(): Boolean {
//
//        val googleApiAvailability = GoogleApiAvailability.getInstance()
//
//        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(mContext)
//
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (googleApiAvailability.isUserResolvableError(resultCode)) {
//                googleApiAvailability.getErrorDialog(currentActivity, resultCode,
//                        PLAY_SERVICES_REQUEST).show()
//            } else {
//                showToast("This device is not supported.")
//            }
//            return false
//        }
//        return true
//    }

//    fun getAddress(latitude: Double, longitude: Double): Address? {
//        val geocoder: Geocoder
//        val addresses: List<Address>
//        geocoder = Geocoder(mContext, Locale.getDefault())
//
//        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            return addresses[0]
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        return null
//
//    }


    /**
     * Method used to build GoogleApiClient
     */

//    fun buildGoogleApiClient() {
//        googleApiCLient = GoogleApiClient.Builder(mContext)
//                .addConnectionCallbacks(currentActivity as GoogleApiClient.ConnectionCallbacks)
//                .addOnConnectionFailedListener(currentActivity as GoogleApiClient.OnConnectionFailedListener)
//                .addApi(LocationServices.API).build()
//
//        googleApiCLient!!.connect()
//
//        @SuppressLint("RestrictedApi") val mLocationRequest = LocationRequest()
//        mLocationRequest.interval = 10000
//        mLocationRequest.fastestInterval = 5000
//        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//
//        val builder = LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequest)
//
//        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiCLient, builder.build())
//
//        result.setResultCallback { locationSettingsResult ->
//            val status = locationSettingsResult.status
//
//            when (status.statusCode) {
//                LocationSettingsStatusCodes.SUCCESS ->
//                    // All location settings are satisfied. The client can initialize location requests here
//                    mLastLocation = location
//                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
//                    // Show the dialog by calling startResolutionForResult(),
//                    // and check the result in onActivityResult().
//                    status.startResolutionForResult(currentActivity, REQUEST_CHECK_SETTINGS)
//
//                } catch (e: IntentSender.SendIntentException) {
//                    // Ignore the error.
//                }
//
//                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//                }
//            }
//        }
//
//
//    }

    /**
     * Method used to connect GoogleApiClient
     */
//    fun connectApiClient() {
//        googleApiCLient!!.connect()
//    }


    /**
     * Handles the permission results
     */
//    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }

    /**
     * Handles the activity results
     */
//    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        when (requestCode) {
//            REQUEST_CHECK_SETTINGS -> when (resultCode) {
//                Activity.RESULT_OK ->
//                    // All required changes were successfully made
//                    mLastLocation = location
//                Activity.RESULT_CANCELED -> {
//                }
//                else -> {
//                }
//            }// The user was asked to change settings, but chose not to
//        }
//    }


    override fun permissionGranted(request_code: Int) {
        Log.i("PERMISSION", "GRANTED")
        isPermissionGranted = true
    }

    override fun partialPermissionGranted(request_code: Int, granted_permissions: ArrayList<String>) {
        Log.i("PERMISSION PARTIALLY", "GRANTED")
    }

    override fun permissionDenied(request_code: Int) {
        Log.i("PERMISSION", "DENIED")
    }

    override fun neverAskAgain(request_code: Int) {
        Log.i("PERMISSION", "NEVER ASK AGAIN")
    }




    override fun doInBackground(vararg voids: Void): Void? {
        googleApiCLient = GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(currentActivity as GoogleApiClient.ConnectionCallbacks)
                .addOnConnectionFailedListener(currentActivity as GoogleApiClient.OnConnectionFailedListener)
                .addApi(LocationServices.API).build()

        googleApiCLient!!.connect()

        @SuppressLint("RestrictedApi") val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)

        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiCLient, builder.build())

        result.setResultCallback { locationSettingsResult ->
            val status = locationSettingsResult.status

            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    // All location settings are satisfied. The client can initialize location requests here
                    mLastLocation = location
                    if (mLastLocation != null)
                        delegate!!.processFinish(mLastLocation!!.longitude, mLastLocation!!.latitude)
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(currentActivity, REQUEST_CHECK_SETTINGS)

                } catch (e: IntentSender.SendIntentException) {
                    // Ignore the error.
                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
            publishProgress()
        }
        return null
    }



    companion object {

        private const val REQUEST_CHECK_SETTINGS = 2000
    }
}

