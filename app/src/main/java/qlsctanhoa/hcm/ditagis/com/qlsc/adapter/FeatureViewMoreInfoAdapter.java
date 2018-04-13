package qlsctanhoa.hcm.ditagis.com.qlsc.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import qlsctanhoa.hcm.ditagis.com.qlsc.R;

/**
 * Created by ThanLe on 04/10/2017.
 */

public class FeatureViewMoreInfoAdapter extends ArrayAdapter<FeatureViewMoreInfoAdapter.Item> {
    private Context mContext;
    private List<Item> items;

    public FeatureViewMoreInfoAdapter(Context context, List<Item> items) {
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


//    public Item getItem(String mlt) {
//        for (Item item : this.items)
//            if (item.getTieuThu().equals(mlt))
//                return item;
//        return null;
//    }
//
//    public boolean removeItem(String mlt) {
//        for (Item item : this.items)
//            if (item.getTieuThu().equals(mlt)) {
//                this.items.remove(item);
//                return true;
//            }
//        return false;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_viewmoreinfo, null);
        }
        Item item = items.get(position);

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layout_viewmoreinfo);

        if (position % 2 == 1) {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBackground_1));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent_1));
        }
        TextView txtID = (TextView) convertView.findViewById(R.id.txt_viewmoreinfo_id);
        //todo
        txtID.setText(item.getAlias());

        TextView txtDiaChi = (TextView) convertView.findViewById(R.id.txt_viewmoreinfo_value);
        //todo
        txtDiaChi.setText(item.getValue());


        return convertView;
    }


    public static class Item {
        private String alias;
        private String value;
        private String fieldName;
        private boolean isEdit;

        public Item() {
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public boolean isEdit() {
            return isEdit;
        }

        public void setEdit(boolean edit) {
            isEdit = edit;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
