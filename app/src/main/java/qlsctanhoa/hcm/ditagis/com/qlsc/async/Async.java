package qlsctanhoa.hcm.ditagis.com.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by ThanLe on 4/9/2018.
 */

public class Async {
    public class EditAsync extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog;
        private Context mContext;

        public EditAsync(Context context) {
            mContext = context;
            this.dialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Đang xử lý...");
            dialog.setCancelable(false);

            dialog.show();

        }

        public ProgressDialog getDialog() {
            return dialog;
        }

        @Override
        protected Void doInBackground(String... params) {

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    }

}
