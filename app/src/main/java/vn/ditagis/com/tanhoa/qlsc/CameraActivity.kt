package vn.ditagis.com.tanhoa.qlsc

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast

import java.io.IOException

import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.vision.CameraSource
import kotlinx.android.synthetic.main.activity_camera.*
import vn.ditagis.com.tanhoa.qlsc.async.CameraAsync
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication

class CameraActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private var mCamera: Camera? = null
    private var mPictureCallback: Camera.PictureCallback? = null
    private var mParameters: Camera.Parameters? = null
    private var surfaceView_fragment_camera: SurfaceView? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mApplication: DApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_camera)
        ButterKnife.bind(this)
        mSurfaceHolder = surfaceView_fragment_camera!!.holder
        mSurfaceHolder!!.addCallback(this)
        mApplication = application as DApplication
    }

    private fun getParameters() {
        mCamera = Camera.open()
        mParameters = mCamera!!.parameters
        mCamera!!.setDisplayOrientation(90)
        mParameters!!.previewFrameRate = 30
        mParameters!!.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE

        //        Camera.Size bestSize = null;
        //        List<Camera.Size> sizeList = mCamera.getParameters().getSupportedJpegThumbnailSizes();
        //        bestSize = sizeList.get(0);
        //        for (Camera.Size size : sizeList) {
        //            if ((size.width * size.height) > (bestSize.width * bestSize.height)) {
        //                bestSize = size;
        //            }
        //        }
        //        mParameters.setPreviewSize(bestSize.width, bestSize.height);
    }

    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        try {
            getParameters()
            mCamera!!.parameters = mParameters
            mSurfaceHolder = surfaceHolder
            mCamera!!.setPreviewDisplay(surfaceHolder)
            mCamera!!.startPreview()
            mPictureCallback =Camera.PictureCallback { bytes, _ ->
                try {
                    CameraAsync(this@CameraActivity, object:CameraAsync.AsyncResponse{
                        override fun processFinish(output: ByteArray?) {
                            if (output != null) {
                                mApplication!!.capture = output
                                val intent = Intent(this@CameraActivity, ShowCaptureActivity::class.java)
                                this@CameraActivity.startActivityForResult(intent, Constant.RequestCode.REQUEST_CODE_SHOW_CAPTURE)
                            } else {
                                Toast.makeText(this@CameraActivity, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }).execute(bytes)

                } catch (e: Exception) {
                    Toast.makeText(this@CameraActivity, "Có lỗi khi chụp ảnh", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    private fun captureImage() {
        mCamera!!.takePicture(null, null, mPictureCallback)
    }

    @Throws(IOException::class)
    private fun turnOnOffFlashCamera() {
        //auto
        if (mCamera!!.parameters.flashMode == Camera.Parameters.FLASH_MODE_OFF) {
            camera_flash!!.setImageResource(R.drawable.ic_flash_auto_white_64dp)
            mCamera = Camera.open()
            getParameters()
            mParameters!!.flashMode = Camera.Parameters.FLASH_MODE_AUTO
            mCamera!!.parameters = mParameters
            mCamera!!.setPreviewDisplay(mSurfaceHolder)
            mCamera!!.startPreview()
        } else if (mCamera!!.parameters.flashMode == Camera.Parameters.FLASH_MODE_AUTO) {
            camera_flash!!.setImageResource(R.drawable.ic_flash_on_white_64dp)
            mCamera = Camera.open()
            getParameters()
            mParameters!!.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            mCamera!!.parameters = mParameters
            mCamera!!.setPreviewDisplay(mSurfaceHolder)
            mCamera!!.startPreview()
        } else if (mCamera!!.parameters.flashMode == Camera.Parameters.FLASH_MODE_TORCH) {
            camera_flash!!.setImageResource(R.drawable.ic_flash_off_white_64dp)
            mCamera = Camera.open()
            getParameters()
            mParameters!!.flashMode = Camera.Parameters.FLASH_MODE_OFF
            mCamera!!.parameters = mParameters
            mCamera!!.setPreviewDisplay(mSurfaceHolder)
            mCamera!!.startPreview()
        }//turn off
        //turn on
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {

    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        goHomeCancel()
    }


    fun goHome() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    fun goHomeCancel() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)

    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.camera_flash -> try {
                turnOnOffFlashCamera()
            } catch (e: Exception) {
                Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
            }

            R.id.camera_back -> goHomeCancel()
            R.id.camera_capture -> captureImage()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            Constant.RequestCode.REQUEST_CODE_SHOW_CAPTURE -> if (resultCode == RESULT_OK)
                goHome()
        }
    }
}
