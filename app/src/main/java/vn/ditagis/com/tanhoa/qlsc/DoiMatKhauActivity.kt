package vn.ditagis.com.tanhoa.qlsc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_doi_mat_khau.*

import vn.ditagis.com.tanhoa.qlsc.async.ChangePasswordAsycn
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.utities.Preference


class DoiMatKhauActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mApplication: DApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doi_mat_khau)

        btn_change_password.setOnClickListener(this)
        val keyListener = View.OnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                changPassword()
            }
            false
        }
        etxt_change_password_new_confirm!!.setOnKeyListener(keyListener)
        mApplication = application as DApplication;
    }

    private fun validate(): Boolean {
        if (etxt_change_password_new!!.text.toString().trim { it <= ' ' } != etxt_change_password_new_confirm!!.text.toString().trim { it <= ' ' }) {
            txt_change_password_validation!!.visibility = View.VISIBLE
            txt_change_password_validation!!.text = getString(R.string.validate_change_password)
            return false
        }
        if (etxt_change_password_new!!.text.toString().trim { it <= ' ' }.length < 6) {
            txt_change_password_validation!!.visibility = View.VISIBLE
            txt_change_password_validation!!.text = getString(R.string.validate_change_password_lack)
            return false
        }
        txt_change_password_validation!!.visibility = View.GONE
        return true
    }

    private fun changPassword() {
        val oldPassword = etxt_change_password_old!!.text.toString().trim { it <= ' ' }
        val newPassword = etxt_change_password_new_confirm!!.text.toString().trim { it <= ' ' }
        if (validate()) {
            val asycn = ChangePasswordAsycn(this,
                    object : ChangePasswordAsycn.AsyncResponse {
                        override fun processFinish(output: Boolean?) {
                            if (output!!) {
                                txt_change_password_validation!!.visibility = View.GONE
                                Toast.makeText(this@DoiMatKhauActivity, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                                mApplication.userDangNhap!!.passWord = newPassword
                                val password = Preference.instance.loadPreference(getString(R.string.preference_password))
                                if (password != null)
                                    Preference.instance.savePreferences(getString(R.string.preference_password), newPassword)
                                this@DoiMatKhauActivity.finish()

                            } else {
                                txt_change_password_validation!!.visibility = View.VISIBLE
                                txt_change_password_validation!!.text = getString(R.string.validate_change_password_fail)
                            }
                        }

                    })
            asycn.execute(oldPassword, newPassword)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_change_password -> changPassword()
        }
    }


}
