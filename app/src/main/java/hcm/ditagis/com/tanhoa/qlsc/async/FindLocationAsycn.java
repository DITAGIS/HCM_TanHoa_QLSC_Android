package hcm.ditagis.com.tanhoa.qlsc.async;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hcm.ditagis.com.tanhoa.qlsc.R;

public class FindLocationAsycn extends AsyncTask<String, Void, List<Address>> {
    private Geocoder mGeocoder;
    private AsyncResponse mDelegate;
    private Context mContext;

    public interface AsyncResponse {
        void processFinish(List<Address> output);
    }

    public FindLocationAsycn(Context context, AsyncResponse delegate) {
        this.mDelegate = delegate;
        this.mContext = context;
        this.mGeocoder = new Geocoder(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Address> doInBackground(String... params) {
        String text = params[0];
        if (!Geocoder.isPresent())
            return null;
        List<Address> lstLocation = new ArrayList<>();


        try {
            List<Address> addressList = mGeocoder.getFromLocationName(text, 5);
            lstLocation.addAll(addressList);
        } catch (IOException e) {
            e.printStackTrace();
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
        this.mDelegate.processFinish(addressList);
//        }
    }
}
