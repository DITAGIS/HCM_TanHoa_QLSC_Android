package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi
import com.esri.arcgisruntime.data.*
import java.text.ParseException
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAdapter
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo
/**
 * Created by ThanLe on 4/16/2018.
 */

class EditAsync @RequiresApi(api = Build.VERSION_CODES.O)
constructor(@field:SuppressLint("StaticFieldLeak")
            private val mActivity: Activity,
            selectedArcGISFeature: ArcGISFeature, private val isUpdateAttachment: Boolean, private val mImage: ByteArray?,
            private val mHoSoVatTuSuCos: List<HoSoVatTuSuCo>, hoSoVatTuThuHoi_suCos: List<HoSoVatTuSuCo>, private val mDelegate: AsyncResponse)
    : AsyncTask<FeatureViewMoreInfoAdapter, ArcGISFeature, Boolean?>() {
    private val mDialog: ProgressDialog?
    private val mServiceFeatureTableSuCoThongTin: ServiceFeatureTable?
    private val mServiceFeatureTableSuCo: ServiceFeatureTable
    private var mSelectedArcGISFeature: ArcGISFeature? = null
    private val mApplication: DApplication

    interface AsyncResponse {
        fun processFinish(feature: ArcGISFeature?)
    }

    init {
        mApplication = mActivity.application as DApplication
        mServiceFeatureTableSuCoThongTin = mApplication.getDFeatureLayer.serviceFeatureTableSuCoThongTin
        mServiceFeatureTableSuCo = mApplication.getDFeatureLayer.layer!!.featureTable as ServiceFeatureTable
        mSelectedArcGISFeature = selectedArcGISFeature
        mDialog = ProgressDialog(mActivity, android.R.style.Theme_Material_Dialog_Alert)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mDialog!!.setMessage(mActivity.getString(R.string.async_dang_xu_ly))
        mDialog.setCancelable(false)
        mDialog.show()

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun doInBackground(vararg params: FeatureViewMoreInfoAdapter): Boolean? {
        val queryServiceFeatureTableAsync = QueryServiceFeatureTableAsync(
                mActivity, mApplication.getDFeatureLayer.serviceFeatureTableSuCoThongTin!!,
                object : QueryServiceFeatureTableAsync.AsyncResponse {
                    override fun processFinish(output: Feature?) {

                        val arcGISFeatureSuCoThongTin = output as ArcGISFeature
                        val adapter = params[0]
                        mDialog!!.max = adapter.count
                        val c = arrayOf(Calendar.getInstance())

                        var loaiSuCo = ""
                        var loaiSuCoShort: Short = 0
                        var trangThai = ""
                        var hasDomain = false
                        for (item in adapter.dItems!!) {
                            if (item.fieldName == Constant.FieldSuCo.LOAI_SU_CO) {
                                loaiSuCo = item.value!!

                            } else if (item.fieldName == Constant.FieldSuCo.TRANG_THAI)
                                trangThai = item.value!!
                        }
                        val featureTypes = arcGISFeatureSuCoThongTin.featureTable.featureTypes
                        val idFeatureTypes = getIdFeatureTypes(featureTypes, loaiSuCo)
                        if (idFeatureTypes != null) {

                            loaiSuCoShort = java.lang.Short.parseShort(idFeatureTypes.toString())
                            //            mSelectedArcGISFeature.getAttributes().put(mActivity.getString(R.string.Field_SuCo_LoaiSuCo), loaiSuCoShort);
                        }
                        //        mSelectedArcGISFeature.getAttributes().put("DuongKinhOng",Short.parseShort(("1")));
//                        val finalLoaiSuCo = loaiSuCo
                        //todo loaiSuCo - 1 chưa rõ nguyên nhân
                        val finalLoaiSuCoShort = loaiSuCoShort
                        val finalTrangThai = trangThai

                        for (item in adapter.dItems!!) {
                            if (item.value == null || !item.isEdit || !item.isEdited) continue
                            val domain = arcGISFeatureSuCoThongTin.featureTable.getField(item.fieldName).domain
                            var codeDomain: Any? = null
                            if (domain != null) {
                                hasDomain = true
                                //Trường hợp nguyên nhân, không tự động lấy được domain
                                when (item.fieldName) {
                                    Constant.FieldSuCo.NGUYEN_NHAN -> if (finalLoaiSuCoShort == Constant.LoaiSuCo.LOAISUCO_ONGNGANH || finalLoaiSuCoShort == Constant.LoaiSuCo.LOAISUCO_ONGCHINH) {
                                        val codedValues = (arcGISFeatureSuCoThongTin.featureTable.featureTypes[finalLoaiSuCoShort - 1].domains[Constant.FieldSuCo.NGUYEN_NHAN] as CodedValueDomain).codedValues
                                        if (codedValues != null) {
                                            for (codedValue in codedValues) {
                                                if (codedValue.name == item.value) {
                                                    codeDomain = codedValue.code
                                                    break
                                                }
                                            }
                                        }
                                    }
                                    //Trường hợp vật liệu, không tự động lấy được domain
                                    Constant.FieldSuCo.VAT_LIEU -> if (finalLoaiSuCoShort == Constant.LoaiSuCo.LOAISUCO_ONGNGANH || finalLoaiSuCoShort == Constant.LoaiSuCo.LOAISUCO_ONGCHINH) {
                                        val codedValues = (arcGISFeatureSuCoThongTin.featureTable.featureTypes[finalLoaiSuCoShort - 1].domains[Constant.FieldSuCo.VAT_LIEU] as CodedValueDomain).codedValues
                                        if (codedValues != null) {
                                            for (codedValue in codedValues) {
                                                if (codedValue.name == item.value) {
                                                    codeDomain = codedValue.code
                                                    break
                                                }
                                            }
                                        }
                                    }
                                    Constant.FieldSuCo.DUONG_KINH_ONG -> if (finalLoaiSuCoShort == Constant.LoaiSuCo.LOAISUCO_ONGNGANH || finalLoaiSuCoShort == Constant.LoaiSuCo.LOAISUCO_ONGCHINH) {
                                        val codedValues = (arcGISFeatureSuCoThongTin.featureTable.featureTypes[finalLoaiSuCoShort - 1].domains[Constant.FieldSuCo.DUONG_KINH_ONG] as CodedValueDomain).codedValues
                                        if (codedValues != null) {
                                            for (codedValue in codedValues) {
                                                if (codedValue.name == item.value) {
                                                    codeDomain = codedValue.code
                                                    break
                                                }
                                            }
                                        }
                                    }
                                    else -> {
                                        val codedValues = (arcGISFeatureSuCoThongTin.featureTable.getField(item.fieldName).domain as CodedValueDomain).codedValues
                                        codeDomain = getCodeDomain(codedValues, item.value!!)
                                    }
                                }
                            }
                            if (item.fieldName == arcGISFeatureSuCoThongTin.featureTable.typeIdField) {
                                arcGISFeatureSuCoThongTin.attributes[item.fieldName] = finalLoaiSuCoShort
                            } else
                                when (item.fieldType) {
                                    Field.Type.DATE -> {
                                        var date: Date
                                        try {
                                            Constant.DateFormat.DATE_FORMAT_VIEW.timeZone = TimeZone.getTimeZone("UTC")
                                            date = Constant.DateFormat.DATE_FORMAT_VIEW.parse(item.value)
                                            c[0].time = date
                                            arcGISFeatureSuCoThongTin.attributes[item.fieldName] = c[0]
                                        } catch (e: ParseException) {
                                            try {
                                                date = Constant.DateFormat.DATE_FORMAT.parse(item.value)
                                                c[0].time = date
                                                arcGISFeatureSuCoThongTin.attributes[item.fieldName] = c[0]
                                            } catch (ignored: ParseException) {

                                            }

                                        }

                                    }

                                    Field.Type.TEXT -> if (hasDomain)
                                        if (codeDomain != null)
                                            arcGISFeatureSuCoThongTin.attributes[item.fieldName] = codeDomain.toString()
                                        else
                                            arcGISFeatureSuCoThongTin.attributes[item.fieldName] = null
                                    else
                                        arcGISFeatureSuCoThongTin.attributes[item.fieldName] = item.value
                                    Field.Type.SHORT -> if (codeDomain != null) {
                                        arcGISFeatureSuCoThongTin.attributes[item.fieldName] = java.lang.Short.parseShort(codeDomain.toString())
                                    } else
                                        try {
                                            arcGISFeatureSuCoThongTin.attributes[item.fieldName] = java.lang.Short.parseShort(item.value)
                                        } catch (ex: NumberFormatException) {
                                            arcGISFeatureSuCoThongTin.attributes[item.fieldName] = null
                                        }

                                    Field.Type.DOUBLE -> if (codeDomain != null) {
                                        arcGISFeatureSuCoThongTin.attributes[item.fieldName] = java.lang.Double.parseDouble(codeDomain.toString())
                                    } else
                                        try {
                                            arcGISFeatureSuCoThongTin.attributes[item.fieldName] = java.lang.Double.parseDouble(item.value)
                                        } catch (e: NumberFormatException) {
                                            arcGISFeatureSuCoThongTin.attributes[item.fieldName] = null
                                        }

                                    Field.Type.INTEGER -> if (codeDomain != null) {
                                        arcGISFeatureSuCoThongTin.attributes[item.fieldName] = Integer.parseInt(codeDomain.toString())
                                    } else
                                        try {
                                            arcGISFeatureSuCoThongTin.attributes[item.fieldName] = Integer.parseInt(item.value)
                                        } catch (e: NumberFormatException) {
                                            arcGISFeatureSuCoThongTin.attributes[item.fieldName] = null
                                        }

                                    else -> {
                                    }
                                }
                            hasDomain = false
                        }
                        arcGISFeatureSuCoThongTin.attributes[Constant.FieldSuCoThongTin.TRANG_THAI] = Constant.TrangThaiSuCo.DANG_XU_LY
                        if (finalTrangThai == mActivity.getString(R.string.SuCo_TrangThai_HoanThanh)) {
                            arcGISFeatureSuCoThongTin.attributes[Constant.FieldSuCoThongTin.TRANG_THAI] = Constant.TrangThaiSuCo.HOAN_THANH
                            //                c[0] = Calendar.getInstance();
                            //                arcGISFeatureSuCoThongTin.getAttributes().put(Constant.FieldSuCo.TGKHAC_PHUC, c[0]);
                            //                long ngayKhacPhuc = c[0].getTimeInMillis();
                            //                long ngayThongBao = ((Calendar) arcGISFeatureSuCoThongTin.getAttributes().
                            //                        get(Constant.FieldSuCo.TGPHAN_ANH)).getTimeInMillis();
                            //                double thoiGianThucHien = new BigDecimal((double) (ngayKhacPhuc - ngayThongBao) / (60 * 60 * 1000)).setScale(2, RoundingMode.HALF_UP).doubleValue();
                            //            arcGISFeature.getAttributes().put((mActivity.getString(R.string.Field_SuCo_ThoiGianThucHien)), thoiGianThucHien);
                        }
                        arcGISFeatureSuCoThongTin.attributes[Constant.FieldSuCoThongTin.TG_CAP_NHAT] = Calendar.getInstance()
                        //        arcGISFeature.getAttributes().put(mActivity.getString(R.string.Field_SuCo_NhanVienGiamSat),
                        //               mApplication.getUserDangNhap.getUserName());
                        mServiceFeatureTableSuCoThongTin!!.loadAsync()
                        mServiceFeatureTableSuCoThongTin.addDoneLoadingListener {
                            // update feature in the feature table
                            mServiceFeatureTableSuCoThongTin.updateFeatureAsync(arcGISFeatureSuCoThongTin).addDoneListener {
                                mServiceFeatureTableSuCoThongTin.applyEditsAsync().addDoneListener {
                                    if (isUpdateAttachment && mImage != null) {
                                        if (arcGISFeatureSuCoThongTin.canEditAttachments())
                                            addAttachment(arcGISFeatureSuCoThongTin)
                                        else
                                            updateSuCo(arcGISFeatureSuCoThongTin)
                                    } else {
                                        updateSuCo(arcGISFeatureSuCoThongTin)

                                    }
                                }
                            }
                        }
                    }


                })
        val queryClause = String.format("%s = '%s' and %s = '%s'",
                Constant.FieldSuCoThongTin.ID_SUCO, mApplication.arcGISFeature!!.attributes[Constant.FieldSuCoThongTin.ID_SUCO].toString(),
                Constant.FieldSuCoThongTin.NHAN_VIEN, mApplication.userDangNhap!!.userName)
        val queryParameters = QueryParameters()
        queryParameters.whereClause = queryClause
        queryServiceFeatureTableAsync.execute(queryParameters)

        return null
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun updateSuCo(arcGISFeature: ArcGISFeature) {
        val queryClause = String.format("%s = '%s'",
                Constant.FieldSuCo.ID_SUCO, mApplication.arcGISFeature!!.attributes[Constant.FieldSuCoThongTin.ID_SUCO].toString())
        val queryParameters = QueryParameters()
        queryParameters.whereClause = queryClause
        QueryServiceFeatureTableAsync(mActivity, mApplication.getDFeatureLayer.layer!!.featureTable as ServiceFeatureTable,
                object : QueryServiceFeatureTableAsync.AsyncResponse {
                    override fun processFinish(output: Feature?) {
                        if (output != null) {
                            val arcGISFeatureSuCo = output as ArcGISFeature
                            val trangThai = arcGISFeature.attributes[Constant.FieldSuCoThongTin.TRANG_THAI]
                            var trangThaiShort: Short = 0
                            if (trangThai != null)
                                trangThaiShort = java.lang.Short.parseShort(trangThai.toString())

                            val hinhThucPhatHien = arcGISFeature.attributes[Constant.FieldSuCoThongTin.HINH_THUC_PHAT_HIEN]
                            var hinhThucPhatHienShort: Short = 2
                            if (hinhThucPhatHien != null)
                                hinhThucPhatHienShort = java.lang.Short.parseShort(hinhThucPhatHien.toString())
                            if (mApplication.userDangNhap!!.groupRole == Constant.Role.GROUPROLE_TC) {
                                arcGISFeatureSuCo.attributes[Constant.FieldSuCo.TRANG_THAI_THI_CONG] = trangThaiShort
                                arcGISFeatureSuCo.attributes[Constant.FieldSuCo.HINH_THUC_PHAT_HIEN_THI_CONG] = hinhThucPhatHienShort

                            } else if (mApplication.userDangNhap!!.groupRole == Constant.Role.GROUPROLE_GS) {
                                arcGISFeatureSuCo.attributes[Constant.FieldSuCo.TRANG_THAI_GIAM_SAT] = trangThaiShort
                                arcGISFeatureSuCo.attributes[Constant.FieldSuCo.HINH_THUC_PHAT_HIEN_GIAM_SAT] = hinhThucPhatHienShort
                            }
                            mServiceFeatureTableSuCo.loadAsync()
                            mServiceFeatureTableSuCo.addDoneLoadingListener {
                                // update feature in the feature table
                                mServiceFeatureTableSuCo.updateFeatureAsync(arcGISFeatureSuCo).addDoneListener { mServiceFeatureTableSuCo.applyEditsAsync().addDoneListener { applyEdit(arcGISFeature) } }
                            }
                        } else
                            publishProgress()
                    }
                }).execute(queryParameters)
        mServiceFeatureTableSuCo.loadAsync()
        mServiceFeatureTableSuCo.addDoneLoadingListener { }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun addAttachment(arcGISFeature: ArcGISFeature) {

        val attachmentName = mActivity.getString(R.string.attachment) + "_" + System.currentTimeMillis() + ".png"
        val addResult = arcGISFeature.addAttachmentAsync(mImage, Bitmap.CompressFormat.PNG.toString(), attachmentName)
        addResult.addDoneListener {
            try {
                val attachment = addResult.get()
                if (attachment.size > 0) {
                    val tableResult = mServiceFeatureTableSuCoThongTin!!.updateFeatureAsync(arcGISFeature)
                    tableResult.addDoneListener { updateSuCo(arcGISFeature) }
                } else
                    publishProgress()
            } catch (ignored: Exception) {
                publishProgress()
            }
        }
    }


    private fun applyEdit(arcGISFeature: ArcGISFeature) {
        val updatedServerResult = mServiceFeatureTableSuCoThongTin!!.applyEditsAsync()
        updatedServerResult.addDoneListener {
            //            List<FeatureEditResult> edits;
            //            try {
            //                edits = updatedServerResult.get();
            //                if (edits.size() > 0) {
            //                    if (!edits.get(0).hasCompletedWithErrors()) {
            publishProgress(arcGISFeature)
            //attachmentList.add(fileName);
            //                                                String s = arcGISFeature.getAttributes().get("objectid").toString();
            // update the attachment list view/ on the control panel
            //                    }
            //                } else {
            //                    publishProgress();
            //                }
            //            } catch (InterruptedException | ExecutionException e) {
            //                publishProgress();
            //                e.printStackTrace();
            //            }
        }

    }

    private fun getIdFeatureTypes(featureTypes: List<FeatureType>, value: String): Any? {
        var code: Any? = null
        for (featureType in featureTypes) {
            if (featureType.name == value) {
                code = featureType.id
                break
            }
        }
        return code
    }

    private fun getCodeDomain(codedValues: List<CodedValue>, value: String): Any? {
        var code: Any? = null
        for (codedValue in codedValues) {
            if (codedValue.name == value) {
                code = codedValue.code
                break
            }
        }
        return code
    }

    override fun onProgressUpdate(vararg values: ArcGISFeature) {
        super.onProgressUpdate(*values)
        if (mDialog != null && mDialog.isShowing) {
            mDialog.dismiss()
        }
        if (values.isNotEmpty())
            this.mDelegate.processFinish(values[0])
        else
            mDelegate.processFinish(null)
    }
}

