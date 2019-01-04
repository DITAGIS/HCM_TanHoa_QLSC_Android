package vn.ditagis.com.tanhoa.qlsc.utities


import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import io.socket.emitter.Emitter
import vn.ditagis.com.tanhoa.qlsc.ClickNotificationHandlingActivity
import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication

// A Service is an application component that can perform long-running operations
// in the background and does not provide a user interface.
@RequiresApi(api = Build.VERSION_CODES.O)
class NotifyService : Service() {
    private var notifyServiceReceiver: NotifyServiceReceiver? = null
    private var mApplication: DApplication? = null
    private var mContext: Context? = null

    // onStartCommand() is called upon calling the startService(Intent) in NotifyActivity
    // Checkout the Log.d and you will see that onStartCommand() execute on the UI thread and
    // not on a worker thread. If you are planning to do a computationally intensive operation in
    // onStartCommand() then create a thread or AsyncTask or loader to do it. Else, you will block
    // the UI.

    private val onInfinity =Emitter.Listener { args ->
        if (args != null && args!!.size > 0) {
            Log.d("Nhận", args!![0].toString())
        }
    }
    internal var onNhanViec =Emitter.Listener { args ->
        Handler(Looper.getMainLooper()).post {
            try {
                if (args != null && args!!.size > 0 && mApplication!!.userDangNhap != null
                        && mApplication!!.userDangNhap!!.userName != null) {
                    val title = "NhanViec"
                    val myData = "{ \"" + title + "\": [" + args!![0].toString() + "]}"

                    val jsonData = JSONObject(myData)
                    val jsonRoutes = jsonData.getJSONArray(title)
                    var suCo = ""
                    var nhanVien = ""
                    for (i in 0 until jsonRoutes.length()) {
                        val jsonRoute = jsonRoutes.getJSONObject(i)
                        suCo = jsonRoute.getString("suCo")
                        nhanVien = jsonRoute.getString("nhanVien")
                    }
                    if (nhanVien == mApplication!!.userDangNhap!!.userName) {
                        mApplication!!.getDiemSuCo.idSuCo = suCo
                        showNotify()
                        Log.d("Nhận việc", args!![0].toString())
                    }

                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "Có lỗi khi nhận thông báo " + e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "NotifyService:onCreate")
        notifyServiceReceiver = NotifyServiceReceiver()
        mApplication = application as DApplication
        mContext = baseContext
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        //        new Thread(() -> {
        val app = application as DApplication
        val socket = app.socket
        val handler = Handler()
        val delay = 5000 //milliseconds
        handler.postDelayed(object : Runnable {
            override fun run() {
                //do something
                if (app.getmLocation() != null) {
                    Log.d("gửi", "hhi")
                    if (mApplication!!.userDangNhap != null && mApplication!!.userDangNhap!!.userName != null)
                        socket!!.emit(Constant.Socket.EVENT_STAFF_NAME, Constant.Socket.APP_ID + "," + mApplication!!.userDangNhap!!.userName)
                    val emit1 = socket!!.emit(Constant.Socket.EVENT_LOCATION,
                            app.getmLocation()!!.latitude.toString() + "," + app.getmLocation()!!.longitude)
                    app.setmLocation(null)
                    Log.d("Kết quả vị trí", emit1.hasListeners(Constant.Socket.EVENT_LOCATION).toString() + "")
                }
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
        socket!!.on(Constant.Socket.EVENT_STAFF_NAME, onInfinity)
        socket.on(Constant.Socket.EVENT_LOCATION, onInfinity)
        socket.on(Constant.Socket.EVENT_GIAO_VIEC, onNhanViec)

        socket.connect()

        //        }).start();

        return Service.START_STICKY

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showNotify() {
        try {

            val intentFilter = IntentFilter()
            Log.d(TAG, "NotifyService:registerReceiver")
            registerReceiver(notifyServiceReceiver, intentFilter)

            val context = applicationContext
            val notificationTitle = "Bạn vừa nhận sự cố " + mApplication!!.getDiemSuCo.idSuCo
            val notificationText = "Click vào đây để biết thêm chi tiết"
            val myIntent = Intent(mContext, ClickNotificationHandlingActivity::class.java)

            val pendingIntent = PendingIntent.getActivity(context, 0, myIntent,
                    PendingIntent.FLAG_ONE_SHOT)

            val notificationChannel = NotificationChannel(CHANNEL_ID,
                    "Giao việc", NotificationManager.IMPORTANCE_HIGH)

            val notificationBuilder = NotificationCompat.Builder(this@NotifyService, CHANNEL_ID)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setSmallIcon(R.drawable.logo)
                    .setContentIntent(pendingIntent) // note the pending intent to launch browser

            val notification = notificationBuilder.build()

            notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel)
                mApplication!!.channelID = mApplication!!.channelID + 1
                notificationManager.notify(mApplication!!.channelID, notification)
            }

        } catch (e: Exception) {
            Toast.makeText(mContext, "Có lỗi khi nhận thông báo", Toast.LENGTH_SHORT).show()
        }

    }

    // onDestroy corresponds to onCreate. It performs any final cleanup before an activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        //        Log.d(TAG, "NotifyService:onDestroy/unregisterReceiver");
        //        this.unregisterReceiver(notifyServiceReceiver);
    }

    override fun onBind(arg0: Intent): IBinder? {
        Log.d(TAG, "NotifyService:onBind()")
        return null
    }

    // Base class for code that will receive intents sent by sendBroadcast().
    // The receiver needs to be registered (see registerReceiver) and specify a certain
    // intent it listens for  (e.g. the intent should specifies the same action)

    inner class NotifyServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                Log.d(TAG, "NotifyService:onReceive(): stop service $intent")
                val notificationManager: NotificationManager?
                val rqs = intent.getIntExtra(STOP_SERVICE_BROADCAST_KEY, 0)
                if (rqs == RQS_STOP_SERVICE) {

                    // NotificationManager - Class to notify the user of events that happen.
                    // Notifications can take different forms: 1) A persistent icon that goes in
                    // the status bar 2) Turning on or flashing LEDs on the device; 3) Alerting the
                    // user by flashing the backlight, playing a sound, or vibrating.
                    // NOTIFICATION_SERVICE - Use with Context.getSystemService(Class) to retrieve a
                    // NotificationManager for informing the user of background events.
                    // Finally, cancelAll - Cancel all previously shown notifications.

                    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager?.cancelAll()
                }
                // Stop the service, if it was previously started. This is the same as
                // calling stopService(Intent) for this particular service.  stopSelf()
                // will call onDestory()

                stopSelf()
            } catch (e: Exception) {
                Toast.makeText(context, "Có lỗi khi nhận thông báo", Toast.LENGTH_SHORT).show()
            }

        }
    }

    companion object {

        val CHANNEL_ID = "notification channel"
        internal val STOP_SERVICE_BROADCAST_KEY = "StopServiceBroadcastKey"
        internal val RQS_STOP_SERVICE = 1
        private val TAG = "NotifyService"
    }

}
