package vn.ditagis.com.tanhoa.qlsc.async

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi

import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.ArcGISFeatureTable
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.GeoElement
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult
import com.esri.arcgisruntime.mapping.view.MapView
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.utities.Popup

/**
 * Created by ThanLe on 4/16/2018.
 */

class SingleTapMapViewAsync(private val mActivity: Activity, private val mPopUp: Popup, private val mClickPoint: android.graphics.Point, private val mMapView: MapView)
    : AsyncTask<Point, Feature?, Void?>() {
    private val mDialog: ProgressDialog?
    private var mSelectedArcGISFeature: ArcGISFeature? = null
    private var isFound = false
    private val mApplication: DApplication

    init {
        this.mApplication = mActivity.application as DApplication
        this.mDialog = ProgressDialog(mActivity, android.R.style.Theme_Material_Dialog_Alert)
    }

    override fun doInBackground(vararg points: Point): Void? {

        val listListenableFuture = mMapView.identifyLayersAsync(mClickPoint, 5.0, false, 1)
        listListenableFuture.addDoneListener {
            val identifyLayerResults: List<IdentifyLayerResult>
            try {
                identifyLayerResults = listListenableFuture.get()
                if (identifyLayerResults.size > 0)
                    for (identifyLayerResult in identifyLayerResults) {
                        run {
                            val elements = identifyLayerResult.elements
                            if (elements.size > 0 && elements[0] is ArcGISFeature && !isFound) {
                                isFound = true
                                mSelectedArcGISFeature = elements[0] as ArcGISFeature
                                val serviceLayerId = mSelectedArcGISFeature!!.featureTable.serviceLayerId
                                if (serviceLayerId == (mApplication.getDFeatureLayer.layer!!.featureTable as ArcGISFeatureTable).serviceLayerId) {
                                    querySuCoThongTin()
                                }
                            }
                        }
                    }
                else
                    publishProgress()
            } catch (e: InterruptedException) {
                e.printStackTrace()
                publishProgress()
            } catch (e: ExecutionException) {
                e.printStackTrace()
                publishProgress()
            }
        }
        return null
    }

    private fun querySuCoThongTin() {
        mApplication.geometry = mSelectedArcGISFeature!!.geometry
        val queryClause = String.format("%s = '%s' and %s = '%s'",
                Constant.FIELD_SUCOTHONGTIN.ID_SUCO, mSelectedArcGISFeature!!.attributes[Constant.FIELD_SUCO.ID_SUCO].toString(),
                Constant.FIELD_SUCOTHONGTIN.NHAN_VIEN, mApplication.userDangNhap!!.userName)
        val queryParameters = QueryParameters()
        queryParameters.whereClause = queryClause
        QueryServiceFeatureTableAsync(mActivity, mApplication.getDFeatureLayer.serviceFeatureTableSuCoThongTin!!,
                object : QueryServiceFeatureTableAsync.AsyncResponse {
                    override fun processFinish(output: Feature?) {
                        if (output != null) publishProgress(output)
                    }
                }).execute(queryParameters)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mDialog!!.setMessage("Đang xử lý...")
        mDialog.setCancelable(false)
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hủy") { dialogInterface, i -> publishProgress() }
        mDialog.show()
    }

    override fun onProgressUpdate(vararg values: Feature?) {
        super.onProgressUpdate(*values)
        //        if (values != null && mSelectedArcGISFeature != null && values.length > 0 && values[0] != null) {
        //
        //            FeatureLayer featureLayer = values[0];
        //            mPopUp.showPopup(mSelectedArcGISFeature, false);
        //        }
        //        if (mDialog != null && mDialog.isShowing()) {
        //            mDialog.dismiss();
        //        }
        if (values.isNotEmpty()) {
            val hoSoVatTuSuCoAsync = HoSoVatTuSuCoAsync(mActivity, object : HoSoVatTuSuCoAsync.AsyncResponse {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun processFinish(`object`: Any?) {
                    //Không kiểm tra object khác null, vì có thể sự cố đó chưa có vật tư
                    mApplication.arcGISFeature = values[0] as ArcGISFeature
                    mPopUp.showPopup()
                    if (mDialog != null && mDialog.isShowing) mDialog.dismiss()
                }
            })
            hoSoVatTuSuCoAsync.execute(Constant.HOSOSUCO_METHOD.FIND, mSelectedArcGISFeature!!.attributes[Constant.FIELD_SUCO.ID_SUCO])
        } else if (mDialog != null && mDialog.isShowing) {
            mDialog.dismiss()
        }
    }

    companion object {
        private val DELTA_MOVE_Y = 0.0//7000;
    }

}