package vn.ditagis.com.tanhoa.qlsc

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_show_capture.*

import vn.ditagis.com.tanhoa.qlsc.entities.DApplication

class ShowCaptureActivity : AppCompatActivity() {
    private var mApplication: DApplication? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_show_capture)
        mApplication = application as DApplication


        val capture = mApplication!!.capture
        if (capture != null) {
            val decodeBitmap = BitmapFactory.decodeByteArray(capture, 0, capture.size)
            show_capture_imageView.setImageBitmap(decodeBitmap)
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.show_capture_cancel -> goHomeCancel()
            R.id.show_capture_ok -> goHome()
        }
    }

    override fun onBackPressed() {
        goHomeCancel()
    }


    private fun goHome() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun goHomeCancel() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}
