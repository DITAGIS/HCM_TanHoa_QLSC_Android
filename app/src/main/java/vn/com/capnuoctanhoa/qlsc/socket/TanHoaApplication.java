package vn.com.capnuoctanhoa.qlsc.socket;

import android.app.Application;
import android.location.Location;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import vn.com.capnuoctanhoa.qlsc.libs.Constants;

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

    private String mUsername;

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }
    private Location mLocation;

    public Location getmLocation() {
        return mLocation;
    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
    }
}
