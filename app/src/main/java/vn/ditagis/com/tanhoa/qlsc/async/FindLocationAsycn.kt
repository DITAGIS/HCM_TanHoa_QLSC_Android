package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.util.ArrayList
import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.entities.DAddress

class FindLocationAsycn(@field:SuppressLint("StaticFieldLeak")
                        private val mContext: Context, private val mIsFromLocationName: Boolean, private val mGeocoder: Geocoder,
                        private val mDelegate: AsyncResponse) : AsyncTask<String, Void, List<DAddress>?>() {
    private var mLongtitude: Double = 0.toDouble()
    private var mLatitude: Double = 0.toDouble()

    interface AsyncResponse {
        fun processFinish(output: List<DAddress>?)
    }

    fun setmLongtitude(mLongtitude: Double) {
        this.mLongtitude = mLongtitude
    }

    fun setmLatitude(mLatitude: Double) {
        this.mLatitude = mLatitude
    }
    override fun doInBackground(vararg params: String): List<DAddress>? {
        if (!Geocoder.isPresent())
            return null
        val lstLocation = ArrayList<DAddress>()
        if (mIsFromLocationName) {
            val text = params[0]
            try {
                val addressList = mGeocoder.getFromLocationName(text, 5)
                for (address in addressList)
                    lstLocation.add(DAddress(address.longitude, address.latitude,
                            address.subAdminArea, address.locality, address.getAddressLine(0)))
            } catch (ignored: IOException) {
                //todo grpc failed
                Log.e("error", ignored.toString())
            }

        } else {
            try {
                val addressList = mGeocoder.getFromLocation(mLatitude, mLongtitude, 1)
                for (address in addressList)
                    lstLocation.add(DAddress(address.longitude, address.latitude,
                            address.subAdminArea, address.locality, address.getAddressLine(0)))
            } catch (ignored: IOException) {
                Log.e("error", ignored.toString())
            }

        }


        return lstLocation
    }

    override fun onPostExecute(dAddresses: List<DAddress>?) {
        super.onPostExecute(dAddresses)
        if (dAddresses == null)
            Toast.makeText(mContext, R.string.message_no_geocoder_available, Toast.LENGTH_LONG).show()
        this.mDelegate.processFinish(dAddresses)
    }
}
