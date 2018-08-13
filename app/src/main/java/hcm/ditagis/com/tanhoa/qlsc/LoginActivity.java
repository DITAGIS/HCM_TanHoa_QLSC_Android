package hcm.ditagis.com.tanhoa.qlsc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import hcm.ditagis.com.tanhoa.qlsc.async.LoginAsycn;
import hcm.ditagis.com.tanhoa.qlsc.entities.entitiesDB.KhachHang;
import hcm.ditagis.com.tanhoa.qlsc.socket.TanHoaApplication;
import hcm.ditagis.com.tanhoa.qlsc.socket.LocationHelper;
import hcm.ditagis.com.tanhoa.qlsc.utities.CheckConnectInternet;
import hcm.ditagis.com.tanhoa.qlsc.utities.Preference;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private TextView mTxtUsername;
    private TextView mTxtPassword;
    private boolean isLastLogin;
    private TextView mTxtValidation;
    private String IMEI = "";
    private Location mLocation;
    private Socket mSocket;
    private LocationHelper mLocationHelper;
    private String mmEvent = "vitrinhanvien";
    private String mmEventStaffName = "tennhanvien";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = (findViewById(R.id.btnLogin));
        btnLogin.setOnClickListener(this);
        findViewById(R.id.txt_login_changeAccount).setOnClickListener(this);

        mTxtUsername = findViewById(R.id.txtUsername);
        mTxtPassword = findViewById(R.id.txtPassword);

        mTxtValidation = findViewById(R.id.txt_login_validation);

        create();

        // GPS
        TanHoaApplication app = (TanHoaApplication) getApplication();
        mSocket = app.getSocket();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationHelper = new LocationHelper(this, new LocationHelper.AsyncResponse() {
            @Override
            public void processFinish(double longtitude, double latitude) {

            }

        });
        mLocationHelper.checkpermission();
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
//                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(i);
                mLocationHelper.execute();
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds
        handler.postDelayed(new Runnable() {
            public void run() {
                //do something
                if (mLocation != null) {
                    Log.d("gửi", "hhi");
                    Emitter emit = mSocket.emit(mmEventStaffName, "than" );
                    Emitter emit1 = mSocket.emit(mmEvent, mLocation.getLatitude() + "," + mLocation.getLongitude());
                    Log.d("Kết quả tên", emit.hasListeners(mmEventStaffName) + "");
                    Log.d("Kết quả vị trí", emit1.hasListeners(mmEvent) + "");
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
        mSocket.on(mmEventStaffName, onInfinity);
        mSocket.on(mmEvent, onInfinity);

        mSocket.connect();
//        mSocket.on(mmEvent, onInfinity);
    }

    private Emitter.Listener onInfinity = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0)
                Log.d("Nhận", args[0].toString());
        }
    };

    private void create() {
        Preference.getInstance().setContext(this);
        String preference_userName = Preference.getInstance().loadPreference(getString(R.string.preference_username));

        //nếu chưa từng đăng nhập thành công trước đó
        //nhập username và password bình thường
        if (preference_userName == null || preference_userName.isEmpty()) {
            findViewById(R.id.layout_login_tool).setVisibility(View.GONE);
            findViewById(R.id.layout_login_username).setVisibility(View.VISIBLE);
            isLastLogin = false;
        }
        //ngược lại
        //chỉ nhập pasword
        else {
            isLastLogin = true;
            findViewById(R.id.layout_login_tool).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_login_username).setVisibility(View.GONE);
        }

    }

    private void login() {
        if (!CheckConnectInternet.isOnline(this)) {
            mTxtValidation.setText(R.string.validate_no_connect);
            mTxtValidation.setVisibility(View.VISIBLE);
            return;
        }
        mTxtValidation.setVisibility(View.GONE);

        String userName;
        if (isLastLogin)
            userName = Preference.getInstance().loadPreference(getString(R.string.preference_username));
        else
            userName = mTxtUsername.getText().toString().trim();
        final String passWord = mTxtPassword.getText().toString().trim();
        if (userName.length() == 0 || passWord.length() == 0) {
            handleInfoLoginEmpty();
            return;
        }
//        handleLoginSuccess(userName,passWord);
        final String finalUserName = userName;
        LoginAsycn loginAsycn = new LoginAsycn(this, false, new LoginAsycn.AsyncResponse() {

            @Override
            public void processFinish(KhachHang output) {
                if (output != null)
                    handleLoginSuccess(output);
                else
                    handleLoginFail();
            }
        });
        loginAsycn.execute(userName, passWord);
    }

    private void loginWithIMEI() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        IMEI = ((TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (!CheckConnectInternet.isOnline(this)) {
            mTxtValidation.setText(R.string.validate_no_connect);
            mTxtValidation.setVisibility(View.VISIBLE);
            return;
        }
        mTxtValidation.setVisibility(View.GONE);

        String userName;
        if (isLastLogin)
            userName = Preference.getInstance().loadPreference(getString(R.string.preference_username));
        else
            userName = mTxtUsername.getText().toString().trim();
        final String passWord = mTxtPassword.getText().toString().trim();
        if (userName.length() == 0 || passWord.length() == 0) {
            handleInfoLoginEmpty();
            return;
        }
//        handleLoginSuccess(userName,passWord);
        final String finalUserName = userName;
        LoginAsycn loginAsycn = new LoginAsycn(this, true, new LoginAsycn.AsyncResponse() {

            @Override
            public void processFinish(KhachHang output) {
                if (output != null)
                    handleLoginSuccess(output);
                else
                    handleLoginFail();
            }
        });
        loginAsycn.execute(userName, passWord, IMEI);
    }

    private void handleInfoLoginEmpty() {
        mTxtValidation.setText(R.string.info_login_empty);
        mTxtValidation.setVisibility(View.VISIBLE);
    }

    private void handleLoginFail() {
        mTxtValidation.setText(R.string.validate_login_fail);
        mTxtValidation.setVisibility(View.VISIBLE);
    }

    private void handleLoginSuccess(KhachHang khachHang) {
        mTxtUsername.setText("");
        mTxtPassword.setText("");

        Preference.getInstance().savePreferences(getString(R.string.preference_username), khachHang.getUserName());
        Preference.getInstance().savePreferences(getString(R.string.preference_password), khachHang.getPassWord());
        Preference.getInstance().savePreferences(getString(R.string.preference_displayname), khachHang.getDisplayName());

        Intent intent = new Intent(this, QuanLySuCo.class);

        startActivity(intent);
    }

    private void changeAccount() {
        mTxtUsername.setText("");
        mTxtPassword.setText("");

        Preference.getInstance().savePreferences(getString(R.string.preference_username), "");
        create();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.txt_login_changeAccount:
                changeAccount();
                break;
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                if (mTxtPassword.getText().toString().trim().length() > 0) {
                    login();
                    return true;
                }
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
