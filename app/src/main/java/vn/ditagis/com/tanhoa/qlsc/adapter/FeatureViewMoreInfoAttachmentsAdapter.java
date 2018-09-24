package vn.ditagis.com.tanhoa.qlsc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;

/**
 * Created by ThanLe on 04/10/2017.
 */

public class FeatureViewMoreInfoAttachmentsAdapter extends ArrayAdapter<FeatureViewMoreInfoAttachmentsAdapter.Item> {
    private Context mContext;
    private List<Item> items;

    public FeatureViewMoreInfoAttachmentsAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.mContext = context;
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.item_viewmoreinfo_attachment, null);
        }
        Item item = items.get(position);

        TextView txtValue = convertView.findViewById(R.id.txt_viewmoreinfo_attachment_name);
        txtValue.setText(item.getName());
        ImageView imageView = convertView.findViewById(R.id.img_viewmoreinfo_attachment);
        if (item.getContentType().equals(Constant.FILE_TYPE.PNG) &&
                item.getImg() != null) {

            Bitmap bmp = BitmapFactory.decodeByteArray(item.getImg(), 0, item.getImg().length);

            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(),
                    bmp.getHeight(), false));
        } else imageView.setVisibility(View.GONE);
        return convertView;
    }


    public static class Item {
        private String name;
        private byte[] img;
        private String url;
        private String contentType;

        public Item() {
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        byte[] getImg() {
            return img;
        }

        public void setImg(byte[] img) {
            this.img = img;
        }
    }
}
