package vn.ditagis.com.tanhoa.qlsc

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView

import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.TimeZone
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.adapter.ThongKeAdapter
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.utities.TimePeriodReport

class ThongKeActivity : AppCompatActivity() {
    private var txtTongSuCo: TextView? = null
    private var mTxtChuaSua: TextView? = null
    private var mTxtBeNgam: TextView? = null
    private var mTxtDangSua: TextView? = null
    private var mTxtHoanThanh: TextView? = null
    private var mTxtPhanTramChuaSua: TextView? = null
    private var mTxtPhanTramBeNgam: TextView? = null
    private var mTxtPhanTramDangSua: TextView? = null
    private var mTxtPhanTramHoanThanh: TextView? = null
    private var mServiceFeatureTable: ServiceFeatureTable? = null
    private var mThongKeAdapter: ThongKeAdapter? = null
    private var mChuaSuaChua: Int = 0
    private var mBeNgam: Int = 0
    private var mDangSuaChua: Int = 0
    private var mHoanThanh: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thong_ke)
        val mApplication = application as DApplication
        for (dLayerInfo in mApplication.lstFeatureLayerDTG!!)
            if (dLayerInfo.id == getString(R.string.IDLayer_DiemSuCo)) {
                mServiceFeatureTable = ServiceFeatureTable(dLayerInfo.url)
                break
            }
        val timePeriodReport = TimePeriodReport(this)
        val items = timePeriodReport.getItems()
        mThongKeAdapter = ThongKeAdapter(this, (items as MutableList<ThongKeAdapter.Item>?)!!)

        this.txtTongSuCo = this.findViewById(R.id.txtTongSuCo)
        this.mTxtChuaSua = this.findViewById(R.id.txtChuaSua)
        this.mTxtBeNgam = findViewById(R.id.txtChuaSuaBeNgam)
        this.mTxtDangSua = this.findViewById(R.id.txtDangSua)
        this.mTxtHoanThanh = this.findViewById(R.id.txtHoanThanh)
        this.mTxtPhanTramChuaSua = this.findViewById(R.id.txtPhanTramChuaSua)
        this.mTxtPhanTramBeNgam = findViewById(R.id.txtPhanTramChuaSuaBeNgam)
        this.mTxtPhanTramDangSua = this.findViewById(R.id.txtPhanTramDangSua)
        this.mTxtPhanTramHoanThanh = this.findViewById(R.id.txtPhanTramHoanThanh)
        this@ThongKeActivity.findViewById<View>(R.id.layout_thongke_thoigian).setOnClickListener { showDialogSelectTime() }
        query(items!![0])
    }

    private fun showDialogSelectTime() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen)
        @SuppressLint("InflateParams") val layout = layoutInflater.inflate(R.layout.layout_listview_thongketheothoigian, null)
        val listView = layout.findViewById<ListView>(R.id.lstView_thongketheothoigian)
        listView.adapter = mThongKeAdapter
        builder.setView(layout)
        val selectTimeDialog = builder.create()
        selectTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        selectTimeDialog.show()
        val finalItems = mThongKeAdapter!!.getItems()
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val itemAtPosition = parent.getItemAtPosition(position) as ThongKeAdapter.Item
            selectTimeDialog.dismiss()
            if (itemAtPosition.id == finalItems.size) {
                val builder = AlertDialog.Builder(this@ThongKeActivity, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen)
                @SuppressLint("InflateParams") val layout = layoutInflater.inflate(R.layout.layout_thongke_thoigiantuychinh, null)
                builder.setView(layout)
                val tuychinhDateDialog = builder.create()
                tuychinhDateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                tuychinhDateDialog.show()
                val edit_thongke_tuychinh_ngaybatdau = layout.findViewById<EditText>(R.id.edit_thongke_tuychinh_ngaybatdau)
                val edit_thongke_tuychinh_ngayketthuc = layout.findViewById<EditText>(R.id.edit_thongke_tuychinh_ngayketthuc)
                if (itemAtPosition.thoigianbatdau != null)
                    edit_thongke_tuychinh_ngaybatdau.setText(itemAtPosition.thoigianbatdau)
                if (itemAtPosition.thoigianketthuc != null)
                    edit_thongke_tuychinh_ngayketthuc.setText(itemAtPosition.thoigianketthuc)

                val finalThoigianbatdau = StringBuilder()
                finalThoigianbatdau.append(itemAtPosition.thoigianbatdau)
                edit_thongke_tuychinh_ngaybatdau.setOnClickListener { showDateTimePicker(edit_thongke_tuychinh_ngaybatdau, finalThoigianbatdau, "START") }
                val finalThoigianketthuc = StringBuilder()
                finalThoigianketthuc.append(itemAtPosition.thoigianketthuc)
                edit_thongke_tuychinh_ngayketthuc.setOnClickListener { showDateTimePicker(edit_thongke_tuychinh_ngayketthuc, finalThoigianketthuc, "FINISH") }

                layout.findViewById<View>(R.id.btn_layngaythongke).setOnClickListener {
                    if (kiemTraThoiGianNhapVao(finalThoigianbatdau.toString(), finalThoigianketthuc.toString())) {
                        tuychinhDateDialog.dismiss()
                        itemAtPosition.thoigianbatdau = finalThoigianbatdau.toString()
                        itemAtPosition.thoigianketthuc = finalThoigianketthuc.toString()
                        itemAtPosition.thoigianhienthi = edit_thongke_tuychinh_ngaybatdau.text.toString() + " - " + edit_thongke_tuychinh_ngayketthuc.text
                        mThongKeAdapter!!.notifyDataSetChanged()
                        query(itemAtPosition)
                    }
                }

            } else {
                query(itemAtPosition)
            }
        }
    }

    private fun kiemTraThoiGianNhapVao(startDate: String, endDate: String): Boolean {
        if (startDate == "" || endDate == "") return false
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        try {
            val date1 = dateFormat.parse(startDate)
            val date2 = dateFormat.parse(endDate)
            return if (date1.after(date2)) {
                false
            } else
                true
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return false
    }

    fun showDateTimePicker(editText: EditText, output: StringBuilder, typeInput: String) {
        output.delete(0, output.length)
        val dialogView = View.inflate(this, R.layout.date_time_picker, null)
        val alertDialog = android.app.AlertDialog.Builder(this).create()
        dialogView.findViewById<View>(R.id.date_time_set).setOnClickListener {
            val datePicker = dialogView.findViewById<DatePicker>(R.id.date_picker)
            val calendar = GregorianCalendar(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val displaytime = DateFormat.format(getString(R.string.format_time_day_month_year), calendar.time) as String
            val format: String
            if (typeInput == "START") {
                calendar.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
                calendar.clear(Calendar.MINUTE)
                calendar.clear(Calendar.SECOND)
                calendar.clear(Calendar.MILLISECOND)
            } else if (typeInput == "FINISH") {
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
            }
            @SuppressLint("SimpleDateFormat") val dateFormatGmt = SimpleDateFormat(getString(R.string.format_day_yearfirst))
            dateFormatGmt.timeZone = TimeZone.getTimeZone("GMT")
            format = dateFormatGmt.format(calendar.time)
            editText.setText(displaytime)
            output.append(format)
            alertDialog.dismiss()
        }
        alertDialog.setView(dialogView)
        alertDialog.show()

    }

    private fun query(item: ThongKeAdapter.Item) {
        mHoanThanh = 0
        mDangSuaChua = mHoanThanh
        mBeNgam = mDangSuaChua
        mChuaSuaChua = mBeNgam
        (this@ThongKeActivity.findViewById<View>(R.id.txt_thongke_mota) as TextView).text = item.mota
        val txtThoiGian = this@ThongKeActivity.findViewById<TextView>(R.id.txt_thongke_thoigian)
        if (item.thoigianhienthi == null)
            txtThoiGian.visibility = View.GONE
        else {
            txtThoiGian.text = item.thoigianhienthi
            txtThoiGian.visibility = View.VISIBLE
        }
        var whereClause = ""

        //binhThuong
        if (item.thoigianbatdau == null || item.thoigianketthuc == null) {

            //            whereClause += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanPhuNhuanCode));
            //
            //            whereClause += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanTanBinhCode));
            //
            //            whereClause += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanTanPhuCode));

            whereClause += " 1 = 1"
        } else {

            whereClause = String.format("(%s >= date '%s' and %s <= date '%s') and (",
                    Constant.FieldSuCo.TGPHAN_ANH, item.thoigianbatdau,
                    Constant.FieldSuCo.TGKHAC_PHUC, item.thoigianketthuc)

            //            whereClause += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanPhuNhuanCode));
            //
            //            whereClause += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanTanBinhCode));
            //
            //            whereClause += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanTanPhuCode));
            whereClause += " 1 = 1)"
        }

        var whereClauseBeNgam = ""
        //Bể ngầm
        if (item.thoigianbatdau == null || item.thoigianketthuc == null) {
            whereClauseBeNgam += " HinhThucPhatHien = 1 and ("
            //            whereClauseBeNgam += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanPhuNhuanCode));
            //
            //            whereClauseBeNgam += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanTanBinhCode));
            //
            //            whereClauseBeNgam += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanTanPhuCode));

            whereClauseBeNgam += " 1 = 1)"
        } else {
            whereClauseBeNgam += " HinhThucPhatHien = 1 and "
            whereClauseBeNgam = String.format("(%s >= date '%s' and %s <= date '%s') and (",
                    Constant.FieldSuCo.TGPHAN_ANH, item.thoigianbatdau,
                    Constant.FieldSuCo.TGKHAC_PHUC, item.thoigianketthuc)

            //            whereClauseBeNgam += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanPhuNhuanCode));
            //
            //            whereClauseBeNgam += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanTanBinhCode));
            //
            //            whereClauseBeNgam += String.format("%s = '%s' or ", getString(R.string.Field_SuCo_MaQuan), getString(R.string.QuanTanPhuCode));
            whereClauseBeNgam += " 1= 1)"
        }


        val queryParameters = QueryParameters()
        queryParameters.whereClause = whereClause


        //        final ListenableFuture<FeatureQueryResult> feature =
        //                mServiceFeatureTable.populateFromServiceAsync(queryParameters, true, outFields);
        val feature = mServiceFeatureTable!!.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        feature.addDoneListener {
            try {
                val result = feature.get()
                val iterator = result.iterator()
                var item: Feature
                while (iterator.hasNext()) {
                    item = iterator.next()
                    //                    for (Object i : result) {
                    //                        Feature item = (Feature) i;
                    val value = item.attributes[getString(R.string.trangthai)]
                    var trangThai = resources.getInteger(R.integer.trang_thai_chua_sua_chua)
                    if (value != null) {
                        trangThai = Integer.parseInt(value.toString())
                    }
                    if (trangThai == resources.getInteger(R.integer.trang_thai_chua_sua_chua))
                        mChuaSuaChua++
                    else if (trangThai == resources.getInteger(R.integer.trang_thai_dang_sua_chua))
                        mDangSuaChua++
                    else if (trangThai == resources.getInteger(R.integer.trang_thai_hoan_thanh))
                        mHoanThanh++

                }
                displayReport()

            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }

        //beNgam
        val queryParametersBeNgam = QueryParameters()
        queryParametersBeNgam.whereClause = whereClauseBeNgam


        //        final ListenableFuture<FeatureQueryResult> feature =
        //                mServiceFeatureTable.populateFromServiceAsync(queryParameters, true, outFields);
        val featureBeNgam = mServiceFeatureTable!!.queryFeaturesAsync(queryParametersBeNgam, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        featureBeNgam.addDoneListener {
            try {
                val result = featureBeNgam.get()
                val iterator = result.iterator()
                var item: Feature
                while (iterator.hasNext()) {
                    item = iterator.next()
                    val value = item.attributes[getString(R.string.trangthai)]
                    var trangThai = resources.getInteger(R.integer.trang_thai_chua_sua_chua)
                    if (value != null) {
                        trangThai = Integer.parseInt(value.toString())
                    }
                    if (trangThai == resources.getInteger(R.integer.trang_thai_chua_sua_chua))
                        mBeNgam++
                }
                displayReport()

            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }


    }

    @SuppressLint("SetTextI18n")
    private fun displayReport() {
        val tongloaitrangthai = mChuaSuaChua + mDangSuaChua + mHoanThanh
        txtTongSuCo!!.text = getString(R.string.nav_thong_ke_tong_su_co) + tongloaitrangthai

        mChuaSuaChua -= mBeNgam
        mTxtChuaSua!!.text = mChuaSuaChua.toString() + ""
        mTxtBeNgam!!.text = mBeNgam.toString() + ""
        mTxtDangSua!!.text = mDangSuaChua.toString() + ""
        mTxtHoanThanh!!.text = mHoanThanh.toString() + ""
        var percentChuaSua: Double
        var percentBeNgam: Double
        var percentDangSua: Double
        var percentHoanThanh: Double
        percentHoanThanh = 0.0
        percentDangSua = percentHoanThanh
        percentBeNgam = percentDangSua
        percentChuaSua = percentBeNgam
        if (tongloaitrangthai > 0) {
            percentChuaSua = mChuaSuaChua.toDouble() * 100 / tongloaitrangthai
            percentBeNgam = mBeNgam.toDouble() * 100 / tongloaitrangthai
            percentDangSua = mDangSuaChua.toDouble() * 100 / tongloaitrangthai
            percentHoanThanh = mHoanThanh.toDouble() * 100 / tongloaitrangthai
        }
        mTxtPhanTramChuaSua!!.text = BigDecimal(percentChuaSua).setScale(2, RoundingMode.HALF_UP).toDouble().toString() + "%"
        mTxtPhanTramBeNgam!!.text = BigDecimal(percentBeNgam).setScale(2, RoundingMode.HALF_UP).toDouble().toString() + "%"
        mTxtPhanTramDangSua!!.text = BigDecimal(percentDangSua).setScale(2, RoundingMode.HALF_UP).toDouble().toString() + "%"
        mTxtPhanTramHoanThanh!!.text = BigDecimal(percentHoanThanh).setScale(2, RoundingMode.HALF_UP).toDouble().toString() + "%"
        var mChart = findViewById<PieChart>(R.id.piechart)
        mChart = configureChart(mChart)

        mChart = setData(mChart)
        mChart.animateXY(1500, 1500)
    }

    fun configureChart(chart: PieChart): PieChart {
        chart.setHoleColor(resources.getColor(android.R.color.background_dark))
        chart.holeRadius = 60f
        chart.setDescription("")
        chart.transparentCircleRadius = 5f
        chart.setDrawCenterText(true)
        chart.isDrawHoleEnabled = false
        chart.rotationAngle = 0f
        chart.isRotationEnabled = true

        chart.setUsePercentValues(false)

        val legend = chart.legend
        legend.position = Legend.LegendPosition.LEFT_OF_CHART
        return chart
    }

    private fun setData(chart: PieChart): PieChart {
        val yVals1 = ArrayList<Entry>()

        yVals1.add(Entry(mChuaSuaChua.toFloat(), 0))
        yVals1.add(Entry(mBeNgam.toFloat(), 1))
        yVals1.add(Entry(mDangSuaChua.toFloat(), 2))
        yVals1.add(Entry(mHoanThanh.toFloat(), 3))
        val xVals = ArrayList<String>()
        xVals.add(getString(R.string.SuCo_TrangThai_ChuaSuaChua))
        xVals.add(getString(R.string.SuCo_TrangThai_ChuaSuaChuaBeNgam))
        xVals.add(getString(R.string.SuCo_TrangThai_DangSuaChua))
        xVals.add(getString(R.string.SuCo_TrangThai_HoanThanh))


        val set1 = PieDataSet(yVals1, "")
        set1.sliceSpace = 0f
        val colors = ArrayList<Int>()
        colors.add(resources.getColor(android.R.color.holo_red_light))
        colors.add(resources.getColor(android.R.color.holo_blue_light))
        colors.add(resources.getColor(android.R.color.holo_orange_light))
        colors.add(resources.getColor(android.R.color.holo_green_light))
        set1.colors = colors
        set1.valueTextSize = 15f
        val data = PieData(xVals, set1)
        //        data.setValueTextSize(20);
        data.isHighlightEnabled = true
        chart.data = data
        chart.highlightValues(null)
        //        chart.invalidate();
        return chart
    }
}
