package hcm.ditagis.com.tanhoa.qlsc.connectDB;

import android.content.Context;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import hcm.ditagis.com.tanhoa.qlsc.R;
import hcm.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo;

public class HoSoVatTuSuCoDB implements IDB<HoSoVatTuSuCo, Boolean, String> {
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private Context mContext;

    public HoSoVatTuSuCoDB(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public Boolean add(HoSoVatTuSuCo hoSoVatTuSuCo) {
        return null;
    }

    @Override
    public Boolean delete(String s) {
        return null;
    }

    @Override
    public Boolean update(HoSoVatTuSuCo hoSoVatTuSuCo) {
        return null;
    }

    @Override
    public HoSoVatTuSuCo find(String s, String k1) {
        return null;
    }

    @Override
    public HoSoVatTuSuCo find(String s, String k1, String k2) {
        return null;
    }


    @Override
    public List<HoSoVatTuSuCo> getAll() {
        return null;
    }

    public HashMap<String, String> find() {
        Connection cnn = ConnectionDB.getInstance().getConnection();
        HashMap<String, String> hashMap = new HashMap<>();
        ResultSet rs = null;
        try {
            if (cnn == null)
                return null;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String query = mContext.getString(R.string.sql_select_nguyennhan_ongchinh);
            PreparedStatement mStatement = cnn.prepareStatement(query);

            rs = mStatement.executeQuery();

            while (rs.next()) {
                hashMap.put(rs.getString(1), rs.getString(2));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (rs != null && !rs.isClosed())
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }

}
