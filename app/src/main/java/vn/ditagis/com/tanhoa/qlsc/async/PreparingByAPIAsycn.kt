package vn.ditagis.com.tanhoa.qlsc.async

import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo
import vn.ditagis.com.tanhoa.qlsc.services.GetDMA
import vn.ditagis.com.tanhoa.qlsc.services.GetThietBi
import vn.ditagis.com.tanhoa.qlsc.services.GetVatTu
import java.io.Reader

class PreparingByAPIAsycn(private val mActivity: Activity, private val mDelegate: AsyncResponse) : AsyncTask<Void, Boolean, Boolean?>() {
    private var mDialog: Dialog? = null
    private val mApplication: DApplication

    interface AsyncResponse {

        fun processFinish(success: Boolean?)
    }

    init {
        mApplication = mActivity.application as DApplication
    }

    override fun onPreExecute() {
        super.onPreExecute()
        val layout = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null) as LinearLayout
        val txtTitle = layout.findViewById<TextView>(R.id.txt_progress_dialog_title)
        txtTitle.text = "Đang khởi tạo bản đồ..."
        mDialog = Dialog(mActivity)
        mDialog!!.setCancelable(false)
        mDialog!!.setContentView(layout)
        mDialog!!.show()
    }

    override fun doInBackground(vararg params: Void): Boolean? {
        try {
            getLayerInfoAPI()
            var current = 0
            val sum = 3
            GetVatTu(mApplication, object : GetVatTu.AsyncResponse {
                override fun processFinish() {
                    current++
                    if (current == sum)
                        publishProgress(true)
                }
            }).execute()
            GetDMA(mApplication, object : GetDMA.AsyncResponse {
                override fun processFinish() {
                    current++
                    if (current == sum)
                        publishProgress(true)
                }

            }).execute()
            GetThietBi(mApplication, object : GetThietBi.AsyncResponse {
                override fun processFinish() {
                    current++
                    if (current == sum)
                        publishProgress(true)
                }
            }).execute()

        } catch (e: Exception) {
            Log.e("Lỗi lấy danh sách DMA", e.toString())
            publishProgress()
        }

        return null
    }

    override fun onProgressUpdate(vararg values: Boolean?) {
        super.onProgressUpdate(*values)
        if (values.isNotEmpty() && values[0]!! && mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
            this.mDelegate.processFinish(values[0])
        }

    }

    private fun getLayerInfoAPI() {
        try {
            val url = URL(Constant.UrlApi.LAYER_INFO)
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.doOutput = false
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", mApplication.userDangNhap!!.token)
                conn.connect()

                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream) as Reader?)
                val builder = StringBuilder()
                var line: String? = null
                while (true) {
                    line = bufferedReader.readLine()
                    if (line == null) break
                    builder.append(line)
                }
                pajsonRouteeJSon(builder.toString())
            } catch (e: Exception) {
                Log.e("error", e.toString())
            } finally {
                conn.disconnect()
            }
        } catch (e: Exception) {
            Log.e("Lỗi lấy LayerInfo", e.toString())
        }

    }

    @Throws(JSONException::class)
    private fun pajsonRouteeJSon(data: String?) {
        if (data == null)
            return
        val myData = "{ \"layerInfo\": $data}"
        val jsonData = JSONObject(myData)
        val jsonRoutes = jsonData.getJSONArray("layerInfo")
        val layerDTGS = ArrayList<DLayerInfo>()
        for (i in 0 until jsonRoutes.length()) {
            val jsonRoute = jsonRoutes.getJSONObject(i)
            var url = jsonRoute.getString(Constant.FieldSys.LAYER_URL)
            if (url.startsWith("http://113.161.88.180:800/arcgis/rest/services/TanHoa/SuCo/FeatureServer")) {
                url = url.replace("TanHoa/SuCo", "TanHoa/THSuCo")
            } else if (url.startsWith("http://113.161.88.180:800/arcgis/rest/services/TanHoa/TanHoaSuCo/FeatureServer")) {
                url = url.replace("TanHoa/TanHoaSuCo", "TanHoa/THSuCo")

            }
            var definition: String? = jsonRoute.getString(Constant.FieldSys.DEFINITION)
            if (definition!!.contains("null"))
                definition = null
            var addFields = ""
            try {
                addFields = jsonRoute.getString(Constant.FieldSys.ADD_FIELD)

            } catch (ignored: Exception) {

            }

            val outFields = jsonRoute.getString(Constant.FieldSys.OUT_FIELD)
            var noOutFields = ""
            val id = jsonRoute.getString(Constant.FieldSys.LAYER_ID)
            if (id == Constant.IDLayer.ID_SU_CO_THONG_TIN_TABLE)
                noOutFields = noOutFields + "," + Constant.NoOutFieldSuCo.DON_VI
            layerDTGS.add(DLayerInfo(
                    id,
                    jsonRoute.getString(Constant.FieldSys.LAYER_TITLE),
                    url,
                    jsonRoute.getBoolean(Constant.FieldSys.IS_CREATE),
                    jsonRoute.getBoolean(Constant.FieldSys.IS_DELETE),
                    jsonRoute.getBoolean(Constant.FieldSys.IS_EDIT),
                    jsonRoute.getBoolean(Constant.FieldSys.IS_VIEW),
                    definition,
                    outFields,
                    noOutFields,
                    addFields,
                    jsonRoute.getString(Constant.FieldSys.UPDATE_FIELD)))


        }
        mApplication.lstFeatureLayerDTG = layerDTGS

    }

}
