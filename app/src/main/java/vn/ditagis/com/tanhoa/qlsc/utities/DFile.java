package vn.ditagis.com.tanhoa.qlsc.utities;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import vn.ditagis.com.tanhoa.qlsc.R;

/**
 * Created by ThanLe on 12/8/2017.
 */

public class DFile {
    public static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static File getImageFile(Context context) {
        String path = Environment.getExternalStorageDirectory().getPath();
        File outFile = new File(path, context.getResources().getString(R.string.path_saveImage));
        if (!outFile.exists())
            outFile.mkdir();
        File f = new File(outFile, "xxx.png");
        return f;
    }
    public static File getPDFFile(Context context, String name) {
        String path = Environment.getExternalStorageDirectory().getPath();
        File outFile = new File(path, context.getResources().getString(R.string.path_saveImage));
        if (!outFile.exists())
            outFile.mkdir();
        File f = new File(outFile, name);
        return f;
    }
    public static File getDocFile(Context context, String name) {
        String path = Environment.getExternalStorageDirectory().getPath();
        File outFile = new File(path, context.getResources().getString(R.string.path_saveImage));
        if (!outFile.exists())
            outFile.mkdir();
        File f = new File(outFile, name);
        return f;
    }

}
