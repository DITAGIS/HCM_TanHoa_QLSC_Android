package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import android.widget.LinearLayout
import android.widget.TextView

import java.io.ByteArrayOutputStream

import vn.ditagis.com.tanhoa.qlsc.R


/**
 * Created by ThanLe on 4/16/2018.
 */

class CameraAsync(activity: Activity, @field:SuppressLint("StaticFieldLeak")
private val mDelegate: AsyncResponse) : AsyncTask<ByteArray, Void, ByteArray?>() {
    //    private val mApplication: DApplication? = null
    private val mDialog: Dialog = Dialog(activity)

    interface AsyncResponse {
        fun processFinish(output: ByteArray?)
    }

    init {
        val inflater = activity.layoutInflater
        val layout = inflater.inflate(R.layout.layout_progress_dialog, null) as LinearLayout
        val txtTitle = layout.findViewById<TextView>(R.id.txt_progress_dialog_title)
        txtTitle.text = "Đang giảm chất lượng hình ảnh..."
        mDialog.setContentView(layout)
        mDialog.show()
    }

    private fun handlingCapture(bytes: ByteArray): ByteArray {
        val decodeBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        val imageMaxSize = 1000000 // 1.0MP
        val height = decodeBitmap.height
        val width = decodeBitmap.width
        val y = Math.sqrt(imageMaxSize / (width.toDouble() / height))
        val x = y / height * width
        val scaledBitmap = Bitmap.createScaledBitmap(decodeBitmap, x.toInt(), y.toInt(), true)

        val stream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        //rotate
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        val matrix = Matrix()
        matrix.postRotate(90f)
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        val outputStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        bitmap.recycle()
        scaledBitmap.recycle()
        return outputStream.toByteArray()
    }

    @SuppressLint("DefaultLocale")
    override fun doInBackground(vararg params: ByteArray): ByteArray? {
        return if (params.isNotEmpty()) {
            handlingCapture(params[0])
        } else null
    }

    override fun onPostExecute(result: ByteArray?) {
        mDialog.dismiss()

        mDelegate.processFinish(result)
    }
}