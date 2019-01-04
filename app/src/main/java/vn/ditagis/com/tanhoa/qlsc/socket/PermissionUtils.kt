package vn.ditagis.com.tanhoa.qlsc.socket


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast

import java.util.ArrayList
import java.util.HashMap


/**
 * Created by Jaison on 02/05/17.
 */

class PermissionUtils(internal var context: Context, callback: PermissionResultCallback) {

    private var currentActivity: Activity = context as Activity

    private var permissionResultCallback: PermissionResultCallback = callback


    private var permissionList = ArrayList<String>()
    private var listPermissionsNeeded = ArrayList<String>()


    private var dialogContent = ""
    private var reqCode: Int = 0


    /**
     * Check the API Level & Permission
     *
     * @param permissions
     * @param dialog_content
     * @param request_code
     */

    @SuppressLint("ObsoleteSdkInt")
    fun checkPermission(permissions: ArrayList<String>, dialog_content: String, request_code: Int) {
        this.permissionList = permissions
        this.dialogContent = dialog_content
        this.reqCode = request_code

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions(permissions, request_code)) {
                permissionResultCallback.permissionGranted(request_code)
                Log.i("all permissions", "granted")
                Log.i("proceed", "to callback")
            }
        } else {
            permissionResultCallback.permissionGranted(request_code)

            Log.i("all permissions", "granted")
            Log.i("proceed", "to callback")
        }

    }


    /**
     * Check and request the Permissions
     *
     * @param permissions
     * @param request_code
     * @return
     */

    private fun checkAndRequestPermissions(permissions: ArrayList<String>, request_code: Int): Boolean {

        if (permissions.size > 0) {
            listPermissionsNeeded = ArrayList()

            for (i in permissions.indices) {
                val hasPermission = ContextCompat.checkSelfPermission(currentActivity, permissions[i])

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions[i])
                }

            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(currentActivity, listPermissionsNeeded.toTypedArray(), request_code)
                return false
            }
        }

        return true
    }

    /**
     *
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty()) {
                val perms = HashMap<String, Int>()

                for (i in permissions.indices) {
                    perms[permissions[i]] = grantResults[i]
                }

                val pendingPermissions = ArrayList<String>()

                for (i in listPermissionsNeeded.indices) {
                    if (perms[listPermissionsNeeded[i]] != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, listPermissionsNeeded[i]))
                            pendingPermissions.add(listPermissionsNeeded[i])
                        else {
                            Log.i("Go to settings", "and enable permissions")
                            permissionResultCallback.neverAskAgain(reqCode)
                            Toast.makeText(currentActivity, "Go to settings and enable permissions", Toast.LENGTH_LONG).show()
                            return
                        }
                    }

                }

                if (pendingPermissions.size > 0) {
                    showMessageOKCancel(dialogContent,
                            DialogInterface.OnClickListener { _, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> checkPermission(permissionList, dialogContent, reqCode)
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                        Log.i("permisson", "not fully given")
                                        if (permissionList.size == pendingPermissions.size)
                                            permissionResultCallback.permissionDenied(reqCode)
                                        else
                                            permissionResultCallback.partialPermissionGranted(reqCode, pendingPermissions)
                                    }
                                }
                            })

                } else {
                    Log.i("all", "permissions granted")
                    Log.i("proceed", "to next step")
                    permissionResultCallback.permissionGranted(reqCode)

                }


            }
        }
    }


    /**
     * Explain why the app needs permissions
     *
     * @param message
     * @param okListener
     */
    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(currentActivity)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    interface PermissionResultCallback {
        fun permissionGranted(request_code: Int)
        fun partialPermissionGranted(request_code: Int, granted_permissions: ArrayList<String>)
        fun permissionDenied(request_code: Int)
        fun neverAskAgain(request_code: Int)
    }
}


