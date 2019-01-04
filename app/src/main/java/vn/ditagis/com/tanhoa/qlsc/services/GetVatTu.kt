package vn.ditagis.com.tanhoa.qlsc.services

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast

import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable

import java.util.ArrayList
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo
import vn.ditagis.com.tanhoa.qlsc.entities.VatTu
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB
import java.lang.Exception


class GetVatTu(private val mApplication: DApplication, private val mDelegate: AsyncResponse) : AsyncTask<Void, Boolean, Void>() {

    interface AsyncResponse {
        fun processFinish()
    }


    private fun getVatTuFromService() {

        val layerInfoVatTu = Constant.IDLayer.ID_VAT_TU_TABLE

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
                    val vatTuList = ArrayList<VatTu>()
                    try {
                        val result = feature.get()
                        val iterator = result.iterator()
                        var item: Feature
                        while (iterator.hasNext()) {
                            item = iterator.next()
                            val maVatTu = item.attributes[Constant.FIELD_VATTU.MA_VAT_TU] as String
                            val tenVatTu = item.attributes[Constant.FIELD_VATTU.TEN_VAT_TU] as String
                            val donViTinh = item.attributes[Constant.FIELD_VATTU.DON_VI_TINH] as String
                            val vatTu = VatTu(maVatTu, tenVatTu, donViTinh)
                            vatTuList.add(vatTu)
                        }
                        ListObjectDB.instance.vatTus = vatTuList
                        publishProgress(true)

                    } catch (e: InterruptedException) {
                        publishProgress()
                    } catch (e: ExecutionException) {
                        publishProgress()
                    }


                }

                break
            }
        }

    }


    override fun doInBackground(vararg voids: Void): Void? {
        try {
            getVatTuFromService()
        } catch (ex: Exception) {
//            Toast.makeText(mApplication, "Có lỗi xảy ra khi lấy vật tư", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    override fun onProgressUpdate(vararg values: Boolean?) {
        super.onProgressUpdate(*values)
        if (values.isNotEmpty() && values[0]!!)
            mDelegate.processFinish()
    }
}
