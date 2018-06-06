package hcm.ditagis.com.tanhoa.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.List;

import hcm.ditagis.com.tanhoa.qlsc.R;
import hcm.ditagis.com.tanhoa.qlsc.libs.FeatureLayerDTG;
import hcm.ditagis.com.tanhoa.qlsc.utities.Popup;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class SingleTapMapViewAsync extends AsyncTask<Point, FeatureLayer, Void> {
    private ProgressDialog mDialog;
    private Context mContext;
    private FeatureLayerDTG mFeatureLayerDTG;
    private Point mPoint;
    private List<FeatureLayerDTG> mFeatureLayerDTGs;
    private MapView mMapView;
    private ArcGISFeature mSelectedArcGISFeature;
    private Popup mPopUp;
    private Callout mCallOut;
    private static double DELTA_MOVE_Y = 0;//7000;
    private android.graphics.Point mClickPoint;
    private boolean isFound = false;


    public SingleTapMapViewAsync(Context context, List<FeatureLayerDTG> featureLayerDTGS,
                                 Popup popup, Callout callout, android.graphics.Point clickPoint,
                                 MapView mapview) {
        this.mMapView = mapview;
        this.mFeatureLayerDTGs = featureLayerDTGS;
        this.mPopUp = popup;
        this.mCallOut = callout;
        this.mClickPoint = clickPoint;
        this.mContext = context;
        this.mDialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage("Đang xử lý...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Void doInBackground(Point... params) {
        mPoint = params[0];
        final int[] isIdentified = {mFeatureLayerDTGs.size()};
        for (final FeatureLayerDTG featureLayerDTG : mFeatureLayerDTGs) {
            if (isIdentified[0] > 0) {
                mFeatureLayerDTG = featureLayerDTG;
                mFeatureLayerDTG.getFeatureLayer().clearSelection();
                final ListenableFuture<IdentifyLayerResult> identifyFuture = mMapView.identifyLayerAsync(featureLayerDTG.getFeatureLayer(), mClickPoint, 5, false, 1);
                identifyFuture.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            isIdentified[0]--;
                            IdentifyLayerResult layerResult = identifyFuture.get();
                            List<GeoElement> resultGeoElements = layerResult.getElements();
                            if (resultGeoElements.size() > 0) {
                                if (resultGeoElements.get(0) instanceof ArcGISFeature) {
                                    mSelectedArcGISFeature = (ArcGISFeature) resultGeoElements.get(0);
                                    publishProgress(featureLayerDTG.getFeatureLayer());
//                                    if (mDialog != null && mDialog.isShowing()) {
//                                        mDialog.dismiss();
//                                    }
                                }
                            } else {
                                if (isIdentified[0] == 0)
                                    // none of the features on the map were selected
                                    publishProgress(null);
                            }

                        } catch (Exception e) {
                            Log.e(mContext.getResources().getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                        }
                    }
                });
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(FeatureLayer... values) {
        super.onProgressUpdate(values);

        FeatureLayer featureLayer = null;
        if (values != null)
            featureLayer = values[0];
        if (mSelectedArcGISFeature != null && featureLayer != null) {
            // highlight the selected feature

            featureLayer.clearSelection();
            featureLayer.selectFeature(mSelectedArcGISFeature);
            mPopUp.setFeatureLayerDTG(mFeatureLayerDTG);

            LinearLayout linearLayout = mPopUp.createPopup(featureLayer.getName(), mSelectedArcGISFeature);

//            Envelope envelope = mSelectedArcGISFeature.getGeometry().getExtent();
//            Envelope envelope1 = new Envelope(new Point(envelope.getXMin(), envelope.getYMin() + DELTA_MOVE_Y), new Point(envelope.getXMax(), envelope.getYMax() + DELTA_MOVE_Y));
//            mMapView.setViewpointGeometryAsync(envelope1, 0);
            // show CallOut
            mCallOut.setLocation(mPoint);
            mCallOut.setContent(linearLayout);
//        mPopUp.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
            mCallOut.refresh();
            mCallOut.show();
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
//            }
//        });
//        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}