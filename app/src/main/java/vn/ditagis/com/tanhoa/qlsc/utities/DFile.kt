package vn.ditagis.com.tanhoa.qlsc.utities

import android.content.Context
import android.os.Environment

import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat

import vn.ditagis.com.tanhoa.qlsc.R

/**
 * Created by ThanLe on 12/8/2017.
 */

object DFile {

//    fun getImageFile(context: Context): File {
//        val path = Environment.getExternalStorageDirectory().path
//        val outFile = File(path, context.resources.getString(R.string.path_saveImage))
//        if (!outFile.exists())
//            outFile.mkdir()
//        return File(outFile, "xxx.png")
//    }

    fun getPDFFile(context: Context, name: String): File {
        val path = Environment.getExternalStorageDirectory().path
        val outFile = File(path, context.resources.getString(R.string.path_saveImage))
        if (!outFile.exists())
            outFile.mkdir()
        return File(outFile, name)
    }

    fun getDocFile(context: Context, name: String): File {
        val path = Environment.getExternalStorageDirectory().path
        val outFile = File(path, context.resources.getString(R.string.path_saveImage))
        if (!outFile.exists())
            outFile.mkdir()
        return File(outFile, name)
    }

}
