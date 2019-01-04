package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask

import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication

/**
 * Created by ThanLe on 4/16/2018.
 */

class QueryServiceFeatureTableAsync(@field:SuppressLint("StaticFieldLeak")
                                    private val mActivity: Activity,
                                    private val mServiceFeatureTable: ServiceFeatureTable, @field:SuppressLint("StaticFieldLeak")
                                    private val mDelegate: AsyncResponse) : AsyncTask<QueryParameters, Feature?, Void?>() {
    private val mApplication: DApplication

    interface AsyncResponse {
        fun processFinish(output: Feature?)
    }

    init {
        this.mApplication = mActivity.application as DApplication
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: QueryParameters): Void? {
        try {
            if (params != null && params.size > 0) {


                val featureQueryResultListenableFuture = mServiceFeatureTable.queryFeaturesAsync(params[0], ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
                featureQueryResultListenableFuture.addDoneListener {
                    try {
                        val result = featureQueryResultListenableFuture.get()
                        val iterator = result.iterator()

                        if (iterator.hasNext()) {
                            val feature = iterator.next() as Feature
                            publishProgress(feature)
                        } else {
                            publishProgress()
                        }

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

    override fun onProgressUpdate(vararg values: Feature?) {
        if (values == null) {
            mDelegate.processFinish(null)
        } else if (values.size > 0)
            mDelegate.processFinish(values[0])
        else
            mDelegate.processFinish(null)
    }


}