package hcm.ditagis.com.tanhoa.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hcm.ditagis.com.tanhoa.qlsc.R;

public class FindLocationAsycn extends AsyncTask<String, Void, List<Address>> {
    private Geocoder mGeocoder;
    private boolean mIsFromLocationName;
    private Context mContext;
    private AsyncResponse mDelegate;
    private double mLongtitude, mLatitude;
    private ProgressDialog mDialog;

    public interface AsyncResponse {
        void processFinish(List<Address> output);
    }

    public void setmLongtitude(double mLongtitude) {
        this.mLongtitude = mLongtitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public FindLocationAsycn(Context context, boolean isFromLocationName, AsyncResponse delegate) {
        this.mDelegate = delegate;
        this.mContext = context;
        this.mIsFromLocationName = isFromLocationName;
        this.mGeocoder = new Geocoder(context);
        mDialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage(mContext.getString(R.string.async_dang_xu_ly));
        mDialog.setCancelable(false);

        mDialog.show();
    }

    @Override
    protected List<Address> doInBackground(String... params) {
        if (!Geocoder.isPresent())
            return null;
        List<Address> lstLocation = new ArrayList<>();
        if (mIsFromLocationName) {
            String text = params[0];
            try {
                List<Address> addressList = mGeocoder.getFromLocationName(text, 5);
                lstLocation.addAll(addressList);
            } catch (IOException ignored) {
                Log.e("error", ignored.toString());
            }
        } else {
            try {
                List<Address> fromLocation = mGeocoder.getFromLocation(mLatitude, mLongtitude, 1);
                lstLocation.addAll(fromLocation);
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
    protected void onPostExecute(List<Address> addressList) {
//        if (khachHang != null) {
        if (addressList == null)
            Toast.makeText(mContext, R.string.message_no_geocoder_available, Toast.LENGTH_LONG).show();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            this.mDelegate.processFinish(addressList);
        }
//        }
    }
}
