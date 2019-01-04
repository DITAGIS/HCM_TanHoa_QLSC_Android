package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi

import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable

import java.text.ParseException
import java.util.ArrayList
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication


/**
 * Created by ThanLe on 4/16/2018.
 */

class QueryFeatureAsync @RequiresApi(api = Build.VERSION_CODES.O)
constructor(activity: Activity, private val mTrangThai: Int, private val mDiaChi: String, thoiGianPhanAnh: String, @field:SuppressLint("StaticFieldLeak")
private val mDelegate: AsyncResponse) : AsyncTask<Void, List<Feature>?, Void?>() {
    private val mApplication: DApplication
    private val mServiceFeatureTable: ServiceFeatureTable?
    private var mThoiGian: String? = null
    private var mHasTime: Boolean = false

    interface AsyncResponse {
        fun processFinish(output: List<Feature>?)
    }

    init {
        this.mApplication = activity.application as DApplication
        this.mServiceFeatureTable = mApplication.getDFeatureLayer.serviceFeatureTableSuCoThongTin
        this.mThoiGian = thoiGianPhanAnh
        try {
            val date = Constant.DateFormat.DATE_FORMAT.parse(thoiGianPhanAnh)
            this.mThoiGian = formatTimeToGMT(date)
            this.mHasTime = true
        } catch (e: ParseException) {
            this.mHasTime = false
            e.printStackTrace()
        }

    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    private fun formatTimeToGMT(date: Date): String {
        val dateFormatGmt = Constant.DateFormat.DATE_FORMAT_YEAR_FIRST
        dateFormatGmt.timeZone = TimeZone.getTimeZone("GMT")
        return dateFormatGmt.format(date)
    }

    @SuppressLint("DefaultLocale")
    override fun doInBackground(vararg aVoids: Void): Void? {
        try {

            val queryParameters = QueryParameters()
            @SuppressLint("DefaultLocale") val queryClause = StringBuilder(String.format(" %s like N'%%%s%%'" + " and %s = '%s'",
                    Constant.FieldSuCoThongTin.DIA_CHI, mDiaChi,
                    Constant.FieldSuCoThongTin.NHAN_VIEN, mApplication.userDangNhap!!.userName))
            if (mHasTime)
                queryClause.append(String.format(" and %s > date '%s'", Constant.FieldSuCoThongTin.TG_GIAO_VIEC, mThoiGian))
            if (mTrangThai != -1) {
                queryClause.append(String.format(" and %s = %d",
                        Constant.FieldSuCoThongTin.TRANG_THAI, mTrangThai))
            }
            queryParameters.whereClause = queryClause.toString()

            val featureQueryResultListenableFuture = mServiceFeatureTable!!.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
            featureQueryResultListenableFuture.addDoneListener {
                try {
                    val result = featureQueryResultListenableFuture.get()
                    val iterator = result.iterator()
                    var item: Feature
                    val features = ArrayList<Feature>()
                    while (iterator.hasNext()) {
                        item = iterator.next()
                        features.add(item)
                    }
                    publishProgress(features)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    publishProgress()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                    publishProgress()
                }
            }
        } catch (e: Exception) {
            publishProgress()
        }

        return null
    }

    override fun onProgressUpdate(vararg values: List<Feature>?) {
        if (values == null) {
            mDelegate.processFinish(null)
        } else if (values.size > 0) mDelegate.processFinish(values[0])
    }


}