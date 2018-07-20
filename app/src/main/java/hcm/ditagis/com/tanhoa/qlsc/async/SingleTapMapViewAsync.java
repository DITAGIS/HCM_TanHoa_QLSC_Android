package hcm.ditagis.com.tanhoa.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import hcm.ditagis.com.tanhoa.qlsc.libs.FeatureLayerDTG;
import hcm.ditagis.com.tanhoa.qlsc.utities.Popup;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class SingleTapMapViewAsync extends AsyncTask<Point, FeatureLayerDTG, Void> {
    private ProgressDialog mDialog;
    private FeatureLayerDTG mFeatureLayerDTG;
    private MapView mMapView;
    private ArcGISFeature mSelectedArcGISFeature;
    private Popup mPopUp;
    private static double DELTA_MOVE_Y = 0;//7000;
    private android.graphics.Point mClickPoint;
    private boolean isFound = false;

    public SingleTapMapViewAsync(Context context, FeatureLayerDTG featureLayerDTG, Popup popup, android.graphics.Point clickPoint, MapView mapview) {
        this.mMapView = mapview;
        this.mFeatureLayerDTG = featureLayerDTG;
        this.mPopUp = popup;
        this.mClickPoint = clickPoint;
        this.mDialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
    }

    @Override
    protected Void doInBackground(Point... points) {
        final ListenableFuture<List<IdentifyLayerResult>> listListenableFuture = mMapView.identifyLayersAsync(mClickPoint, 5, false, 1);
        listListenableFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                List<IdentifyLayerResult> identifyLayerResults = null;
                try {
                    identifyLayerResults = listListenableFuture.get();
                    for (IdentifyLayerResult identifyLayerResult : identifyLayerResults) {
                        {
                            List<GeoElement> elements = identifyLayerResult.getElements();
                            if (elements.size() > 0 && elements.get(0) instanceof ArcGISFeature && !isFound) {
                                isFound = true;
                                mSelectedArcGISFeature = (ArcGISFeature) elements.get(0);
                                long serviceLayerId = mSelectedArcGISFeature.getFeatureTable().
                                        getServiceLayerId();
                                if(serviceLayerId == ((ArcGISFeatureTable) mFeatureLayerDTG.getLayer().getFeatureTable()).getServiceLayerId())
                                publishProgress(mFeatureLayerDTG);
                            }
                        }
                    }
                    publishProgress(null);
                } catch (
                        InterruptedException e)

                {
                    e.printStackTrace();
                } catch (
                        ExecutionException e)

                {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage("Đang xử lý...");
        mDialog.setCancelable(false);
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                publishProgress();
            }
        });
        mDialog.show();
    }

    @Override
    protected void onProgressUpdate(FeatureLayerDTG... values) {
        super.onProgressUpdate(values);
        if (values != null && mSelectedArcGISFeature != null) {
            FeatureLayerDTG featureLayerDTG = values[0];
            mPopUp.setFeatureLayerDTG(featureLayerDTG);
            mPopUp.showPopup(mSelectedArcGISFeature, false);
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}