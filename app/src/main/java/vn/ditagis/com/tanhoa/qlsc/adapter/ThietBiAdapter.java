package vn.ditagis.com.tanhoa.qlsc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import vn.ditagis.com.tanhoa.qlsc.R;

/**
 * Created by ThanLe on 04/10/2017.
 */

public class ThietBiAdapter extends ArrayAdapter<ThietBiAdapter.Item> {
    private Context context;
    private List<Item> items;

    public ThietBiAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.context = context;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_tracuu, null);
        }
        Item item = items.get(position);

        TextView txtTop = convertView.findViewById(R.id.txt_top);
        if (item.getTenThietBi() == null || item.getTenThietBi().isEmpty())
            txtTop.setVisibility(View.GONE);
        else
            txtTop.setText(item.getTenThietBi());


        TextView txtRight = (TextView) convertView.findViewById(R.id.txt_right);
        txtRight.setText(item.getSoLuong() + " phút");

        TextView txtBottom = convertView.findViewById(R.id.txt_bottom);
        txtBottom.setVisibility(View.GONE);
        return convertView;
    }


    public static class Item {
        private String tenThietBi;
        private double soLuong;
        private String maThietBi;

        public String getTenThietBi() {
            return tenThietBi;
        }

        public double getSoLuong() {
            return soLuong;
        }


        public String getMaThietBi() {
            return maThietBi;
        }

        public Item(String tenVatTu, double soLuong, String maVatTu) {
            this.tenThietBi = tenVatTu;
            this.soLuong = soLuong;
            this.maThietBi = maVatTu;
        }
    }
}