package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.loadable.LoadStatus

import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB


class HoSoVatTuSuCoAsync(@field:SuppressLint("StaticFieldLeak")
                         private val mActivity: Activity, private val mDelegate: AsyncResponse) : AsyncTask<Any, Any?, Boolean?>() {
    private val mServiceFeatureTable: ServiceFeatureTable?
    private val mApplication: DApplication = mActivity.application as DApplication
    private val mLoaiVatTu: Short

    interface AsyncResponse {
        fun processFinish(`object`: Any?)
    }

    init {
        mLoaiVatTu = mApplication.loaiVatTu
        mServiceFeatureTable = mApplication.getDFeatureLayer.serviceFeatureTableHoSoVatTuSuCo
    }

    private fun find(idSuCo: String) {
        val queryParameters = QueryParameters()
        val hoSoVatTuSuCos = ArrayList<HoSoVatTuSuCo>()

        val queryClause = String.format("%s like '%%%s%%' and %s = %d", Constant.FIELD_VATTU.ID_SU_CO, idSuCo,
                Constant.FIELD_VATTU.LOAI_VAT_TU, mLoaiVatTu)
        queryParameters.whereClause = queryClause
        val queryResultListenableFuture = this.mServiceFeatureTable!!.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        queryResultListenableFuture.addDoneListener {
            try {
                val result = queryResultListenableFuture.get()
                if (result.iterator().hasNext()) {
                    val query = StringBuilder()
                    val tempHoSoVatTuSuCo = ArrayList<HoSoVatTuSuCo>()
                    val it = result.iterator()
                    while (it.hasNext()) {
                        val feature = it.next() as Feature
                        val attributes = feature.attributes
                        val maVatTu = attributes[Constant.FIELD_VATTU.MA_VAT_TU].toString()
                        query.append(String.format("%s = '%s' or ", Constant.FIELD_VATTU.MA_VAT_TU, maVatTu))
                        tempHoSoVatTuSuCo.add(HoSoVatTuSuCo(attributes[Constant.FIELD_VATTU.ID_SU_CO].toString(),
                                java.lang.Double.parseDouble(attributes[Constant.FIELD_VATTU.SO_LUONG].toString()),
                                maVatTu,
                                "",
                                ""))
                    }
                    query.append("1 = 0")
                    for (dLayerInfo in mApplication.lstFeatureLayerDTG!!) {
                        if (dLayerInfo.id == Constant.IDLayer.ID_VAT_TU_TABLE) {
                            val queryParametersVatTu = QueryParameters()
                            queryParametersVatTu.whereClause = query.toString()
                            var url = dLayerInfo.url
                            if (!url.startsWith("http"))
                                url = "http:" + dLayerInfo.url
                            val serviceFeatureTableVatTu = ServiceFeatureTable(url)
                            //
                            val featuresAsyncVatTu = serviceFeatureTableVatTu.queryFeaturesAsync(queryParametersVatTu,
                                    ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
                            featuresAsyncVatTu.addDoneListener {
                                try {
                                    val features = featuresAsyncVatTu.get()
                                    val iterator = features.iterator()
                                    var item: Feature
                                    while (iterator.hasNext()) {
                                        item = iterator.next()
                                        for (hoSoVatTuSuCo in tempHoSoVatTuSuCo) {
                                            if (hoSoVatTuSuCo.maVatTu == item.attributes[Constant.FIELD_VATTU.MA_VAT_TU].toString()) {
                                                hoSoVatTuSuCos.add(HoSoVatTuSuCo(hoSoVatTuSuCo.idSuCo,
                                                        hoSoVatTuSuCo.soLuong,
                                                        hoSoVatTuSuCo.maVatTu,
                                                        item.attributes[Constant.FIELD_VATTU.TEN_VAT_TU].toString(),
                                                        item.attributes[Constant.FIELD_VATTU.DON_VI_TINH].toString()))
                                            }
                                        }
                                    }
                                    ListObjectDB.instance.setHoSoVatTuSuCos(hoSoVatTuSuCos)
                                    publishProgress(hoSoVatTuSuCos)
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                    publishProgress(hoSoVatTuSuCos)
                                } catch (e: ExecutionException) {
                                    e.printStackTrace()
                                    publishProgress(hoSoVatTuSuCos)
                                }


                            }

                            break
                        }
                    }

                } else {

                    publishProgress(hoSoVatTuSuCos)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
                publishProgress(hoSoVatTuSuCos)
            } catch (e: ExecutionException) {
                e.printStackTrace()
                publishProgress(hoSoVatTuSuCos)
            }
        }
    }

    private fun delete(idSuCo: String) {
        val queryParameters = QueryParameters()
        val queryClause = String.format("%s like '%%%s%%' and %s = %d", Constant.FIELD_VATTU.ID_SU_CO, idSuCo,
                Constant.FIELD_VATTU.LOAI_VAT_TU, mLoaiVatTu)
        queryParameters.whereClause = queryClause
        val queryResultListenableFuture = this.mServiceFeatureTable!!.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        queryResultListenableFuture.addDoneListener {
            try {
                val result = queryResultListenableFuture.get()

                mServiceFeatureTable.deleteFeaturesAsync(result).addDoneListener {
                    val listListenableFuture = mServiceFeatureTable.applyEditsAsync()
                    listListenableFuture.addDoneListener {
                        ListObjectDB.instance.clearHoSoVatTuSuCos()
                        //Không cần kiểm tra xóa thành công,
                        // bởi vì có thể chưa có vật tư trước đó
                        insert(ListObjectDB.instance.getLstHoSoVatTuSuCoInsert()!!)
                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
                publishProgress(false)
            } catch (e: ExecutionException) {
                e.printStackTrace()
                publishProgress(false)
            }
        }
    }


    private fun insert(hoSoVatTuSuCos: List<HoSoVatTuSuCo>) {
        val features = ArrayList<Feature>()
        for (hoSoVatTuSuCo in hoSoVatTuSuCos) {
            val attributes = HashMap<String, Any>()
            attributes[Constant.FIELD_VATTU.ID_SU_CO] = hoSoVatTuSuCo.idSuCo
            attributes[Constant.FIELD_VATTU.MA_VAT_TU] = hoSoVatTuSuCo.maVatTu
            attributes[Constant.FIELD_VATTU.SO_LUONG] = hoSoVatTuSuCo.soLuong

            val feature = mServiceFeatureTable!!.createFeature()
            feature.attributes[Constant.FIELD_VATTU.ID_SU_CO] = hoSoVatTuSuCo.idSuCo
            feature.attributes[Constant.FIELD_VATTU.MA_VAT_TU] = hoSoVatTuSuCo.maVatTu
            feature.attributes[Constant.FIELD_VATTU.SO_LUONG] = hoSoVatTuSuCo.soLuong
            feature.attributes[Constant.FIELD_VATTU.LOAI_VAT_TU] = mLoaiVatTu
            features.add(feature)
        }
        mServiceFeatureTable!!.addFeaturesAsync(features).addDoneListener {
            val listListenableFuture = mServiceFeatureTable.applyEditsAsync()
            listListenableFuture.addDoneListener {
                try {
                    val featureEditResults = listListenableFuture.get()
                    if (featureEditResults.size > 0) {
                        ListObjectDB.instance.clearListHoSoVatTuSuCoChange()
                        publishProgress(true)
                    } else {
                        publishProgress(false)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    publishProgress(false)
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                    publishProgress(false)
                }
            }

        }


    }

    override fun doInBackground(vararg objects: Any): Boolean? {
        mServiceFeatureTable!!.loadAsync()
        mServiceFeatureTable.addDoneLoadingListener {
            if (mServiceFeatureTable.loadStatus == LoadStatus.LOADED) {
                if (objects.isNotEmpty()) {
                    when (Integer.parseInt(objects[0].toString())) {
                        Constant.HOSOSUCO_METHOD.FIND -> if (objects.size > 1 && objects[1] is String) {
                            find(objects[1].toString())
                        }
                        Constant.HOSOSUCO_METHOD.INSERT -> if (objects.size > 1 && objects[1] is String) {
                            delete(objects[1].toString())
                        }
                    }
                }
            } else {
                publishProgress(false)
                Log.e("Load table", "không loaded")
            }
        }

        return null
    }

    override fun onProgressUpdate(vararg values: Any?) {
        super.onProgressUpdate(*values)

        this.mDelegate.processFinish(values[0])
    }
}
