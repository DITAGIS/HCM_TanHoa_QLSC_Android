package vn.ditagis.com.tanhoa.qlsc.entities;

import android.app.Application;
import android.location.Location;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.geometry.Geometry;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import vn.ditagis.com.tanhoa.qlsc.libs.Constants;

public class DApplication extends Application {
    public Constant getConstant;

    {
        getConstant = new Constant();
    }


    private User userDangNhap;

    public User getUserDangNhap() {
        return userDangNhap;
    }

    public void setUserDangNhap(User userDangNhap) {
        this.userDangNhap = userDangNhap;
    }

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

    private short loaiVatTu;

    public short getLoaiVatTu() {
        return loaiVatTu;
    }

    public void setLoaiVatTu(short loaiVatTu) {
        this.loaiVatTu = loaiVatTu;
    }

    public DiemSuCo getDiemSuCo;

    {
        getDiemSuCo = new DiemSuCo();
    }


    private Geometry geometry;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    private ArcGISFeature arcGISFeature;

    public ArcGISFeature getArcGISFeature() {
        return arcGISFeature;
    }

    public void setArcGISFeature(ArcGISFeature arcGISFeature) {
        this.arcGISFeature = arcGISFeature;
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
