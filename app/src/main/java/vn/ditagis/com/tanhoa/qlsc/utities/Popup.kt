package vn.ditagis.com.tanhoa.qlsc.utities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Attachment
import com.esri.arcgisruntime.data.CodedValue
import com.esri.arcgisruntime.data.CodedValueDomain
import com.esri.arcgisruntime.data.FeatureType
import com.esri.arcgisruntime.data.Field
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.Callout
import com.esri.arcgisruntime.mapping.view.MapView
import kotlinx.android.synthetic.main.layout_timkiemdiachi.view.*

import java.util.ArrayList
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.CameraActivity
import vn.ditagis.com.tanhoa.qlsc.MainActivity
import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.ThietBiActivity
import vn.ditagis.com.tanhoa.qlsc.VatTuActivity
import vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewInfoAdapter
import vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAdapter
import vn.ditagis.com.tanhoa.qlsc.async.EditAsync
import vn.ditagis.com.tanhoa.qlsc.async.FindLocationAsycn
import vn.ditagis.com.tanhoa.qlsc.async.NotifyDataSetChangeAsync
import vn.ditagis.com.tanhoa.qlsc.async.ViewAttachmentAsync
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DAddress
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB

@SuppressLint("Registered")
class Popup @RequiresApi(api = Build.VERSION_CODES.O)
constructor(val callout: Callout?, private val mMainActivity: MainActivity, private val mMapView: MapView, private val mGeocoder: Geocoder) : AppCompatActivity(), View.OnClickListener {
    private val mListTenVatTus: MutableList<String>
    private var mSelectedArcGISFeature: ArcGISFeature? = null
    private var mServiceFeatureTable: ServiceFeatureTable? = null
    private var lstFeatureType: MutableList<String>? = null
    private val mDeltaScale = 2
    var featureViewMoreInfoAdapter: FeatureViewMoreInfoAdapter? = null
        private set

    var dialog: DialogInterface? = null
        private set
    private var linearLayout: LinearLayout? = null
    private var mLoaiSuCoID: Any? = null
    private var listHoSoVatTuSuCo: List<HoSoVatTuSuCo>? = null
    private var mListHoSoVatTuThuHoiSuCo: List<HoSoVatTuSuCo>? = null
    private var mIDSuCo: String? = null
    private var mBtnLeft: Button? = null
    private val mListItemBeNgam: MutableList<FeatureViewMoreInfoAdapter.Item>
    private val mApplication: DApplication = mMainActivity.application as DApplication

    init {
        if (mApplication.getDFeatureLayer.layer != null)
            mServiceFeatureTable = mApplication.getDFeatureLayer.layer!!.featureTable as ServiceFeatureTable
        mListTenVatTus = ArrayList()
        mLoaiSuCoID = 0
        try {
            for (vatTu in ListObjectDB.instance.vatTus!!)
                mListTenVatTus.add(vatTu.tenVatTu!!)
        } catch (ignored: Exception) {

        }

        this.mListItemBeNgam = ArrayList()

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun refreshPopup(arcGISFeatureSuCoThongTin: ArcGISFeature) {
        mSelectedArcGISFeature = arcGISFeatureSuCoThongTin
        val attributes = mSelectedArcGISFeature!!.attributes
        val listView = linearLayout!!.findViewById<ListView>(R.id.lstview_thongtinsuco)
        val featureViewInfoAdapter = FeatureViewInfoAdapter(mMainActivity, ArrayList())
        listView.adapter = featureViewInfoAdapter
        val typeIdField = mSelectedArcGISFeature!!.featureTable.typeIdField
        val outFields = mApplication.getDFeatureLayer.layerInfoDTG!!.outFields.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val noOutFields = mApplication.getDFeatureLayer.layerInfoDTG!!.noOutFields.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var isFoundField = false
        //        if (mSelectedArcGISFeature.getFeatureTable().getLayerInfo().getServiceLayerName().equals(mMainActivity.getResources().getString(R.string.ALIAS_DIEM_SU_CO))) {
        mIDSuCo = mApplication.getDiemSuCo!!.idSuCo
        //        }


        for (field in this.mSelectedArcGISFeature!!.featureTable.fields) {
            if (outFields.size == 1 && (outFields[0] == "*" || outFields[0] == "null")) {
            } else {
                for (outField in outFields)
                    if (outField == field.name) {
                        isFoundField = true
                        break
                    }
                if (isFoundField) {
                    isFoundField = false
                } else {
                    continue
                }
            }
            for (noOutField in noOutFields)
                if (noOutField == field.name) {
                    isFoundField = true
                    break
                }
            if (isFoundField) {
                isFoundField = false
                continue
            }
            val value = attributes[field.name]
            val item = FeatureViewInfoAdapter.Item()

            item.alias = field.alias
            item.fieldName = field.name
            if (value != null) {

                if (item.fieldName == typeIdField) {
                    val featureTypes = mSelectedArcGISFeature!!.featureTable.featureTypes
                    val valueFeatureType = getValueFeatureType(featureTypes, value.toString())

                    if (valueFeatureType != null) {
                        mLoaiSuCoID = java.lang.Short.parseShort(attributes[Constant.FieldSuCoThongTin.LOAI_SU_CO].toString())
                        item.value = valueFeatureType.toString()
                    } else
                        continue
                } else if (field.domain != null) {
                    var codedValues: List<CodedValue> = ArrayList()
                    try {
                        when (field.name) {
                            Constant.FieldSuCoThongTin.NGUYEN_NHAN, Constant.FieldSuCoThongTin.VAT_LIEU, Constant.FieldSuCoThongTin.DUONG_KINH_ONG -> for (featureType in arcGISFeatureSuCoThongTin.featureTable.featureTypes) {
                                if (featureType.id == mLoaiSuCoID) {
                                    codedValues = (featureType.domains[field.name] as CodedValueDomain).codedValues
                                    break
                                }
                            }
                            else -> codedValues = (this.mSelectedArcGISFeature!!.featureTable.getField(item.fieldName).domain as CodedValueDomain).codedValues
                        }
                    } catch (ignored: Exception) {

                    }

                    val valueDomain = getValueDomain(codedValues, value.toString())
                    if (valueDomain != null) item.value = valueDomain.toString()
                } else
                    when (field.fieldType) {
                        Field.Type.DATE -> {
                            //                        if (item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienTuNgay))
                            //                                || item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienDenNgay)))
                            //                            item.setValue(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                            //                        else
                            Constant.DateFormat.DATE_FORMAT_VIEW.timeZone = TimeZone.getTimeZone("UTC")
                            item.value = Constant.DateFormat.DATE_FORMAT_VIEW.format((value as Calendar).time)
                        }
                        Field.Type.OID, Field.Type.TEXT, Field.Type.SHORT, Field.Type.DOUBLE, Field.Type.INTEGER, Field.Type.FLOAT -> item.value = value.toString()
                        else -> {
                        }
                    }

            }
            if (item.value != null && item.value!!.isNotEmpty()) {
                featureViewInfoAdapter.add(item)
                featureViewInfoAdapter.notifyDataSetChanged()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun viewMoreInfo() {
        //        AlertDialog.Builder builderProgress = new AlertDialog.Builder(mMainActivity);
        //        LinearLayout layoutProgress = (LinearLayout) mMainActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null);
        //        TextView txtTitle = layoutProgress.findViewById(R.id.txt_progress_dialog_title);
        //        txtTitle.setText(mMainActivity.getString(R.string.message_progress_title));
        //        TextView txtMessage = layoutProgress.findViewById(R.id.txt_progress_dialog_message);
        //        txtMessage.setText(mMainActivity.getString(R.string.message_viewmore_message));
        //
        //        builderProgress.setView(layoutProgress);
        //        builderProgress.setCancelable(false);
        //        AlertDialog dialogProgress = builderProgress.create();
        //        dialogProgress.show();
        //        Window window = dialogProgress.getWindow();
        //        if (window != null) {
        //            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        //            layoutParams.copyFrom(dialogProgress.getWindow().getAttributes());
        //            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        //            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        //            dialogProgress.getWindow().setAttributes(layoutParams);
        //        }

        val arcGISFeatureSuCoThongTin = mApplication.arcGISFeature
        val builder = AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
        @SuppressLint("InflateParams") val layout = mMainActivity.layoutInflater.inflate(R.layout.layout_viewmoreinfo_feature, null)
        featureViewMoreInfoAdapter = FeatureViewMoreInfoAdapter(mMainActivity, ArrayList())
        val lstViewInfo = layout.findViewById<ListView>(R.id.lstView_alertdialog_info)
        mBtnLeft = layout.findViewById(R.id.btn_updateinfo_left)
        val btnRight = layout.findViewById<Button>(R.id.btn_update_right)
        val btnStart = layout.findViewById<Button>(R.id.btn_updateinfo_start)
        btnStart.visibility = View.VISIBLE
        layout.findViewById<View>(R.id.layout_viewmoreinfo_id_su_co).visibility = View.VISIBLE

        layout.findViewById<View>(R.id.framelayout_viewmoreinfo_attachment).setOnClickListener { viewAttachment() }

        lstViewInfo.adapter = featureViewMoreInfoAdapter
        lstViewInfo.setOnItemClickListener { parent, _, position, _ -> listViewMoreInfoItemClick(parent, position, arcGISFeatureSuCoThongTin) }
        loadDataViewMoreInfo(layout, arcGISFeatureSuCoThongTin!!)
        builder.setView(layout)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        layout.findViewById<View>(R.id.framelayout_viewmoreinfo_attachment).visibility = View.VISIBLE
        mBtnLeft!!.text = mMainActivity.resources.getString(R.string.btnLeftUpdateFeature)
        btnRight.text = mMainActivity.resources.getString(R.string.btnRightUpdateFeature)
        btnStart.setOnClickListener { complete(arcGISFeatureSuCoThongTin, dialog) }
        mBtnLeft!!.setOnClickListener { update(arcGISFeatureSuCoThongTin, dialog) }
        btnRight.setOnClickListener {
            val intent = Intent(mMainActivity, CameraActivity::class.java)
            mMainActivity.startActivityForResult(intent, Constant.RequestCode.REQUEST_CODE_CAPTURE)
            this.dialog = dialog
        }
        dialog.show()
        //        if (dialogProgress.isShowing())
        //            dialogProgress.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun complete(arcGISFeatureSuCoThongTin: ArcGISFeature?, dialog: Dialog) {
        if (arcGISFeatureSuCoThongTin != null) {
            val attachmentResults = arcGISFeatureSuCoThongTin.fetchAttachmentsAsync()
            attachmentResults.addDoneListener {

                val attachments: List<Attachment>
                try {
                    attachments = attachmentResults.get()
                    var isFound = false
                    if (!attachments.isEmpty()) {
                        for (attachment in attachments) {
                            if (!attachment.name.contains(mMainActivity.getString(R.string.attachment_add))) {
                                isFound = true
                                break
                            }
                        }
                    }
                    if (isFound) {
                        arcGISFeatureSuCoThongTin.attributes[Constant.FieldSuCoThongTin.TRANG_THAI] = Constant.TrangThaiSuCo.HOAN_THANH
                        arcGISFeatureSuCoThongTin.attributes[Constant.FieldSuCoThongTin.TG_HOAN_THANH] = Calendar.getInstance()
                        mApplication.arcGISFeature = arcGISFeatureSuCoThongTin
                        for (item in featureViewMoreInfoAdapter!!.dItems!!)
                            if (item.fieldName == Constant.FieldSuCoThongTin.TRANG_THAI) {
                                item.value = mMainActivity.getString(R.string.SuCo_TrangThai_HoanThanh)
                                break
                            }
                        featureViewMoreInfoAdapter!!.notifyDataSetChanged()
                        update(arcGISFeatureSuCoThongTin, dialog)
                    } else {
                        Toast.makeText(dialog.context, "Cần chụp ảnh để hoàn thành sự cố", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }
                // if selected feature has attachments, display them in a list fashion
            }


        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun update(arcGISFeatureSuCoThongTin: ArcGISFeature?, dialog: Dialog) {
        var isComplete = false
        for (item in featureViewMoreInfoAdapter!!.dItems!!)
            if (item.fieldName == Constant.FieldSuCoThongTin.TRANG_THAI && item.value == mMainActivity.resources.getString(R.string.SuCo_TrangThai_HoanThanh)) {
                isComplete = true
            }
        if (isComplete) {
            val attachmentResults = arcGISFeatureSuCoThongTin!!.fetchAttachmentsAsync()
            attachmentResults.addDoneListener {
                try {
                    val attachments = attachmentResults.get()
                    val size = attachments.size
                    if (size == 0) {
                        MySnackBar.make(mBtnLeft!!, R.string.message_ChupAnh_HoanThanh, true)
                    } else if (mServiceFeatureTable != null) {
                        val editAsync: EditAsync
                        editAsync = EditAsync(mMainActivity,
                                mSelectedArcGISFeature!!, true, null,
                                listHoSoVatTuSuCo!!, mListHoSoVatTuThuHoiSuCo!!,
                                object : EditAsync.AsyncResponse {
                                    override fun processFinish(feature: ArcGISFeature?) {
                                        if (feature != null) {
                                            callout!!.dismiss()
                                            dialog.dismiss()
                                            APICompleteAsync(mApplication, mIDSuCo!!)
                                                    .execute()
                                        } else {
                                            MySnackBar.make(mBtnLeft!!, mMainActivity.resources.getString(R.string.message_update_failed), true)
                                        }

                                    }

                                })
                        editAsync.execute(featureViewMoreInfoAdapter)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }
            }

        } else if (mServiceFeatureTable != null) {

            val editAsync: EditAsync
            editAsync = EditAsync(mMainActivity,
                    mSelectedArcGISFeature!!, true, null,
                    listHoSoVatTuSuCo!!, mListHoSoVatTuThuHoiSuCo!!,
                    object : EditAsync.AsyncResponse {
                        override fun processFinish(feature: ArcGISFeature?) {
                            if (feature != null) {
                                callout!!.dismiss()
                                dialog.dismiss()
                            } else {
                                MySnackBar.make(mBtnLeft!!, mMainActivity.resources.getString(R.string.message_update_failed), true)
                            }
                        }

                    })
            editAsync.execute(featureViewMoreInfoAdapter)
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun loadDataViewMoreInfo(layout: View, arcGISFeatureSuCoThongTin: ArcGISFeature) {
        val attr = arcGISFeatureSuCoThongTin.attributes
        mListItemBeNgam.clear()
        val updateFields = mApplication.getDFeatureLayer.layerInfoDTG!!.updateFields.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val noOutFields = mApplication.getDFeatureLayer.layerInfoDTG!!.noOutFields.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var isFoundField = false
        val typeIdField = arcGISFeatureSuCoThongTin.featureTable.typeIdField
        var hinhThucPhatHien: Short = -1
        for (field in arcGISFeatureSuCoThongTin.featureTable.fields) {
            if (field.name == Constant.FieldSuCoThongTin.HINH_THUC_PHAT_HIEN) {
                val value = attr[field.name]
                if (value != null)
                    hinhThucPhatHien = java.lang.Short.parseShort(value.toString())
                break
            }
        }
        for (field in arcGISFeatureSuCoThongTin.featureTable.fields) {
            for (noOutField in noOutFields)
                if (noOutField == field.name) {
                    isFoundField = true
                    break
                }
            if (isFoundField) {
                isFoundField = false
                continue
            }
            val value = attr[field.name]

            if (field.name == Constant.FieldSuCoThongTin.ID_SUCO) {
                if (value != null) {
                    mIDSuCo = value.toString()
                    (layout.findViewById<View>(R.id.txt_alertdialog_id_su_co) as TextView).text = mIDSuCo
//                    this.listHoSoVatTuSuCo = HoSoVatTuSuCoDB(mMainActivity).find(mIDSuCo)
//                    this.mListHoSoVatTuThuHoiSuCo = HoSoVatTuThuHoiSuCoDB(mMainActivity).find(mIDSuCo)
                    this.listHoSoVatTuSuCo = ArrayList()
                    this.mListHoSoVatTuThuHoiSuCo = ArrayList()
                }
            } else {
                val item = FeatureViewMoreInfoAdapter.Item()
                item.alias = field.alias
                item.fieldName = field.name
                item.isEdit = false
                for (updateField in updateFields) {
                    //Nếu là update field
                    if (item.fieldName == updateField) {
                        item.isEdit = true
                        break
                    }
                }
                if (field.name == Constant.FieldSuCoThongTin.TGTC_DU_KIEN_TU || field.name == Constant.FieldSuCoThongTin.TGTC_DU_KIEN_DEN)
                    item.isEdit = hinhThucPhatHien == Constant.HinhThucPhatHien.BE_NGAM && mApplication.userDangNhap!!.role == Constant.Role.ROLE_PGN
                //                boolean isPGNField = false, isPGNField_NotPGNRole = false;
                //                for (String pgnField : pgnFields) {
                //                    if (item.getFieldName().equals(pgnField)) {
                //                        isPGNField = true;
                ////                        if (KhachHangDangNhap.getInstance().getKhachHang().getRole().equals(mMainActivity.getResources().getString(R.string.role_phong_giam_nuoc))) {
                //                        item.setEdit(true);
                ////                        } else
                ////                            isPGNField_NotPGNRole = true;
                //                    }
                //                }

                if (value != null) {
                    if (item.fieldName == typeIdField) {
                        val featureTypes = arcGISFeatureSuCoThongTin.featureTable.featureTypes
                        val valueFeatureType = getValueFeatureType(featureTypes, value.toString())
                        mLoaiSuCoID = java.lang.Short.parseShort(value.toString())
                        if (valueFeatureType != null) {
                            item.value = valueFeatureType.toString()
                        }

                    } else if (field.domain != null) {
                        var codedValues: List<CodedValue> = ArrayList()
                        try {
                            when (field.name) {
                                Constant.FieldSuCoThongTin.NGUYEN_NHAN, Constant.FieldSuCoThongTin.VAT_LIEU, Constant.FieldSuCoThongTin.DUONG_KINH_ONG -> for (featureType in arcGISFeatureSuCoThongTin.featureTable.featureTypes) {
                                    if (featureType.id == mLoaiSuCoID) {
                                        codedValues = (featureType.domains[field.name] as CodedValueDomain).codedValues
                                        break
                                    }
                                }
                                else -> codedValues = (arcGISFeatureSuCoThongTin.featureTable.getField(item.fieldName).domain as CodedValueDomain).codedValues
                            }
                        } catch (ignored: Exception) {

                        }

                        val valueDomain = getValueDomain(codedValues, value.toString())
                        if (valueDomain != null) item.value = valueDomain.toString()
                    } else
                        when (field.fieldType) {
                            Field.Type.DATE ->
                                //                            if (item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienTuNgay))
                                //                                    || item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienDenNgay)))
                                //                                item.setValue(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                                //                            else
                                item.value = Constant.DateFormat.DATE_FORMAT.format((value as Calendar).time)
                            Field.Type.OID, Field.Type.TEXT -> item.value = value.toString()
                            Field.Type.DOUBLE, Field.Type.SHORT -> item.value = value.toString()
                            else -> {
                            }
                        }
                }

                item.fieldType = field.fieldType
                //                if (isPGNField) {
                //                    if (!mListItemBeNgam.contains(item))
                //                        mListItemBeNgam.add(item);
                //                    continue;
                //                }
                //                if (isPGNField_NotPGNRole)
                //                    continue;
                featureViewMoreInfoAdapter!!.add(item)
                featureViewMoreInfoAdapter!!.notifyDataSetChanged()
            }
        }

    }

    private fun viewAttachment() {
        try {
            val viewAttachmentAsync = ViewAttachmentAsync(mMainActivity)
            viewAttachmentAsync.execute()
        } catch (e: Exception) {
            Toast.makeText(mMainActivity.applicationContext, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getValueDomain(codedValues: List<CodedValue>, code: String): Any? {
        var value: Any? = null
        for (codedValue in codedValues) {
            if (codedValue.code.toString() == code) {
                value = codedValue.name
                break
            }

        }
        return value
    }

    private fun getValueFeatureType(featureTypes: List<FeatureType>, code: String): Any? {
        var value: Any? = null
        for (featureType in featureTypes) {
            if (featureType.id.toString() == code) {
                value = featureType.name
                break
            }
        }
        return value
    }

    private fun listViewMoreInfoItemClick(parent: AdapterView<*>,
                                          position: Int, arcGISFeature: ArcGISFeature?) {
        if (parent.getItemAtPosition(position) is FeatureViewMoreInfoAdapter.Item) {
            val item = parent.getItemAtPosition(position) as FeatureViewMoreInfoAdapter.Item
            if (item.isEdit) {
                val builder = AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_Dialog_Alert)
                builder.setTitle("Cập nhật thuộc tính")
                builder.setMessage(item.alias)

                @SuppressLint("InflateParams") val layout = mMainActivity.layoutInflater.inflate(R.layout.layout_dialog_update_feature_listview, null) as LinearLayout
                val btnLeft = layout.findViewById<Button>(R.id.btn_updateinfo_left)
                val btnRight = layout.findViewById<Button>(R.id.btn_update_right)

                btnLeft.text = mMainActivity.resources.getString(R.string.btnLeft_editItemViewMoreInfo)
                btnRight.text = mMainActivity.resources.getString(R.string.btnRight_editItemViewMoreInfo)


                builder.setView(layout)

                loadDataEdit(item, layout, arcGISFeature)

                val dialog = builder.create()
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                btnLeft.setOnClickListener { dialog.dismiss() }
                btnRight.setOnClickListener { updateEdit(item, layout, parent, dialog, arcGISFeature!!) }
                dialog.show()

            }
        }

    }

    private fun loadDataEdit(item: FeatureViewMoreInfoAdapter.Item, layout: LinearLayout, arcGISFeature: ArcGISFeature?) {
        when (item.fieldName) {
            Constant.FieldSuCoThongTin.NGUYEN_NHAN, Constant.FieldSuCoThongTin.VAT_LIEU, Constant.FieldSuCoThongTin.DUONG_KINH_ONG -> loadDataEditDomain(item, layout, arcGISFeature)
            else -> loadDataEditAnother(item, layout, arcGISFeature!!)
        }
    }

    private fun loadDataEditDomain(item: FeatureViewMoreInfoAdapter.Item, layout: LinearLayout, arcGISFeatureSuCoThongTin: ArcGISFeature?) {
        val layoutSpin = layout.findViewById<LinearLayout>(R.id.layout_edit_viewmoreinfo_Spinner)
        val spin = layout.findViewById<Spinner>(R.id.spin_edit_viewmoreinfo)

        layoutSpin.visibility = View.VISIBLE
        val codes = ArrayList<String>()
        try {
            var codedValues: List<CodedValue>? = null
            for (featureType in arcGISFeatureSuCoThongTin!!.featureTable.featureTypes) {
                if (featureType.id == mLoaiSuCoID) {
                    codedValues = (featureType.domains[item.fieldName] as CodedValueDomain).codedValues
                    break
                }
            }
            if (codedValues != null) {
                for (codedValue in codedValues)
                    codes.add(codedValue.name)
                val adapter = ArrayAdapter(layout.context, android.R.layout.simple_list_item_1, codes)
                spin.adapter = adapter
            }
        } catch (e: Exception) {
            messageSelectLoaiSuCo()
        }

        if (item.value != null)
            spin.setSelection(codes.indexOf(item.value!!))
    }

    private fun messageSelectLoaiSuCo() {
        Toast.makeText(mMainActivity.applicationContext, R.string.message_select_loai_su_co, Toast.LENGTH_LONG).show()
    }


    private fun loadDataEditAnother(item: FeatureViewMoreInfoAdapter.Item, layout: LinearLayout, arcGISFeature: ArcGISFeature) {
        val layoutTextView = layout.findViewById<FrameLayout>(R.id.layout_edit_viewmoreinfo_TextView)
        val textView = layout.findViewById<TextView>(R.id.txt_edit_viewmoreinfo)
        val button = layout.findViewById<Button>(R.id.btn_edit_viewmoreinfo)
        val layoutEditText = layout.findViewById<LinearLayout>(R.id.layout_edit_viewmoreinfo_Editext)
        val editText = layout.findViewById<EditText>(R.id.etxt_edit_viewmoreinfo)
        val layoutSpin = layout.findViewById<LinearLayout>(R.id.layout_edit_viewmoreinfo_Spinner)
        val spin = layout.findViewById<Spinner>(R.id.spin_edit_viewmoreinfo)

        val domain = arcGISFeature.featureTable.getField(item.fieldName).domain
        if (item.fieldName == arcGISFeature.featureTable.typeIdField) {
            layoutSpin.visibility = View.VISIBLE
            val adapter = ArrayAdapter(layout.context, android.R.layout.simple_list_item_1, lstFeatureType!!)
            spin.adapter = adapter
            if (item.value != null) {
                spin.setSelection(lstFeatureType!!.indexOf(item.value!!))
            }

        } else if (domain != null) {
            layoutSpin.visibility = View.VISIBLE
            val codedValues = (domain as CodedValueDomain).codedValues

            if (codedValues != null) {
                val codes = ArrayList<String>()
                for (codedValue in codedValues)
                    codes.add(codedValue.name)
                val adapter = ArrayAdapter(layout.context, android.R.layout.simple_list_item_1, codes)
                spin.adapter = adapter
                if (item.value != null)
                    spin.setSelection(codes.indexOf(item.value!!))


                if (item.fieldName == Constant.FieldSuCoThongTin.HINH_THUC_PHAT_HIEN) {
                    spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                            if (i == 0) {
                                //                                if (!KhachHangDangNhap.getInstance().getKhachHang().getRole()
                                //                                        .equals(mMainActivity.getResources().getString(R.string.role_phong_giam_nuoc))) {
                                MySnackBar.make(spin, "Bạn không có quyền chọn lựa chọn: bể ngầm!!!", true)
                                spin.setSelection(1)

                                //                                } else
                                //                                    isVISIBLE = true;
                            }


                            for (itemBeNgam in mListItemBeNgam) {
                                if (featureViewMoreInfoAdapter!!.dItems!!.contains(itemBeNgam))
                                    featureViewMoreInfoAdapter!!.remove(itemBeNgam)
                            }
                            featureViewMoreInfoAdapter!!.notifyDataSetChanged()
                        }

                        override fun onNothingSelected(adapterView: AdapterView<*>) {

                        }
                    }
                }

            }
        } else
            when (item.fieldType) {
                Field.Type.DATE -> {
                    layoutTextView.visibility = View.VISIBLE
                    textView.text = item.value
                    button.setOnClickListener {
                        val dialogView = View.inflate(mMainActivity, R.layout.date_time_picker, null)
                        val alertDialog = android.app.AlertDialog.Builder(mMainActivity).create()
                        dialogView.findViewById<View>(R.id.date_time_set).setOnClickListener {
                            val datePicker = dialogView.findViewById<DatePicker>(R.id.date_picker)
                            val s = String.format(mMainActivity.resources.getString(R.string.format_date_month_year), datePicker.dayOfMonth, datePicker.month + 1, datePicker.year)

                            textView.text = s
                            alertDialog.dismiss()
                        }
                        alertDialog.setView(dialogView)
                        alertDialog.show()
                    }
                }
                Field.Type.TEXT -> {
                    layoutEditText.visibility = View.VISIBLE
                    editText.setText(item.value)
                }
                Field.Type.SHORT -> {
                    layoutEditText.visibility = View.VISIBLE
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                    editText.setText(item.value)
                }
                Field.Type.DOUBLE -> {
                    layoutEditText.visibility = View.VISIBLE
                    editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                    editText.setText(item.value)
                }
                else -> {
                }
            }
    }

    private fun updateEdit(item: FeatureViewMoreInfoAdapter.Item, layout: LinearLayout, parent: AdapterView<*>, dialog: DialogInterface, arcGISFeatureSuCoThongTin: ArcGISFeature) {
        val textView = layout.findViewById<TextView>(R.id.txt_edit_viewmoreinfo)
        val editText = layout.findViewById<EditText>(R.id.etxt_edit_viewmoreinfo)
        val spin = layout.findViewById<Spinner>(R.id.spin_edit_viewmoreinfo)

        val domain = arcGISFeatureSuCoThongTin.featureTable.getField(item.fieldName).domain
        if (item.fieldName == arcGISFeatureSuCoThongTin.featureTable.typeIdField || domain != null) {
            //Khi đổi subtype
            item.value = spin.selectedItem.toString()
            if (item.fieldName == arcGISFeatureSuCoThongTin.featureTable.typeIdField) {
                for (i in 0 until arcGISFeatureSuCoThongTin.featureTable.featureTypes.size) {
                    val featureType = arcGISFeatureSuCoThongTin.featureTable.featureTypes[i]
                    if (featureType.name == item.value) {
                        mLoaiSuCoID = java.lang.Short.parseShort(featureType.id.toString())
                        //reset những field ảnh hưởng bởi subtype
                        val adapter = parent.adapter as FeatureViewMoreInfoAdapter
                        for (item1 in adapter.dItems!!) {
                            if (item1.fieldName == Constant.FieldSuCoThongTin.NGUYEN_NHAN ||
                                    item1.fieldName == Constant.FieldSuCoThongTin.VAT_LIEU ||
                                    item1.fieldName == Constant.FieldSuCoThongTin.DUONG_KINH_ONG) {
                                item1.value = null
                            }
                        }
                        break
                    }
                }

            }
        } else {
            if (item.fieldName == Constant.FieldSuCoThongTin.NGUYEN_NHAN ||
                    item.fieldName == Constant.FieldSuCoThongTin.VAT_LIEU ||
                    item.fieldName == Constant.FieldSuCoThongTin.DUONG_KINH_ONG) {
                item.value = spin.selectedItem.toString()
            } else {
                when (item.fieldType) {
                    Field.Type.DATE -> item.value = textView.text.toString()
                    Field.Type.DOUBLE -> try {
                        val x = java.lang.Double.parseDouble(editText.text.toString())
                        item.value = String.format("%s", x)
                    } catch (e: Exception) {
                        Toast.makeText(mMainActivity, "Số liệu nhập vào không đúng định dạng!!!", Toast.LENGTH_LONG).show()
                    }

                    Field.Type.TEXT -> item.value = editText.text.toString()
                    Field.Type.SHORT -> try {
                        val x = java.lang.Short.parseShort(editText.text.toString())
                        item.value = String.format("%s", x)
                    } catch (e: Exception) {
                        Toast.makeText(mMainActivity, "Số liệu nhập vào không đúng định dạng!!!", Toast.LENGTH_LONG).show()
                    }

                    else -> {
                    }
                }
            }
        }
        dialog.dismiss()
        item.isEdited = true
        val adapter = parent.adapter as FeatureViewMoreInfoAdapter
        NotifyDataSetChangeAsync(mMainActivity).execute(adapter)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun clearSelection() {
        if (mServiceFeatureTable != null) {
            val featureLayer = mApplication.getDFeatureLayer.layer
            featureLayer!!.clearSelection()
        }
    }

    private fun dimissCallout() {
        if (callout != null && callout.isShowing) {
            callout.dismiss()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    fun showPopup() {
        val idSuCo = mApplication.arcGISFeature!!.attributes[Constant.FieldSuCoThongTin.ID_SUCO]
        if (idSuCo != null) {
            mIDSuCo = idSuCo.toString()
            mApplication.getDiemSuCo!!.idSuCo = mIDSuCo
        } else if (mApplication.getDiemSuCo!!.idSuCo != null) {
            mIDSuCo = mApplication.getDiemSuCo!!.idSuCo
        }
        clearSelection()
        dimissCallout()
        this.mSelectedArcGISFeature = mApplication.arcGISFeature
        val featureLayer: FeatureLayer? = mApplication.getDFeatureLayer.layer
        featureLayer!!.selectFeature(mSelectedArcGISFeature!!)
        lstFeatureType = ArrayList()
        for (i in 0 until mSelectedArcGISFeature!!.featureTable.featureTypes.size) {
            lstFeatureType!!.add(mSelectedArcGISFeature!!.featureTable.featureTypes[i].name)
        }
        val inflater = LayoutInflater.from(this.mMainActivity.applicationContext)
        linearLayout = inflater.inflate(R.layout.layout_thongtinsuco, null) as LinearLayout
        refreshPopup(mSelectedArcGISFeature!!)
        (linearLayout!!.findViewById<View>(R.id.txt_thongtin_ten) as TextView).text = featureLayer.name
        linearLayout!!.findViewById<View>(R.id.imgBtn_cancel_layout_thongtinsuco).setOnClickListener { callout!!.dismiss() }
        //user admin mới có quyền xóa
        if (mApplication.getDFeatureLayer.layerInfoDTG!!.isDelete) {
            linearLayout!!.findViewById<View>(R.id.imgBtn_delete).setOnClickListener(this)
        } else {
            linearLayout!!.findViewById<View>(R.id.imgBtn_delete).visibility = View.GONE
        }
        //        if (Short.parseShort(mApplication.getArcGISFeature().getAttributes().
        //                get(Constant.FieldSuCoThongTin.TRANG_THAI).toString()) == Constant.TrangThaiSuCo.HOAN_THANH)
        //            linearLayout.findViewById(R.id.imgBtn_ViewMoreInfo).setVisibility(View.GONE);
        //        else
        linearLayout!!.findViewById<View>(R.id.imgBtn_thongtinsuco_menu).setOnClickListener(this)
        linearLayout!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        val envelope = mApplication.geometry!!.extent
        mMapView.setViewpointGeometryAsync(envelope, 0.0)
        // show CallOut
        callout!!.location = envelope.center
        callout.content = linearLayout!!
        callout.show()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    fun showPopupFindLocation(position: Point?, location: String) {
        try {
            if (position == null)
                return
            clearSelection()
            dimissCallout()

            val inflater = LayoutInflater.from(this.mMainActivity.applicationContext)
            linearLayout = inflater.inflate(R.layout.layout_timkiemdiachi, null) as LinearLayout

            (linearLayout!!.findViewById<View>(R.id.txt_timkiemdiachi) as TextView).text = location
            linearLayout!!.findViewById<View>(R.id.imgBtn_timkiemdiachi_themdiemsuco).setOnClickListener(this)
            linearLayout!!.txt__timkiemdiachi__phong_to.setOnClickListener {
                var scale = mMapView.mapScale
                try {
                    scale/=mDeltaScale
                    val geometry = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).targetGeometry
                    val center = geometry.extent.center
                    mMapView.setViewpointCenterAsync(center, scale)
                } catch (Ex: Exception) {
                }
            }
            linearLayout!!.txt__timkiemdiachi__thu_nho.setOnClickListener {
                var scale = mMapView.mapScale
                try {
                    scale*=mDeltaScale
                    val geometry = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).targetGeometry
                    val center = geometry.extent.center
                    mMapView.setViewpointCenterAsync(center, scale)
                } catch (Ex: Exception) {
                }
            }
            linearLayout!!.findViewById<View>(R.id.imgBtn_cancel_timkiemdiachi).setOnClickListener {
                callout!!.dismiss()
                mMainActivity.setIsAddFeature(false)
            }


            linearLayout!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // show CallOut
            callout!!.location = position
            callout.content = linearLayout!!
            this.runOnUiThread {
                callout.refresh()
                callout.show()
            }
        } catch (e: Exception) {
            Log.e("Popup tìm kiếm", e.toString())
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun showPopupFindLocation(position: Point?) {
        try {
            if (position == null)
                return
            @SuppressLint("InflateParams")
            val findLocationAsycn = FindLocationAsycn(mMainActivity, false, mGeocoder,
                    object : FindLocationAsycn.AsyncResponse {
                        override fun processFinish(output: List<DAddress>?) {
                            if (output != null && output.isNotEmpty()) {
                                clearSelection()

                                dimissCallout()

                                val address = output[0]
                                val addressLine = address.location
                                val inflater = LayoutInflater.from(mMainActivity.applicationContext)
                                linearLayout = inflater.inflate(R.layout.layout_timkiemdiachi, null) as LinearLayout
                                (linearLayout!!.findViewById<View>(R.id.txt_timkiemdiachi) as TextView).text = addressLine
                                linearLayout!!.txt__timkiemdiachi__phong_to.setOnClickListener {
                                    var scale = mMapView.mapScale
                                    try {
                                        scale /= mDeltaScale
                                        val geometry = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).targetGeometry
                                        val center = geometry.extent.center
                                        mMapView.setViewpointCenterAsync(center, scale)
                                    } catch (Ex: Exception) {
                                    }
                                }
                                linearLayout!!.txt__timkiemdiachi__thu_nho.setOnClickListener {
                                    var scale = mMapView.mapScale
                                    try {
                                        scale *= mDeltaScale
                                        val geometry = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).targetGeometry
                                        val center = geometry.extent.center
                                        mMapView.setViewpointCenterAsync(center, scale)
                                    } catch (Ex: Exception) {
                                    }
                                }
                                linearLayout!!.findViewById<View>(R.id.imgBtn_timkiemdiachi_themdiemsuco).setOnClickListener(this@Popup)
                                linearLayout!!.findViewById<View>(R.id.imgBtn_cancel_timkiemdiachi).setOnClickListener {
                                    mMainActivity.setIsAddFeature(false)
                                    callout!!.dismiss()
                                }
                                linearLayout!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                // show CallOut
                                callout!!.location = position
                                callout.content = linearLayout!!
                                this@Popup.runOnUiThread {
                                    callout.refresh()
                                    callout.show()
                                }
                            }
                        }

                    })
            val project = GeometryEngine.project(position, SpatialReferences.getWgs84())
            val location = doubleArrayOf(project.extent.center.x, project.extent.center.y)
            findLocationAsycn.setmLongtitude(location[0])
            findLocationAsycn.setmLatitude(location[1])
            findLocationAsycn.execute()
        } catch (e: Exception) {
            Log.e("Popup tìm kiếm", e.toString())
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onClick(view: View) {
        when (view.id) {
            R.id.imgBtn_thongtinsuco_menu -> {
                val popup = PopupMenu(mMainActivity, view)
                val inflater = popup.menuInflater
                inflater.inflate(R.menu.menu_feature_popup, popup.menu)
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.item_popup_find_route -> {
                            mMainActivity.findRoute()
                            true
                        }
                        R.id.item_popup_view_attachment -> {
                            viewAttachment()
                            true
                        }
                        R.id.item_popup_edit -> {
                            viewMoreInfo()
                            true
                        }
                        R.id.item_popup_vattu_capmoi -> {
                            val intent = Intent(mMainActivity, VatTuActivity::class.java)
                            mApplication.loaiVatTu = Constant.CodeVatTu.CAPMOI
                            mApplication.getDiemSuCo!!.idSuCo = mIDSuCo
                            mMainActivity.startActivity(intent)
                            true
                        }
                        R.id.item_popup_vattu_thuhoi -> {
                            val intentThuHoi = Intent(mMainActivity, VatTuActivity::class.java)
                            mApplication.loaiVatTu = Constant.CodeVatTu.THUHOI
                            mApplication.getDiemSuCo!!.idSuCo = mIDSuCo
                            mMainActivity.startActivity(intentThuHoi)
                            true
                        }
                        R.id.item_popup_thietbi -> {
                            val intentThietBi = Intent(mMainActivity, ThietBiActivity::class.java)
                            mMainActivity.startActivity(intentThietBi)
                            true
                        }

                        else -> false
                    }
                }
                popup.show()
            }
            R.id.imgBtn_delete -> mSelectedArcGISFeature!!.featureTable.featureLayer.clearSelection()
            R.id.imgBtn_timkiemdiachi_themdiemsuco -> mMainActivity.onClick(view)
        }//                deleteFeature();
    }

}
