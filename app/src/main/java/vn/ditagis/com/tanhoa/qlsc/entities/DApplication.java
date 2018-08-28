package vn.ditagis.com.tanhoa.qlsc.entities;

import android.app.Application;
import android.location.Location;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import vn.ditagis.com.tanhoa.qlsc.libs.Constants;

public class DApplication extends Application {
    public Constant getConstant;

    {
        getConstant = new Constant();
    }

    public DiemSuCo getDiemSuCo;

    {
        getDiemSuCo = new DiemSuCo();
    }


    public User getUserDangNhap;
    public DFeatureLayer getDFeatureLayer;

    {
        getDFeatureLayer = new DFeatureLayer();
    }


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
