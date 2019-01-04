package vn.ditagis.com.tanhoa.qlsc.services

import android.os.AsyncTask

import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import vn.ditagis.com.tanhoa.qlsc.entities.*

import java.util.ArrayList
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB


class GetThietBi(private val mApplication: DApplication, private val mDelegate: AsyncResponse) : AsyncTask<Void, Boolean, Void>() {

    interface AsyncResponse {
        fun processFinish()
    }


    private fun getVatTuFromService() {

        val layerInfoThietBi = Constant.IDLayer.ID_SU_CO_THIET_BI_TABLE

        for (dLayerInfo in mApplication.lstFeatureLayerDTG!!) {
            if (dLayerInfo.id == layerInfoThietBi) {
                val queryParameters = QueryParameters()
                queryParameters.whereClause = "1=1"
                var url = dLayerInfo.url
                if (!url.startsWith("http"))
                    url = "http:" + dLayerInfo.url
                val serviceFeatureTable = ServiceFeatureTable(url)
                //
                val feature = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
                feature.addDoneListener {
                    val thietBiList = ArrayList<ThietBi>()
                    try {
                        val result = feature.get()
                        val iterator = result.iterator()
                        var item: Feature

                        while (iterator.hasNext()) {
                            item = iterator.next()
                            val maThietBi = item.attributes[Constant.FieldThietBi.MA_THIET_BI] as String
                            val tenThietBi = item.attributes[Constant.FieldThietBi.TEN_THIET_BI] as String
                            val thietBi = ThietBi(maThietBi, tenThietBi)
                            thietBiList.add(thietBi)
                        }
                        ListObjectDB.instance.thietBis = thietBiList
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
        getVatTuFromService()
        return null
    }

    override fun onProgressUpdate(vararg values: Boolean?) {
        super.onProgressUpdate(*values)
        if (values.isNotEmpty() && values[0]!!)
            mDelegate.processFinish()
    }
}
