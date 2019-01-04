package vn.ditagis.com.tanhoa.qlsc.utities

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper


import io.socket.client.Socket
import io.socket.emitter.Emitter
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication

class SocketServiceProvider : Service() {
    private var signalApplication: DApplication? = null

    private val onStaff = Emitter.Listener { Handler(Looper.getMainLooper()).post { } }

    private val onDisconnect = Emitter.Listener { Handler(Looper.getMainLooper()).post { } }

    private val onConnectError = Emitter.Listener { Handler(Looper.getMainLooper()).post { } }

    private val message = Emitter.Listener {
        //        val result = it[0] as JSONObject
        Handler(mainLooper)
                .post {
                    //                    try {
////                        val username = result.getString("username")
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
                }
    }


    override fun onCreate() {
        if (isInstanceCreated) {
            return
        }
        super.onCreate()

        signalApplication = application as DApplication


        signalApplication!!.socket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        signalApplication!!.socket!!.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
        signalApplication!!.socket!!.on(Socket.EVENT_CONNECT, onStaff)

        //@formatter:off
        signalApplication!!.socket!!.on("message", message)
        //@formatter:on

    }

    @SuppressLint("WrongConstant")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (isInstanceCreated) {
            return Service.START_FLAG_RETRY
        }
        super.onStartCommand(intent, flags, startId)
        connectConnection()
        return Service.START_STICKY
    }

    private fun connectConnection() {
        instance = this
        signalApplication!!.socket!!.connect()
    }

    private fun disconnectConnection() {
        instance = null
        signalApplication!!.socket!!.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()


        signalApplication!!.socket!!.off(Socket.EVENT_CONNECT, onStaff)
        signalApplication!!.socket!!.off(Socket.EVENT_DISCONNECT, onDisconnect)
        signalApplication!!.socket!!.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
        signalApplication!!.socket!!.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)

        //@formatter:off
        signalApplication!!.socket!!.off("message", message)
        //@formatter:on

        disconnectConnection()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {

        var instance: SocketServiceProvider? = null

        val isInstanceCreated: Boolean
            get() = instance != null
    }

}