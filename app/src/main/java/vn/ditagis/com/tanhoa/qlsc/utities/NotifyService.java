package vn.ditagis.com.tanhoa.qlsc.utities;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import io.socket.emitter.Emitter;
import vn.ditagis.com.tanhoa.qlsc.MainActivity;
import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

// A Service is an application component that can perform long-running operations
// in the background and does not provide a user interface.
public class NotifyService extends Service {

    public static final String CHANNEL_ID = "notification channel";
    final static String ACTION = "edu.dartmouth.cs.notifydemo.CANCEL_RECEIVED";
    final static String STOP_SERVICE_BROADCAST_KEY = "StopServiceBroadcastKey";
    final static int RQS_STOP_SERVICE = 1;
    private static final String TAG = "NotifyService";
    private NotifyServiceReceiver notifyServiceReceiver;
    private DApplication mApplication;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "NotifyService:onCreate");
        notifyServiceReceiver = new NotifyServiceReceiver();
        mApplication = (DApplication) getApplication();
        mContext = getBaseContext();
    }

    // onStartCommand() is called upon calling the startService(Intent) in NotifyActivity
    // Checkout the Log.d and you will see that onStartCommand() execute on the UI thread and
    // not on a worker thread. If you are planning to do a computationally intensive operation in
    // onStartCommand() then create a thread or AsyncTask or loader to do it. Else, you will block
    // the UI.

    private Emitter.Listener onInfinity = args -> {
        if (args != null && args.length > 0) {
            Log.d("Nhận", args[0].toString());
        }
    };
    Emitter.Listener onNhanViec = args -> new Handler(Looper.getMainLooper()).post(() -> {
        if (args != null && args.length > 0) {
            try {
                Toast.makeText(mContext, "Thông báo", Toast.LENGTH_SHORT).show();
                showNotify();
            } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "Có lỗi khi nhận thông báo", Toast.LENGTH_SHORT).show();
            }
            Log.d("Nhận việc", args[0].toString());
        }

    });

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        new Thread(() -> {
        final DApplication app = (DApplication) getApplication();
        io.socket.client.Socket socket = app.getSocket();
        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds
        handler.postDelayed(new Runnable() {
            public void run() {
                //do something
                if (app.getmLocation() != null) {
                    Log.d("gửi", "hhi");
                    if (mApplication.getUserDangNhap() != null &&
                            mApplication.getUserDangNhap().getUserName() != null)
                        socket.emit(Constant.Socket.EVENT_STAFF_NAME, Constant.Socket.APP_ID + "," + mApplication.getUserDangNhap().getUserName());
                    Emitter emit1 = socket.emit(Constant.Socket.EVENT_LOCATION,
                            app.getmLocation().getLatitude() + "," + app.getmLocation().getLongitude());
                    app.setmLocation(null);
                    Log.d("Kết quả vị trí", emit1.hasListeners(Constant.Socket.EVENT_LOCATION) + "");
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
        socket.on(Constant.Socket.EVENT_STAFF_NAME, onInfinity);
        socket.on(Constant.Socket.EVENT_LOCATION, onInfinity);
        socket.on(Constant.Socket.EVENT_GIAO_VIEC, onNhanViec);

        socket.connect();

//        }).start();

        return START_REDELIVER_INTENT;

    }

    private void showNotify() {
        try {
            // register the receiver
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION);
            Log.d(TAG, "NotifyService:registerReceiver");
            registerReceiver(notifyServiceReceiver, intentFilter);

            //Send Notification

            String notificationTitle = "Bạn vừa nhận một công việc mới";
            String notificationText = "Vui lòng tải lại bản đồ và xem danh sách công việc!";
            Intent myIntent = new Intent(mContext, MainActivity.class);

            // PendingIntent is a token that you give to a application (e.g. NotificationManager), which
            // allows the application to use your application's permissions to execute a
            // predefined piece of code.
            // Here we create PendingIntent to run myIntent instead of normal way of startService(myIntent)
            // so that clicking the notification icon will run this Intent (myIntent) to open a web browser

            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, myIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            mApplication.setChannelID(mApplication.getChannelID() + 1);
            // set up a notification with the pending intent
//            NotificationChannel notificationChannel = new NotificationChannel(mApplication.getChannelID() + "",
//                    "channel name", NotificationManager.IMPORTANCE_HIGH);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(mContext, mApplication.getChannelID() + "")
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationText)
                            .setSmallIcon(R.drawable.logo)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
//                            .setContentIntent(pendingIntent)
                            .setFullScreenIntent(pendingIntent, true)
                            .setCategory(NotificationCompat.CATEGORY_EVENT);
            Notification notification = notificationBuilder.build();

            // FLAG_AUTO_CANCEL - means the notification icon disappears when the user taps
            // the notification icon.
            // comment out this line and tap the notification icon to see what happens.
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            // get the notification manager
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(notificationChannel);
                // 0 - can be any number. An identifier for this notification to be unique within
                // your application.
                notificationManager.notify(mApplication.getChannelID(), notification);
            }

            // START_NOT_STICKY used for services that should only remain running while
            // processing any commands sent to them.
        } catch (Exception e) {
            Toast.makeText(mContext, "Có lỗi khi nhận thông báo", Toast.LENGTH_SHORT).show();
        }
    }

    // onDestroy corresponds to onCreate. It performs any final cleanup before an activity is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "NotifyService:onDestroy/unregisterReceiver");
//        this.unregisterReceiver(notifyServiceReceiver);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "NotifyService:onBind()");
        return null;
    }

    // Base class for code that will receive intents sent by sendBroadcast().
    // The receiver needs to be registered (see registerReceiver) and specify a certain
    // intent it listens for  (e.g. the intent should specifies the same action)

    public class NotifyServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, "NotifyService:onReceive(): stop service " + intent);
                NotificationManager notificationManager;
                int rqs = intent.getIntExtra(STOP_SERVICE_BROADCAST_KEY, 0);
                if (rqs == RQS_STOP_SERVICE) {

                    // NotificationManager - Class to notify the user of events that happen.
                    // Notifications can take different forms: 1) A persistent icon that goes in
                    // the status bar 2) Turning on or flashing LEDs on the device; 3) Alerting the
                    // user by flashing the backlight, playing a sound, or vibrating.
                    // NOTIFICATION_SERVICE - Use with Context.getSystemService(Class) to retrieve a
                    // NotificationManager for informing the user of background events.
                    // Finally, cancelAll - Cancel all previously shown notifications.

                    notificationManager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
                    if (notificationManager != null)
                        notificationManager.cancelAll();
                }
                // Stop the service, if it was previously started. This is the same as
                // calling stopService(Intent) for this particular service.  stopSelf()
                // will call onDestory()

                stopSelf();
            } catch (Exception e) {
                Toast.makeText(context, "Có lỗi khi nhận thông báo", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
