package vn.ditagis.com.tanhoa.qlsc.async

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.LinearLayout
import android.widget.TextView

import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.ArcGISFeatureTable
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult
import com.esri.arcgisruntime.mapping.view.MapView
import vn.ditagis.com.tanhoa.qlsc.R
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.utities.Popup

/**
 * Created by ThanLe on 4/16/2018.
 */

class SingleTapMapViewAsync(private val mActivity: Activity, private val mPopUp: Popup, private val mClickPoint: android.graphics.Point, private val mMapView: MapView)
    : AsyncTask<Point, Feature?, Void?>() {
    private var mDialog: Dialog? = null
    private var mSelectedArcGISFeature: ArcGISFeature? = null
    private var isFound = false
    private val mApplication: DApplication

    init {
        this.mApplication = mActivity.application as DApplication
    }

    override fun onPreExecute() {
        super.onPreExecute()

        val layout = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null) as LinearLayout
        val txtTitle = layout.findViewById<TextView>(R.id.txt_progress_dialog_title)
        txtTitle.text = "Đang lấy thông tin..."
        mDialog = Dialog(mActivity)
        mDialog!!.setCancelable(false)
        mDialog!!.setContentView(layout)
        mDialog!!.show()
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
                Constant.FieldSuCoThongTin.ID_SUCO, mSelectedArcGISFeature!!.attributes[Constant.FieldSuCo.ID_SUCO].toString(),
                Constant.FieldSuCoThongTin.NHAN_VIEN, mApplication.userDangNhap!!.userName)
        val queryParameters = QueryParameters()
        queryParameters.whereClause = queryClause
        QueryServiceFeatureTableAsync(mActivity, mApplication.getDFeatureLayer.serviceFeatureTableSuCoThongTin!!,
                object : QueryServiceFeatureTableAsync.AsyncResponse {
                    override fun processFinish(output: Feature?) {
                        if (output != null) publishProgress(output)
                    }
                }).execute(queryParameters)
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
                    if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
                }
            })
            hoSoVatTuSuCoAsync.execute(Constant.HoSoSuCoMethod.FIND, mSelectedArcGISFeature!!.attributes[Constant.FieldSuCo.ID_SUCO])
        } else if (mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
        }
    }

    companion object {
        private val DELTA_MOVE_Y = 0.0//7000;
    }

}