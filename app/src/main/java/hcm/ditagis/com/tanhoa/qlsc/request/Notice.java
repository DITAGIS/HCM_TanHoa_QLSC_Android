package hcm.ditagis.com.tanhoa.qlsc.request;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

import hcm.ditagis.com.tanhoa.qlsc.R;

public class Notice {
    private static Socket socket;
    private Context mContext;

    private Notice(Context context) {
        mContext = context;
        connect();
    }

    private static Notice instance = null;

    public static Notice getInstance(Context context) {
        if (instance == null) {
            instance = new Notice(context);
        }
        socket.on("hihi", onNewMessage);
        return instance;
    }

    private void connect() {

        try {
            socket = IO.socket(mContext.getString(R.string.URL_SERVER));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private static Emitter.Listener onNewMessage = new Emitter.Listener() {
        @SuppressLint("LongLogTag")
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            Log.d("notice from server.......", data.optString("data"));
        }
    };

    public void emit(String message, Object... args) {
        Emitter emitter = socket.emit(message, args);
        Log.d("notice to server", "ok");
    }

}
