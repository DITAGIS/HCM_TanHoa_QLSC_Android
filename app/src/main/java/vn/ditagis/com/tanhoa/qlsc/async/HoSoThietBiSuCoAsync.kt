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
import java.util.concurrent.ExecutionException
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoThietBiSuCo
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB


class HoSoThietBiSuCoAsync(@field:SuppressLint("StaticFieldLeak")
                           private val mActivity: Activity, private val mDelegate: AsyncResponse) : AsyncTask<Any, Any, Boolean?>() {
    private val mServiceFeatureTable: ServiceFeatureTable?
    private val mApplication: DApplication = mActivity.application as DApplication

    interface AsyncResponse {
        fun processFinish(`object`: Any?)
    }

    init {
        mServiceFeatureTable = mApplication.getDFeatureLayer.serviceFeatureTableHoSoThietBiSuCo
    }

    private fun find(idSuCo: String) {
        val queryParameters = QueryParameters()
        val hoSoThietBiSuCos = ArrayList<HoSoThietBiSuCo>()

        val queryClause = String.format("%s like '%%%s%%'", Constant.FIELD_THIETBI.ID_SU_CO, idSuCo)
        queryParameters.whereClause = queryClause
        val queryResultListenableFuture = this.mServiceFeatureTable!!.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        queryResultListenableFuture.addDoneListener {
            try {
                val result = queryResultListenableFuture.get()
                if (result.iterator().hasNext()) {
                    val query = StringBuilder()
                    val tempHoSoThietBiSuCos = ArrayList<HoSoThietBiSuCo>()
                    val it = result.iterator()
                    while (it.hasNext()) {
                        val feature = it.next() as Feature
                        val attributes = feature.attributes
                        val maThietBi = attributes[Constant.FIELD_THIETBI.MA_THIET_BI].toString()
                        query.append(String.format("%s = '%s' or ", Constant.FIELD_THIETBI.MA_THIET_BI, maThietBi))
                        tempHoSoThietBiSuCos.add(HoSoThietBiSuCo(attributes[Constant.FIELD_THIETBI.ID_SU_CO].toString(),
                                java.lang.Double.parseDouble(attributes[Constant.FIELD_THIETBI.THOI_GIAN_VAN_HANH].toString()),
                                maThietBi, ""))
                    }
                    query.append("1 = 0")
                    for (dLayerInfo in mApplication.lstFeatureLayerDTG!!) {
                        if (dLayerInfo.id == Constant.IDLayer.ID_SU_CO_THIET_BI_TABLE) {
                            val queryParametersThietBi = QueryParameters()
                            queryParametersThietBi.whereClause = query.toString()
                            var url = dLayerInfo.url
                            if (!url.startsWith("http"))
                                url = "http:" + dLayerInfo.url
                            val serviceFeatureTableThietBi = ServiceFeatureTable(url)
                            //
                            val featuresAsyncThietBi = serviceFeatureTableThietBi.queryFeaturesAsync(queryParametersThietBi,
                                    ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
                            featuresAsyncThietBi.addDoneListener {
                                try {
                                    val features = featuresAsyncThietBi.get()
                                    val iterator = features.iterator()
                                    var item: Feature
                                    while (iterator.hasNext()) {
                                        item = iterator.next()
                                        for (hoSoThietBiSuCo in tempHoSoThietBiSuCos) {
                                            if (hoSoThietBiSuCo.maThietBi == item.attributes[Constant.FIELD_THIETBI.MA_THIET_BI].toString()) {
                                                hoSoThietBiSuCos.add(HoSoThietBiSuCo(hoSoThietBiSuCo.idSuCo,
                                                        hoSoThietBiSuCo.thoigianVanHanh,
                                                        hoSoThietBiSuCo.maThietBi,
                                                        item.attributes[Constant.FIELD_THIETBI.TEN_THIET_BI].toString()))
                                            }
                                        }

                                    }
                                    ListObjectDB.instance.setHoSoThietBiSuCos(hoSoThietBiSuCos)
                                    publishProgress(hoSoThietBiSuCos)
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                    publishProgress(hoSoThietBiSuCos)
                                } catch (e: ExecutionException) {
                                    e.printStackTrace()
                                    publishProgress(hoSoThietBiSuCos)
                                }


                            }

                            break
                        }
                    }

                } else {

                    publishProgress(hoSoThietBiSuCos)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
                publishProgress(hoSoThietBiSuCos)
            } catch (e: ExecutionException) {
                e.printStackTrace()
                publishProgress(hoSoThietBiSuCos)
            }
        }
    }

    private fun delete(idSuCo: String) {
        val queryParameters = QueryParameters()
        val queryClause = String.format("%s like '%%%s%%'", Constant.FIELD_THIETBI.ID_SU_CO, idSuCo)
        queryParameters.whereClause = queryClause
        val queryResultListenableFuture = this.mServiceFeatureTable!!.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        queryResultListenableFuture.addDoneListener {
            try {
                val result = queryResultListenableFuture.get()

                mServiceFeatureTable.deleteFeaturesAsync(result).addDoneListener {
                    val listListenableFuture = mServiceFeatureTable.applyEditsAsync()
                    listListenableFuture.addDoneListener {
                        ListObjectDB.instance.clearHoSoThietBiSuCos()
                        //Không cần kiểm tra xóa thành công,
                        // bởi vì có thể chưa có vật tư trước đó
                        insert(ListObjectDB.instance.getLstHoSoThietBiSuCoInsert()!!)
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


    private fun insert(hoSoThietBiSuCos: List<HoSoThietBiSuCo>) {
        val features = ArrayList<Feature>()
        for (hoSoThietBiSuCo in hoSoThietBiSuCos) {
            //            Map<String, Object> attributes = new HashMap<>();
            //            attributes.put(Constant.FIELD_THIETBI.ID_SU_CO, hoSoThietBiSuCo.getIdSuCo());
            //            attributes.put(Constant.FIELD_THIETBI.MA_THIET_BI, hoSoThietBiSuCo.getMaThietBi());
            //            attributes.put(Constant.FIELD_THIETBI.THOI_GIAN_VAN_HANH,(int) hoSoThietBiSuCo.getThoigianVanHanh());

            val feature = mServiceFeatureTable!!.createFeature()
            feature.attributes[Constant.FIELD_THIETBI.ID_SU_CO] = hoSoThietBiSuCo.idSuCo
            feature.attributes[Constant.FIELD_THIETBI.MA_THIET_BI] = hoSoThietBiSuCo.maThietBi
            feature.attributes[Constant.FIELD_THIETBI.THOI_GIAN_VAN_HANH] = hoSoThietBiSuCo.thoigianVanHanh
            features.add(feature)
        }
        mServiceFeatureTable!!.addFeaturesAsync(features).addDoneListener {
            val listListenableFuture = mServiceFeatureTable.applyEditsAsync()
            listListenableFuture.addDoneListener {
                try {
                    val featureEditResults = listListenableFuture.get()
                    if (featureEditResults.size > 0) {
                        ListObjectDB.instance.clearListHoSoThietBiSuCoChange()
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

    override fun onProgressUpdate(vararg values: Any) {
        super.onProgressUpdate(*values)

        this.mDelegate.processFinish(values[0])
    }
}
