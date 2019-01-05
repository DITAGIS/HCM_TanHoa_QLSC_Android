package vn.ditagis.com.tanhoa.qlsc.utities

import android.os.AsyncTask
import android.util.Log

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import java.io.Reader

class APICompleteAsync(private val mApplication: DApplication, private val mIDSuCo: String) : AsyncTask<Void, Void, Void>() {

    private fun send() {
        try {
            val apiURL = String.format(Constant.UrlApi.COMPLETE, mIDSuCo)

            val url = URL(apiURL)
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.doOutput = false
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", mApplication.userDangNhap!!.token)
                conn.connect()

                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream) as Reader?)
                val builder = StringBuilder()
                var line: String
                while (true) {
                    line = bufferedReader.readLine()
                    if (line == null)
                        break
                    builder.append(line)
                }
            } catch (e: Exception) {
                Log.e("error", e.toString())
            } finally {
                conn.disconnect()
            }
        } catch (e: Exception) {
            Log.e("Lỗi lấy LayerInfo", e.toString())
        }

    }

    override fun doInBackground(vararg voids: Void): Void? {
        send()
        return null
    }
}
