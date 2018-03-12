package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import java.text.SimpleDateFormat;

/**
 * Created by ThanLe on 3/1/2018.
 */

public class Constant {
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy");
    public static enum FEATURE_ATTRIBUTES {
        ID_SUCO {
            @Override
            public String toString() {
                return "IDSuCo";
            }
        },
        VITRI_SUCO {
            @Override
            public String toString() {
                return "ViTri";
            }
        },
        TRANGTHAI_SUCO {
            @Override
            public String toString() {
                return "TrangThai";
            }
        },
        NGAYCAPNHAT_SUCO {
            @Override
            public String toString() {
                return "NgayCapNhat";
            }
        };
    }

    public static final String IDSU_CO = "IDSuCo";
    public static final String VI_TRI = "ViTri";
    public static final String TRANG_THAI = "TrangThai";
    public static final String NGAY_CAP_NHAT = "NgayCapNhat";
    public static final int REQUEST_CODE = 99;
}
