package vn.ditagis.com.tanhoa.qlsc.entities;

import android.Manifest;

import java.text.SimpleDateFormat;

public class Constant {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy");
    public static final SimpleDateFormat DATE_FORMAT_VIEW = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    public static final int REQUEST_CODE_PERMISSION = 2;
    public static final int REQUEST_CODE_BASEMAP = 5;
    public static final int REQUEST_CODE_LAYER = 6;
    public static final int REQUEST_CODE_ADD_FEATURE = 7;
    public static final int REQUEST_CODE_ADD_FEATURE_ATTACHMENT = 8;
    public static final String[] REQUEST_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public final String[] OUT_FIELDS_ARR = new String[]{
            FIELD_SUCO.ID_SUCO,
            FIELD_SUCO.DIA_CHI,
            FIELD_SUCO.GHI_CHU,
            FIELD_SUCO.NGAY_THONG_BAO,
            FIELD_SUCO.NGUYEN_NHAN
    };
    public final String[] ADD_FIELDS_ARR = new String[]{
            FIELD_SUCO.ID_SUCO,
            FIELD_SUCO.DIA_CHI,
            FIELD_SUCO.GHI_CHU,
            FIELD_SUCO.NGUYEN_NHAN,
            FIELD_SUCO.TRANG_THAI,
            FIELD_SUCO.NGUOI_CAP_NHAT,
            FIELD_SUCO.NGAY_THONG_BAO,
            FIELD_SUCO.SDT
    };
    //    public static final String SERVER_API = "http://gis.capnuoccholon.com.vn/cholon/api";
    private final String SERVER_API = "http://sawagis.vn/tanhoa1/api";
    public String URL_SUCO;

    {
//        URL_SUCO = "http://112.78.5.191:6080/arcgis/rest/services/TruyenDan/TruyenDanDiemSuCo/FeatureServer/0";
//        URL_SUCO = "https://sawagis.vn/arcgis/rest/services/TruyenDan/TruyenDanDiemSuCo/FeatureServer/0";
        URL_SUCO = "https://prgrd.hcmpc.com.vn/arcgis/rest/services/GISGOVAP/LuoiDien_KT_H/FeatureServer/0";
    }

    public String URL_VATTU;

    {
//        URL_SUCO = "http://112.78.5.191:6080/arcgis/rest/services/TruyenDan/TruyenDanDiemSuCo/FeatureServer/0";
//        URL_SUCO = "https://sawagis.vn/arcgis/rest/services/TruyenDan/TruyenDanDiemSuCo/FeatureServer/0";
        URL_VATTU = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/THSuCo/FeatureServer/2";
    }

    public String API_LOGIN;


    {
        API_LOGIN = SERVER_API + "/Login";
    }

    public String DISPLAY_NAME;


    {
        DISPLAY_NAME = SERVER_API + "/Account/Profile";
    }

    public String GENERATE_ID_SUCO;


    {
        GENERATE_ID_SUCO = SERVER_API + "/QuanLySuCo/GenerateIDSuCo";
    }

    public String LAYER_INFO;


    {
        LAYER_INFO = SERVER_API + "/Account/layerinfo";
    }

    public String IS_ACCESS;

    {
        IS_ACCESS = SERVER_API + "/Account/IsAccess/m_qlsc";
    }

    public String ADMIN_AREA_TPHCM;

    {
        ADMIN_AREA_TPHCM = "Hồ Chí Minh";
    }

    public int SCALE_IMAGE_WITH_LABLES;

    {
        SCALE_IMAGE_WITH_LABLES = 100;
    }

    public int MAX_SCALE_IMAGE_WITH_LABLES;

    {
        MAX_SCALE_IMAGE_WITH_LABLES = 5;
    }

    public class FIELD_SUCO {
        public static final String ID_SUCO = "IDSuCo";
        public static final String TRANG_THAI = "TrangThai";
        public static final String GHI_CHU = "GhiChu";
        public static final String NGUOI_CAP_NHAT = "NguoiPhanAnh";
        public static final String TGKHAC_PHUC = "TGKhacPhuc";
        public static final String NGAY_THONG_BAO = "TGPhanAnh";
        public static final String DIA_CHI = "DiaChi";
        public static final String SDT = "SDTPhanAnh";
        public static final String NGUYEN_NHAN = "NGUYENNHAN";

    }

    public Constant() {
    }
}
