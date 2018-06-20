package hcm.ditagis.com.tanhoa.qlsc.async;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hcm.ditagis.com.tanhoa.qlsc.R;
import hcm.ditagis.com.tanhoa.qlsc.entities.MyAddress;
import hcm.ditagis.com.tanhoa.qlsc.libs.FeatureLayerDTG;
import hcm.ditagis.com.tanhoa.qlsc.utities.MyServiceFeatureTable;

public class FindLocationAsycn extends AsyncTask<String, Void, List<MyAddress>> {
    private Geocoder mGeocoder;
    private boolean mIsFromLocationName;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private AsyncResponse mDelegate;
    private double mLongtitude, mLatitude;
    private List<FeatureLayerDTG> mFeatureLayerDTGS;
    private boolean mIsAddFeature;

    public interface AsyncResponse {
        void processFinish(List<MyAddress> output);
    }

    public void setmLongtitude(double mLongtitude) {
        this.mLongtitude = mLongtitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public FindLocationAsycn(Context context, boolean isFromLocationName, Geocoder geocoder,
                             List<FeatureLayerDTG> featureLayerDTGS, boolean isAddFeature, AsyncResponse delegate) {
        this.mDelegate = delegate;
        this.mContext = context;
        this.mIsFromLocationName = isFromLocationName;
        this.mGeocoder = geocoder;
        this.mFeatureLayerDTGS = featureLayerDTGS;
        mIsAddFeature = isAddFeature;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<MyAddress> doInBackground(String... params) {
        if (!Geocoder.isPresent())
            return null;
        final List<MyAddress> lstLocation = new ArrayList<>();
        if (mIsFromLocationName) {
            if (!mIsAddFeature) {
                String text = params[0];
                try {
                    List<Address> addressList = mGeocoder.getFromLocationName(text, 5);
                    for (Address address : addressList)
                        lstLocation.add(new MyAddress(address.getLongitude(), address.getLatitude(),  address.getSubAdminArea(),address.getAddressLine(0)));
                } catch (IOException ignored) {
                    //todo grpc failed
                    Log.e("error", ignored.toString());
                }
            } else {
                if (MyServiceFeatureTable.getInstance(mContext, mFeatureLayerDTGS).getLayerThuaDat() != null) {
                    Point project = new Point(mLongtitude, mLatitude);
                    Geometry center = GeometryEngine.project(project, SpatialReferences.getWgs84());
                    Geometry geometry = GeometryEngine.project(center, SpatialReferences.getWebMercator());

                    //kiểm tra có thuộc địa bàn quản lý của tài khoản hay không
                    QueryParameters queryParam = new QueryParameters();
                    //lấy hành chính của điểm báo sự cố
                    queryParam.setGeometry(geometry);
                    queryParam.setWhereClause("1=1");
                    final ListenableFuture<FeatureQueryResult> featureQueryResultListenableFuture = MyServiceFeatureTable.getInstance(mContext, mFeatureLayerDTGS).getLayerThuaDat().queryFeaturesAsync(queryParam);
                    featureQueryResultListenableFuture.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //todo không có địa chỉ trên thửa đất
                                FeatureQueryResult features = featureQueryResultListenableFuture.get();
                                for (Object item : features) {
                                    Feature feature = (Feature) item;
                                    lstLocation.add(new MyAddress(mLongtitude, mLatitude, "", feature.getAttributes().get("SoNha").toString()
                                            + " " + feature.getAttributes().get("TenConDuong").toString()));
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        } else {
            try {
                List<Address> addressList = mGeocoder.getFromLocation(mLatitude, mLongtitude, 1);
                for (Address address : addressList)
                    lstLocation.add(new MyAddress(address.getLongitude(), address.getLatitude(),  address.getSubAdminArea(),address.getAddressLine(0)));
            } catch (IOException ignored) {
                Log.e("error", ignored.toString());
            }
        }


        return lstLocation;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<MyAddress> addressList) {
//        if (khachHang != null) {
        if (addressList == null)
            Toast.makeText(mContext, R.string.message_no_geocoder_available, Toast.LENGTH_LONG).show();
        this.mDelegate.processFinish(addressList);
//        }
    }
}
