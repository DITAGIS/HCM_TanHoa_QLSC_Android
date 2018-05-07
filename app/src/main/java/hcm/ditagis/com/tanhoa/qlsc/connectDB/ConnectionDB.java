package hcm.ditagis.com.tanhoa.qlsc.connectDB;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private static final String PROTOCOL = "jdbc:jtds:sqlserver://";
    private static final String SERVER = "103.74.117.51";
    private static final String SERVER14 = "113.161.88.180";
    private static final String SERVER_LOCAL = "12.0.4237.0";
    private static final String INSTANCT_NAME = "MSSQLSERVER";
    private static final int PORT = 1433;
    private static final int PORT14 = 1810;
    private static final String DB = "DocSoTH";
    private static final String DB_IMAGE = "DocSoTH_Hinh";
    private static final String DB14 = "DocSoTH2";
    private static final String USER = "docsotanhoa";
    private static final String USER_LOCAL = "sa";
    private static final String PASSWORD = "Docso111";
    private static final String PASSWORD_LOCAL = "123456";
    private static final ConnectionDB _instance = new ConnectionDB();
    private Connection connection;
    private Connection connection_image;

    private ConnectionDB() {
        connection = getConnect();
//        connection_image = getConnect_image();
    }

    public static final ConnectionDB getInstance() {
        return _instance;
    }

    public static void main(String[] args) {
        ConnectionDB cndb = new ConnectionDB();
        System.out.println(cndb.getConnect());
    }

    public Connection getConnection() {
        if (connection == null)
            connection = getConnect();
        return connection;
    }

    public Connection getConnectionImage() {
//        if (connection_image == null)
//            connection_image = getConnect_image();
        return connection_image;
    }

    public Connection getConnection(boolean isLogin) {
        connection = getConnect();
//        connection_image = getConnect_image();
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void reConnect() {
        connection = null;
    }

    private Connection getConnect() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = String.format("jdbc:jtds:sqlserver://%s:%s/%s;instance=%s", SERVER, PORT, DB, INSTANCT_NAME);
        Connection cnn = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            cnn = DriverManager.getConnection(url, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cnn;
    }

    private Connection getConnect_image() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = String.format("jdbc:jtds:sqlserver://%s:%s/%s;instance=%s", SERVER, PORT, DB_IMAGE, INSTANCT_NAME);
        Connection cnn = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            cnn = DriverManager.getConnection(url, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnn;
    }
}