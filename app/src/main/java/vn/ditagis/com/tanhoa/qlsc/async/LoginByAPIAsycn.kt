package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView

import org.json.JSONException
import org.json.JSONObject
import vn.ditagis.com.tanhoa.qlsc.R

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.User
import java.io.Reader


class LoginByAPIAsycn(@field:SuppressLint("StaticFieldLeak")
                      private val mActivity: Activity, private val mDelegate: LoginByAPIAsycn.AsyncResponse) :
        AsyncTask<String, Void, Boolean?>() {
    private var mDialog: Dialog? = null
    private val mApplication: DApplication

    interface AsyncResponse {
        fun processFinish(success: Boolean?)
    }

    init {
        mApplication = mActivity.applicationContext as DApplication
    }

    override fun onPreExecute() {
        super.onPreExecute()
        val layout = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null) as LinearLayout
        val txtTitle = layout.findViewById<TextView>(R.id.txt_progress_dialog_title)
        txtTitle.text = "Đang kết nối"
        mDialog = Dialog(mActivity)
        mDialog!!.setContentView(layout)
        mDialog!!.show()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun doInBackground(vararg params: String): Boolean? {
        val userName = params[0]
        val pin = params[1]
        //        String passEncoded = (new EncodeMD5()).encode(pin + "_DITAGIS");
        // Do some validation here
        val urlParameters = String.format("Username=%s&Password=%s", userName, pin)
        val urlWithParam = String.format("%s?%s", Constant.UrlApi.LOGIN, urlParameters)
        try {
            //            + "&apiKey=" + API_KEY
            val url = URL(urlWithParam)
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.requestMethod = Constant.HTTPRequest.GET_METHOD
                conn.connect()
                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream) as Reader?)
                val stringBuilder = StringBuilder()
                val line = bufferedReader.readLine()
                if (line != null) stringBuilder.append(line)
                bufferedReader.close()
                conn.disconnect()
                val token = stringBuilder.toString().replace("\"", "")
                if (checkAccess(token)!!) {
                    mApplication.userDangNhap = User()
                    mApplication.userDangNhap!!.token = token
                    getProfile()
                    mApplication.userDangNhap!!.passWord = pin
                    mApplication.userDangNhap!!.userName = userName

                } else {
                    mApplication.userDangNhap = null
                }
            } catch (e1: Exception) {
                mApplication.userDangNhap = null
                Log.e("Lỗi login", e1.toString())
                conn.disconnect()
                return false
            }
        } catch (e: Exception) {
            mApplication.userDangNhap = null
            Log.e("ERROR", e.message, e)
            return false
        }
        return true
    }


    override fun onPostExecute(result: Boolean?) {
        //        if (user != null) {
        mDialog!!.dismiss()
        this.mDelegate.processFinish(result)
        //        }
    }

    private fun checkAccess(token: String): Boolean? {
        var isAccess = false
        try {
            val url = URL(Constant.UrlApi.IS_ACCESS)
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.doOutput = false
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", token)
                conn.connect()

                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                val line = bufferedReader.readLine()
                if (line == "true")
                    isAccess = true
                bufferedReader.close()

            } catch (e: Exception) {
                Log.e("error", e.toString())
            } finally {
                conn.disconnect()
            }
        } catch (e: Exception) {
            Log.e("error", e.toString())
        }

        return isAccess

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getProfile() {

        //        String API_URL = "http://sawagis.vn/tanhoa1/api/Account/Profile";
        try {
            val url = URL(Constant.UrlApi.PROFILE)
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.doOutput = false
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", mApplication.userDangNhap!!.token)
                conn.connect()

                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                val line = bufferedReader.readLine()
                if (line != null) pajsonRouteeJSon(line)

            } catch (e: Exception) {
                Log.e("lỗi lấy profile", e.toString())
            } finally {
                conn.disconnect()
            }
        } catch (e: Exception) {
            Log.e("lỗi lấy profile", e.toString())
        } finally {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(JSONException::class)
    private fun pajsonRouteeJSon(data: String?): String {
        if (data == null)
            return ""
        val displayName = ""
        val myData = "{ \"account\": [$data]}"
        val jsonData = JSONObject(myData)
        val jsonRoutes = jsonData.getJSONArray("account")
        for (i in 0 until jsonRoutes.length()) {
            val jsonRoute = jsonRoutes.getJSONObject(i)
            mApplication.userDangNhap!!.displayName = jsonRoute.getString(Constant.FieldAccount.DISPLAY_NAME)
            mApplication.userDangNhap!!.role = jsonRoute.getString(Constant.FieldAccount.ROLE)
            mApplication.userDangNhap!!.groupRole = jsonRoute.getString(Constant.FieldAccount.GROUP_ROLE)
        }
        return displayName

    }
}
