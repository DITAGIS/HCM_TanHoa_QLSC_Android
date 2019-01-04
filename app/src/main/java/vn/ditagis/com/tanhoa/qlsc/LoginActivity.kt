package vn.ditagis.com.tanhoa.qlsc

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.Toast

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

import kotlinx.android.synthetic.main.activity_login.*
import vn.ditagis.com.tanhoa.qlsc.async.CheckVersionAsycn
import vn.ditagis.com.tanhoa.qlsc.async.LoginByAPIAsycn
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.VersionInfo
import vn.ditagis.com.tanhoa.qlsc.utities.CheckConnectInternet
import vn.ditagis.com.tanhoa.qlsc.utities.NotifyService
import vn.ditagis.com.tanhoa.qlsc.utities.Preference

class LoginActivity : AppCompatActivity(), View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private var mApplication: DApplication? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mApplication = application as DApplication
        mApplication!!.channelID = 0
        try {
            txt_version_login!!.text = packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        btnLogin.setOnClickListener(this)

        //        txtUsername.setText("ditagis");
        //        txtPassword.setText("ditagis@123");
        create()

        val intent = Intent(this@LoginActivity, NotifyService::class.java)
        startService(intent)
        //        startService(new Intent(getBaseContext(), SocketServiceProvider.class));

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun create() {
        Preference.instance.mContext = this
        val username = Preference.instance.loadPreference(getString(R.string.preference_username))
        val password = Preference.instance.loadPreference(getString(R.string.preference_password))
        if (username != null && !username.isEmpty()) {
            txtUsername!!.setText(username)
        }

        if (password != null && !password.isEmpty()) {
            txtPassword!!.setText(password)
            chk_login_save_password!!.isChecked = true
        }

        try {
            if (!mApplication!!.isCheckedVersion) {
                mApplication!!.isCheckedVersion = true
                CheckVersionAsycn(this,
                        object : CheckVersionAsycn.AsyncResponse {
                            override fun processFinish(versionInfo: VersionInfo?) {
                                if (versionInfo != null) {
                                    val builder = AlertDialog.Builder(this@LoginActivity, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                                    builder.setCancelable(false)
                                            .setPositiveButton("CẬP NHẬT") { _, _ -> goURLBrowser(versionInfo.link) }.setTitle("Có phiên bản mới")
                                            .setNegativeButton("HỦY") { _, _ ->
                                                if (chk_login_save_password!!.isChecked)
                                                    login()
                                            }
                                    var isDeveloper = false
                                    if (versionInfo.type != "RELEASE") {
                                        val anInt = Settings.Secure.getInt(this@LoginActivity.contentResolver,
                                                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0)
                                        if (anInt != 0)
                                            isDeveloper = true

                                    }
                                    if (isDeveloper)
                                        builder.setMessage("Bạn là người phát triển ứng dụng! Bạn có muốn cập nhật lên phiên bản " + versionInfo.versionCode + "?")
                                    else
                                        builder.setMessage("Bạn có muốn cập nhật lên phiên bản " + (versionInfo.versionCode + "?"))
                                    val dialog = builder.create()
                                    dialog.show()

                                } else {
                                    Toast.makeText(this@LoginActivity, "Phiên bản hiện tại là mới nhất", Toast.LENGTH_LONG).show()
                                    if (chk_login_save_password!!.isChecked) {
                                        login()
                                    }
                                }
                            }

                        }).execute(packageManager.getPackageInfo(packageName, 0).versionName)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(this, "Có lỗi xảy ra khi kiểm tra phiên bản", Toast.LENGTH_LONG).show()
        }

    }

    private fun goURLBrowser(url: String) {
        var localUrl = url
        if (!localUrl.startsWith("http://") && !localUrl.startsWith("https://"))
            localUrl = "http://$localUrl"

        val webpage = Uri.parse(localUrl)
        val intent = Intent(Intent.ACTION_VIEW, webpage)

        try {
            startActivity(intent)
        } catch (ignored: Exception) {
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun login() {
        if (!CheckConnectInternet.isOnline(this)) {
            txt_login_validation.setText(R.string.validate_no_connect)
            txt_login_validation.visibility = View.VISIBLE
        } else {
            txt_login_validation.visibility = View.GONE

            val userName = txtUsername!!.text.toString().trim { it <= ' ' }
            val passWord = txtPassword!!.text.toString().trim { it <= ' ' }
            if (userName.isEmpty() || passWord.isEmpty()) {
                handleInfoLoginEmpty()
            } else {
                val loginAsycn = LoginByAPIAsycn(this,
                        object : LoginByAPIAsycn.AsyncResponse {
                            override fun processFinish(success: Boolean?) {
                                if (mApplication!!.userDangNhap != null)
                                    handleLoginSuccess()
                                else
                                    handleLoginFail()
                            }

                        })
                loginAsycn.execute(userName, passWord)
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun handleInfoLoginEmpty() {
        txt_login_validation.setText(R.string.info_login_empty)
        txt_login_validation.visibility = View.VISIBLE
    }

    private fun handleLoginFail() {
        txt_login_validation.setText(R.string.validate_login_fail)
        txt_login_validation.visibility = View.VISIBLE
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun handleLoginSuccess() {
        txtUsername!!.setText("")
        txtPassword!!.setText("")

        Preference.instance.savePreferences(getString(R.string.preference_username), mApplication!!.userDangNhap!!.userName!!)
        if (chk_login_save_password!!.isChecked)
            Preference.instance.savePreferences(getString(R.string.preference_password), mApplication!!.userDangNhap!!.passWord!!)
        else
            Preference.instance.deletePreferences(getString(R.string.preference_password))
        Preference.instance.savePreferences(getString(R.string.preference_displayname), mApplication!!.userDangNhap!!.displayName!!)

        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnLogin -> login()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                if (txtPassword!!.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                    login()
                    return true
                }
                return super.onKeyUp(keyCode, event)
            }
            else -> return super.onKeyUp(keyCode, event)
        }
    }

    override fun onConnected(bundle: Bundle?) {

    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}
