package vn.ditagis.com.tanhoa.qlsc.entities;

import android.app.Application;
import android.location.Location;

import java.net.URISyntaxException;

import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DFeatureLayer;
import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo;
import vn.ditagis.com.tanhoa.qlsc.entities.DiemSuCo;
import vn.ditagis.com.tanhoa.qlsc.entities.User;
import vn.ditagis.com.tanhoa.qlsc.libs.Constants;
import io.socket.client.IO;
import io.socket.client.Socket;

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
    private DFeatureLayer mDFeatureLayer;

    public DFeatureLayer getDFeatureLayer() {
        return mDFeatureLayer;
    }

    public void setDFeatureLayer(DFeatureLayer dFeatureLayer) {
        this.mDFeatureLayer = dFeatureLayer;
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
