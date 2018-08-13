package hcm.ditagis.com.tanhoa.qlsc.socket;

import android.app.Application;

import hcm.ditagis.com.tanhoa.qlsc.libs.Constants;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class TanHoaApplication extends Application {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
