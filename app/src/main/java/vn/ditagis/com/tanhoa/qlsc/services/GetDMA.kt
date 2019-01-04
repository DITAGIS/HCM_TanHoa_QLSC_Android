package vn.ditagis.com.tanhoa.qlsc.services

import android.os.AsyncTask

import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable

import java.util.ArrayList
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB


class GetDMA(private val mApplication: DApplication, private val mDelegate: AsyncResponse) : AsyncTask<Void, Boolean, Void>() {

    interface AsyncResponse {

        fun processFinish()
    }


    private fun getMaDMAFromService() {

        val layerInfoVatTu = Constant.IDLayer.DMA
        for (dLayerInfo in mApplication.lstFeatureLayerDTG!!) {
            if (dLayerInfo.id == layerInfoVatTu) {
                val queryParameters = QueryParameters()
                queryParameters.whereClause = "1=1"
                var url = dLayerInfo.url
                if (!url.startsWith("http"))
                    url = "http:" + dLayerInfo.url
                val serviceFeatureTable = ServiceFeatureTable(url)
                //
                val feature = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
                feature.addDoneListener {
                    val dmaList = ArrayList<String>()
                    try {
                        val result = feature.get()
                        val iterator = result.iterator()
                        var item: Feature
                        while (iterator.hasNext()) {
                            item = iterator.next()
                            val dma = item.attributes[Constant.FieldDMA.MA_DMA]
                            if (dma != null)
                                dmaList.add(dma.toString())
                        }
                        ListObjectDB.instance.dmas = dmaList
                        publishProgress(true)

                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        publishProgress()
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                        publishProgress()
                    }


                }

                break
            }
        }

    }


    override fun doInBackground(vararg voids: Void): Void? {
        getMaDMAFromService()
        return null
    }

    override fun onProgressUpdate(vararg values: Boolean?) {
        super.onProgressUpdate(*values)
        if (values.isNotEmpty() && values[0]!!)
            mDelegate.processFinish()
    }
}
