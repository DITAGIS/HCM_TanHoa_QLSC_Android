package vn.ditagis.com.tanhoa.qlsc.utities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import vn.ditagis.com.tanhoa.qlsc.MainActivity;
import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;


public class Notification {

    public static void notify(Context context) {
        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(context);
        notBuilder.setAutoCancel(true);


        notBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notBuilder.setTicker("This is a ticker");

        // Sét đặt thời điểm sự kiện xẩy ra.
        // Các thông báo trên Panel được sắp xếp bởi thời gian này.
        notBuilder.setWhen(System.currentTimeMillis() + 10 * 1000);
        notBuilder.setContentTitle("Bạn vừa được giao để xử lý một sự cố");
        notBuilder.setContentText("Làm mới dữ liệu và xem danh sách công việc để biết thêm thông tin ...");

        // Tạo một Intent
        Intent intent = new Intent(context, MainActivity.class);


        // PendingIntent.getActivity(..) sẽ start mới một Activity và trả về
        // đối tượng PendingIntent.
        // Nó cũng tương đương với gọi Context.startActivity(Intent).
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Constant.REQUEST_CODE_NOTIFICATION,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        notBuilder.setContentIntent(pendingIntent);

        // Lấy ra dịch vụ thông báo (Một dịch vụ có sẵn của hệ thống).
        NotificationManager notificationService =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Xây dựng thông báo và gửi nó lên hệ thống.

        android.app.Notification notification = notBuilder.build();
        assert notificationService != null;
        notificationService.notify(Constant.NOTIFICATION_ID, notification);
    }

}
