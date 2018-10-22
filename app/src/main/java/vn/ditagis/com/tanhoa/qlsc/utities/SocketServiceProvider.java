package vn.ditagis.com.tanhoa.qlsc.utities;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

public class SocketServiceProvider extends Service {
    private DApplication signalApplication;

    public static SocketServiceProvider instance = null;

    public static boolean isInstanceCreated() {
        return instance == null ? false : true;
    }


    @Override
    public void onCreate() {
        if (isInstanceCreated()) {
            return;
        }
        super.onCreate();

        signalApplication = (DApplication) getApplication();


        signalApplication.getSocket().on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        signalApplication.getSocket().on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        signalApplication.getSocket().on(Socket.EVENT_CONNECT, onNhanVien);

        //@formatter:off
        signalApplication.getSocket().on("message"                   , message);
        //@formatter:on

    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isInstanceCreated()) {
            return START_FLAG_RETRY;
        }
        super.onStartCommand(intent, flags, startId);
        connectConnection();
        return START_STICKY;
    }

    private Emitter.Listener onNhanVien = args -> new Handler(Looper.getMainLooper()).post(new Runnable() {

        @Override
        public void run() {

        }
    });

    private Emitter.Listener onDisconnect = args -> new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() {
        }
    });

    private Emitter.Listener onConnectError = args -> new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() {
        }
    });

    private Emitter.Listener message = args -> {
        final JSONObject result = (JSONObject) args[0];
        new Handler(getMainLooper())
                .post(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String username = result.getString("username");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                );
    };

    private void connectConnection() {
        instance = this;
        signalApplication.getSocket().connect();
    }

    private void disconnectConnection() {
        instance = null;
        signalApplication.getSocket().disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        signalApplication.getSocket().off(Socket.EVENT_CONNECT, onNhanVien);
        signalApplication.getSocket().off(Socket.EVENT_DISCONNECT, onDisconnect);
        signalApplication.getSocket().off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        signalApplication.getSocket().off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        //@formatter:off
        signalApplication.getSocket().off("message"                         , message);
        //@formatter:on

        disconnectConnection();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}