package vn.ditagis.com.tanhoa.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import vn.ditagis.com.tanhoa.qlsc.MainActivity;
import vn.ditagis.com.tanhoa.qlsc.R;

public class LoadLegendAsycn extends AsyncTask<Void, Void, Void> {
    private ProgressDialog mDialog;
    private Context mContext;
    private AsyncResponse mDelegate;
    private LinearLayout mLayout;
    private MainActivity mActivity;

    public interface AsyncResponse {
        void processFinish(Void output);
    }

    public LoadLegendAsycn(Context context, LinearLayout layout, MainActivity quanLySuCo, AsyncResponse delegate) {
        this.mDelegate = delegate;
        this.mLayout = layout;
        this.mContext = context;
        this.mActivity = quanLySuCo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mContext, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mContext.getString(R.string.preparing));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }



    @Override
    protected Void doInBackground(Void... params) {
        final LinearLayout layoutFeatureBeNgam = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_legend, null);
        final ImageView imgBeNgam = layoutFeatureBeNgam.findViewById(R.id.img_layout_legend);
        final TextView txtBeNgam = layoutFeatureBeNgam.findViewById(R.id.txt_layout_legend);
        txtBeNgam.setTextColor(mContext.getResources().getColor(android.R.color.black));

        final LinearLayout layoutFeatureChuaXuLy = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_legend, null);
        final ImageView imgChuaXuLy = layoutFeatureChuaXuLy.findViewById(R.id.img_layout_legend);
        final TextView txtChuaXuLy = layoutFeatureChuaXuLy.findViewById(R.id.txt_layout_legend);
        txtChuaXuLy.setTextColor(mContext.getResources().getColor(android.R.color.black));

        final LinearLayout layoutFeatureDangXuLy = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_legend, null);
        final ImageView imgDangXuLy = layoutFeatureDangXuLy.findViewById(R.id.img_layout_legend);
        final TextView txtDangXuLy = layoutFeatureDangXuLy.findViewById(R.id.txt_layout_legend);
        txtDangXuLy.setTextColor(mContext.getResources().getColor(android.R.color.black));

        final LinearLayout layoutFeatureHoanThanh = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_legend, null);
        final ImageView imgHoanThanh = layoutFeatureHoanThanh.findViewById(R.id.img_layout_legend);
        final TextView txtHoanThanh = layoutFeatureHoanThanh.findViewById(R.id.txt_layout_legend);
        txtHoanThanh.setTextColor(mContext.getResources().getColor(android.R.color.black));


        try {
            Drawable drawableBeNgam = Drawable.createFromStream((
                    InputStream) new URL(mContext.getString(R.string.url_image_symbol_beNgam)).getContent(), "src");
            imgBeNgam.setImageDrawable(drawableBeNgam);
            txtBeNgam.setText("Chưa sửa chữa bể ngầm");

            Drawable drawableChuaXuLy = Drawable.createFromStream((
                    InputStream) new URL(mContext.getString(R.string.url_image_symbol_chuasuachua)).getContent(), "src");
            imgChuaXuLy.setImageDrawable(drawableChuaXuLy);
            txtChuaXuLy.setText("Chưa sửa chữa");

            Drawable drawableDangXuLy = Drawable.createFromStream((
                    InputStream) new URL(mContext.getString(R.string.url_image_symbol_dangsuachua)).getContent(), "src");
            imgDangXuLy.setImageDrawable(drawableDangXuLy);
            txtDangXuLy.setText("Đang sửa chữa");

            Drawable drawableHoanThanh = Drawable.createFromStream((
                    InputStream) new URL(mContext.getString(R.string.url_image_symbol_hoanthanh)).getContent(), "src");
            imgHoanThanh.setImageDrawable(drawableHoanThanh);
            txtHoanThanh.setText("Hoàn thành");


            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLayout.addView(layoutFeatureChuaXuLy);
                    mLayout.addView(layoutFeatureBeNgam);
                    mLayout.addView(layoutFeatureDangXuLy);
                    mLayout.addView(layoutFeatureHoanThanh);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);


    }

    @Override
    protected void onPostExecute(Void value) {
//        if (khachHang != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish(value);
//        }
    }
}
