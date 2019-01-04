package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Build
import android.widget.Toast

import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Attachment
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.FeatureEditResult
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable

import java.util.Calendar
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB

/**
 * Created by ThanLe on 4/16/2018.
 */

class SingleTapAddFeatureAsync(@field:SuppressLint("StaticFieldLeak")
                               private val mActivity: Activity,
                               private val mServiceFeatureTable: ServiceFeatureTable, @field:SuppressLint("StaticFieldLeak")
                               private val mDelegate: AsyncResponse) : AsyncTask<Void, Feature?, Void?>() {
    private val mDialog: ProgressDialog?
    private val mApplication: DApplication

    interface AsyncResponse {
        fun processFinish(output: Feature?)
    }

    init {
        this.mApplication = mActivity.application as DApplication
        this.mDialog = ProgressDialog(mActivity, android.R.style.Theme_Material_Dialog_Alert)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mDialog!!.setMessage("Đang xử lý...")
        mDialog.setCancelable(false)
        mDialog.show()
    }

    override fun doInBackground(vararg aVoids: Void): Void? {
        val feature: Feature
        try {
            feature = mServiceFeatureTable.createFeature()
            feature.geometry = mApplication.getDiemSuCo.point
            feature.attributes[Constant.FIELD_SUCO.DIA_CHI] = mApplication.getDiemSuCo.vitri

            feature.attributes[Constant.FIELD_SUCO.GHI_CHU] = mApplication.getDiemSuCo.ghiChu
            feature.attributes[Constant.FIELD_SUCO.NGUOI_PHAN_ANH] = mApplication.getDiemSuCo.nguoiPhanAnh
            feature.attributes[Constant.FIELD_SUCO.EMAIL_NGUOI_PHAN_ANH] = mApplication.getDiemSuCo.emailPhanAnh
            feature.attributes[Constant.FIELD_SUCO.SDT] = mApplication.getDiemSuCo.sdtPhanAnh
            feature.attributes[Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN] = mApplication.getDiemSuCo.hinhThucPhatHien
            feature.attributes[Constant.FIELD_SUCO.DOI_TUONG_PHAT_HIEN] = Constant.Another.DOI_TUONG_PHAT_HIEN_CBCNV
            feature.attributes[Constant.FIELD_SUCO.KET_CAU_DUONG] = mApplication.getDiemSuCo.ketCauDuong


            //            if (mApplication.getDiemSuCo.getPhuiDaoDai() != null)
            //                feature.getAttributes().put(Constant.FIELD_SUCO.PHUI_DAO_1_DAI, mApplication.getDiemSuCo.getPhuiDaoDai());
            //            if (mApplication.getDiemSuCo.getPhuiDaoRong() != null)
            //                feature.getAttributes().put(Constant.FIELD_SUCO.PHUI_DAO_1_RONG, mApplication.getDiemSuCo.getPhuiDaoRong());
            //            if (mApplication.getDiemSuCo.getPhuiDaoSau() != null)
            //                feature.getAttributes().put(Constant.FIELD_SUCO.PHUI_DAO_1_SAU, mApplication.getDiemSuCo.getPhuiDaoSau());
            for (dLayerInfo in mApplication.lstFeatureLayerDTG!!)
                if (dLayerInfo.id == Constant.IDLayer.ID_BASEMAP) {
                    val serviceFeatureTableHanhChinh = ServiceFeatureTable(
                            dLayerInfo.url + Constant.Another.URL_BASEMAP)
                    val queryParameters = QueryParameters()
                    queryParameters.geometry = feature.geometry
                    QueryServiceFeatureTableAsync(mActivity, serviceFeatureTableHanhChinh,
                            object : QueryServiceFeatureTableAsync.AsyncResponse {
                                override fun processFinish(output: Feature?) {
                                    if (output != null) {
                                        val phuong = output.attributes[Constant.FIELD_HANHCHINH.ID_HANH_CHINH]
                                        val quan = output.attributes[Constant.FIELD_HANHCHINH.MA_HUYEN]
                                        if (quan != null) {
                                            feature.attributes[Constant.FIELD_SUCO.QUAN] = quan.toString()
                                        }
                                        if (phuong != null)
                                            feature.attributes[Constant.FIELD_SUCO.PHUONG] = phuong.toString()
                                    }
                                    addFeature(feature)
                                }
                            }).execute(queryParameters)
                    break
                }


        } catch (e: Exception) {
            publishProgress()
        }

        return null
    }

    private fun addFeature(feature: Feature) {
        GenerateIDSuCoByAPIAsycn(mActivity, object : GenerateIDSuCoByAPIAsycn.AsyncResponse {
            override fun processFinish(output: String?) {
                if (output != null && output.isEmpty()) {
                    publishProgress()
                    return
                }
                feature.attributes[Constant.FIELD_SUCO.ID_SUCO] = output
                val intObj = 0.toShort()
                feature.attributes[Constant.FIELD_SUCO.TRANG_THAI] = intObj
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val c = Calendar.getInstance()
                    feature.attributes[Constant.FIELD_SUCO.TGPHAN_ANH] = c
                }
                //            if (mApplication.getUserDangNhap().getGroupRole().equals(Constant.GROUPROLE_TC)) {
                feature.attributes[Constant.FIELD_SUCO.TRANG_THAI_THI_CONG] = Constant.TRANG_THAI_SU_CO.CHUA_XU_LY
                feature.attributes[Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN_THI_CONG] = mApplication.getDiemSuCo.hinhThucPhatHien

                //            } else if (mApplication.getUserDangNhap().getGroupRole().equals(Constant.GROUPROLE_GS)) {
                feature.attributes[Constant.FIELD_SUCO.TRANG_THAI_GIAM_SAT] = Constant.TRANG_THAI_SU_CO.CHUA_XU_LY
                feature.attributes[Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN_GIAM_SAT] = mApplication.getDiemSuCo.hinhThucPhatHien
                //            }
                mServiceFeatureTable.addFeatureAsync(feature).addDoneListener {
                    val listListenableEditAsync = mServiceFeatureTable.applyEditsAsync()
                    listListenableEditAsync.addDoneListener {
                        try {
                            val featureEditResults = listListenableEditAsync.get()
                            if (featureEditResults.size > 0) {
                                addServiceFeatureTable(feature as ArcGISFeature, feature)
                            } else
                                publishProgress()
                        } catch (e: InterruptedException) {
                            publishProgress()
                            e.printStackTrace()
                        } catch (e: ExecutionException) {
                            publishProgress()
                            e.printStackTrace()
                        }


                    }
                }
            }
        }).execute(Constant.URL_API.GENERATE_ID_SUCO)
    }

    private fun addServiceFeatureTable(arcGISFeature: ArcGISFeature, feature: Feature) {
        val serviceFeatureTable = mApplication.getDFeatureLayer.serviceFeatureTableSuCoThongTin
        serviceFeatureTable!!.loadAsync()
        serviceFeatureTable.addDoneLoadingListener {
            val idSuCo = feature.attributes[Constant.FIELD_SUCO.ID_SUCO].toString()
            GenerateIDSuCoByAPIAsycn(mActivity, object : GenerateIDSuCoByAPIAsycn.AsyncResponse {
                override fun processFinish(output: String?) {
                    if (output != null) {

                        val suCoThongTinFeature = serviceFeatureTable.createFeature()
                        suCoThongTinFeature.attributes[Constant.FIELD_SUCOTHONGTIN.ID_SUCO] = idSuCo
                        suCoThongTinFeature.attributes[Constant.FIELD_SUCOTHONGTIN.ID_SUCOTT] = output
                        suCoThongTinFeature.attributes[Constant.FIELD_SUCOTHONGTIN.TRANG_THAI] = 0.toShort()
                        suCoThongTinFeature.attributes[Constant.FIELD_SUCOTHONGTIN.NHAN_VIEN] = mApplication.userDangNhap!!.userName
                        suCoThongTinFeature.attributes[Constant.FIELD_SUCOTHONGTIN.HINH_THUC_PHAT_HIEN] = mApplication.getDiemSuCo.hinhThucPhatHien
                        suCoThongTinFeature.attributes[Constant.FIELD_SUCOTHONGTIN.DIA_CHI] = mApplication.getDiemSuCo.vitri
                        suCoThongTinFeature.attributes[Constant.FIELD_SUCOTHONGTIN.GHI_CHU] = mApplication.getDiemSuCo.ghiChu
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            val c = Calendar.getInstance()
                            suCoThongTinFeature.attributes[Constant.FIELD_SUCOTHONGTIN.TG_CAP_NHAT] = c
                        }
                        suCoThongTinFeature.attributes[Constant.FIELD_SUCOTHONGTIN.DON_VI] = mApplication.userDangNhap!!.role
                        serviceFeatureTable.addFeatureAsync(suCoThongTinFeature).addDoneListener {
                            val listListenableFuture = serviceFeatureTable.applyEditsAsync()
                            listListenableFuture.addDoneListener {
                                try {
                                    val featureEditResults = listListenableFuture.get()
                                    if (featureEditResults.size > 0) {
                                        val queryParameters = QueryParameters()
                                        //                            final String query = String.format(mActivity.getString(R.string.arcgis_query_by_OBJECTID), objectId);
                                        val query = String.format("%s = '%s'", Constant.FIELD_SUCOTHONGTIN.ID_SUCOTT, output)
                                        queryParameters.whereClause = query
                                        val featuresAsync = serviceFeatureTable
                                                .queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.IDS_ONLY)
                                        featuresAsync.addDoneListener {
                                            try {
                                                val result = featuresAsync.get()
                                                if (result.iterator().hasNext()) {
                                                    val item = result.iterator().next()
                                                    addAttachment(item, serviceFeatureTable)
                                                }
                                            } catch (e: InterruptedException) {
                                                e.printStackTrace()
                                                publishProgress()
                                            } catch (e: ExecutionException) {
                                                e.printStackTrace()
                                                publishProgress()
                                            }


                                        }
                                        //                                    addAttachment(arcGISFeature, feature);
                                        //                                    publishProgress(feature);
                                    } else
                                        publishProgress()
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                    publishProgress()
                                } catch (e: ExecutionException) {
                                    e.printStackTrace()
                                    publishProgress()
                                }
                            }
                        }
                        //                    addAttachment(arcGISFeature, feature);
                        //                    publishProgress(feature);
                    }
                }

            }).execute(Constant.URL_API.GENERATE_ID_SUCOTHONGTIN + idSuCo)
        }
    }

    private fun addAttachment(feature: Feature, serviceFeatureTable: ServiceFeatureTable?) {
        val arcGISFeature = feature as ArcGISFeature
        val attachmentName = mApplication.applicationContext.getString(R.string.attachment_add) + "_" + System.currentTimeMillis() + ".png"
        val addResult = arcGISFeature.addAttachmentAsync(
                mApplication.getDiemSuCo.image, Bitmap.CompressFormat.PNG.toString(), attachmentName)
        addResult.addDoneListener {
            //            if (mDialog != null && mDialog.isShowing()) {
            //                mDialog.dismiss();
            //            }
            try {
                val attachment = addResult.get()
                if (attachment.size > 0) {
                    val tableResult = serviceFeatureTable!!.updateFeatureAsync(feature)
                    tableResult.addDoneListener {
                        val updatedServerResult = serviceFeatureTable.applyEditsAsync()
                        updatedServerResult.addDoneListener {
                            val edits: List<FeatureEditResult>
                            try {
                                edits = updatedServerResult.get()
                                if (edits.size > 0) {
                                    if (!edits[0].hasCompletedWithErrors()) {
                                        publishProgress(feature)
                                    }
                                }
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                                publishProgress()
                            } catch (e: ExecutionException) {
                                e.printStackTrace()
                                publishProgress()
                            }
                        }
                    }
                } else {
                    publishProgress()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }
    }

    override fun onProgressUpdate(vararg values: Feature?) {
        if (values == null) {
            Toast.makeText(mActivity.applicationContext, "Không phản ánh được sự cố. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show()
            mDelegate.processFinish(null)
        } else if (values.size > 0) {
            mDelegate.processFinish(values[0])
            mApplication.getDiemSuCo.clear()
        }
        if (mDialog != null && mDialog.isShowing) {
            mDialog.dismiss()
        }
    }


}