package vn.ditagis.com.tanhoa.qlsc

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import vn.ditagis.com.tanhoa.qlsc.entities.DApplication

class ClickNotificationHandlingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_click_notification_handling)

        (application as DApplication).isFromNotification = true
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }
}
