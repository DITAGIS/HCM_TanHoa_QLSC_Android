package hcm.ditagis.com.tanhoa.qlsc.socket;

import android.app.Application;
import android.location.Location;

import java.net.URISyntaxException;

import hcm.ditagis.com.tanhoa.qlsc.libs.Constants;
import io.socket.client.IO;
import io.socket.client.Socket;

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

    private Location mLocation;

    public Location getmLocation() {
        return mLocation;
    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
    }
}
