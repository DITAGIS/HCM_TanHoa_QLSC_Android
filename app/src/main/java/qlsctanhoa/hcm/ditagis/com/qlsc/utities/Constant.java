package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import java.text.SimpleDateFormat;

/**
 * Created by ThanLe on 3/1/2018.
 */

public class Constant {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");

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

    public static final String FEATURE_ATTRIBUTE_ID_SUCO = "IDSuCo";
    public static final String FEATURE_ATTRIBUTE_VITRI_SUCO = "ViTri";
    public static final String FEATURE_ATTRIBUTE_TRANGTHAI_SUCO = "TrangThai";
    public static final String FEATURE_ATTRIBUTE_NGAYCAPNHAT_SUCO = "NgayCapNhat";
}
