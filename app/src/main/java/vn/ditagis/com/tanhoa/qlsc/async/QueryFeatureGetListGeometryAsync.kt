package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.AsyncTask
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Geometry

import java.util.ArrayList
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication

/**
 * Created by ThanLe on 4/16/2018.
 */

class QueryFeatureGetListGeometryAsync(@field:SuppressLint("StaticFieldLeak")
                                       private val mActivity: Activity, private val mServiceFeatureTable: ServiceFeatureTable,
                                       @field:SuppressLint("StaticFieldLeak")
                                       private val mDelegate: AsyncResponse) : AsyncTask<List<String>, List<Geometry>?, Void?>() {
    private val mApplication: DApplication
    private var mDialog: AlertDialog? = null

    interface AsyncResponse {
        fun processFinish(output: List<Geometry>?)
    }

    init {
        this.mApplication = mActivity.application as DApplication
    }

    override fun onPreExecute() {
        super.onPreExecute()
        val layout = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null) as LinearLayout
        val txtTitle = layout.findViewById<TextView>(R.id.txt_progress_dialog_title)
        txtTitle.text = "Đang lấy danh sách công việc..."
        val builder = AlertDialog.Builder(mActivity)
        builder.setCancelable(false)
        builder.setView(layout)

        mDialog = builder.create()
//        mDialog!!.show()
        val window = mDialog!!.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(mDialog!!.window!!.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            mDialog!!.window!!.attributes = layoutParams
        }
        //        mDialog = new ProgressDialog(mActivity);
        //        mDialog.setCancelable(false);
        //        mDialog.setTitle(R.string.message_title_list_task);
        //        mDialog.show();

    }

    @SafeVarargs
    override fun doInBackground(vararg lists: List<String>): Void? {
        try {
            if (lists != null && lists.size > 0) {
                val builder = StringBuilder()
                builder.append(String.format("%s in (", Constant.FieldSuCo.ID_SUCO))
                for (idSuCo in lists[0]) {
                    builder.append(String.format("'%s' ,", idSuCo))
                }
                builder.append("'')")
                val queryParameters = QueryParameters()
                queryParameters.whereClause = builder.toString()

                val featureQueryResultListenableFuture = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
                featureQueryResultListenableFuture.addDoneListener {
                    try {
                        val result = featureQueryResultListenableFuture.get()
                        val iterator = result.iterator()
                        var item: Feature
                        val geometries = ArrayList<Geometry>()
                        while (iterator.hasNext()) {
                            item = iterator.next()

                            geometries.add(item.geometry)
                        }
                        publishProgress(geometries)

                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        publishProgress()
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                        publishProgress()
                    }
                }
            } else
                publishProgress()
        } catch (e: Exception) {
            publishProgress()
        }

        return null
    }

    override fun onProgressUpdate(vararg values: List<Geometry>?) {
        if (values == null) {
            mDelegate.processFinish(null)
        } else if (values.size > 0)
            mDelegate.processFinish(values[0])
        else
            mDelegate.processFinish(null)

        if (mDialog != null && mDialog!!.isShowing)
            mDialog!!.dismiss()
    }

}