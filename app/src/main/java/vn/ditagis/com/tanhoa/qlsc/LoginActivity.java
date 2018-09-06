package vn.ditagis.com.tanhoa.qlsc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import vn.ditagis.com.tanhoa.qlsc.async.LoginByAPIAsycn;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.libs.Constants;
import vn.ditagis.com.tanhoa.qlsc.utities.CheckConnectInternet;
import vn.ditagis.com.tanhoa.qlsc.utities.Preference;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {
    @BindView(R.id.txtUsername)
    TextView mTxtUsername;
    @BindView(R.id.txtPassword)
    TextView mTxtPassword;
    @BindView(R.id.txt_login_validation)
    TextView mTxtValidation;
    @BindView(R.id.txt_version_login)
    TextView mTxtVersion;
    private Socket mSocket;
    private boolean isLastLogin;
    private DApplication mApplication;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mApplication = (DApplication) getApplication();
        ButterKnife.bind(this);
        try {
            mTxtVersion.setText("Phiên bản: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Button btnLogin = (findViewById(R.id.btnLogin));
        btnLogin.setOnClickListener(this);
        findViewById(R.id.txt_login_changeAccount).setOnClickListener(this);

//        mTxtUsername.setText("ditagis");
//        mTxtPassword.setText("ditagis@123");
        mTxtValidation = findViewById(R.id.txt_login_validation);
        create();


    }

    private Emitter.Listener onInfinity = args -> {
        if (args != null && args.length > 0)
            Log.d("Nhận", args[0].toString());
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
        LoginByAPIAsycn loginAsycn = new LoginByAPIAsycn(this, () -> {
            if (mApplication.getUserDangNhap != null)
                handleLoginSuccess();
            else
                handleLoginFail();
        });
        loginAsycn.execute(userName, passWord);
    }

    @SuppressLint("HardwareIds")

    private void handleInfoLoginEmpty() {
        mTxtValidation.setText(R.string.info_login_empty);
        mTxtValidation.setVisibility(View.VISIBLE);
    }

    private void handleLoginFail() {
        mTxtValidation.setText(R.string.validate_login_fail);
        mTxtValidation.setVisibility(View.VISIBLE);
    }

    private void handleLoginSuccess() {
        // GPS
        final DApplication app = (DApplication) getApplication();
        mSocket = app.getSocket();
        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds
        handler.postDelayed(new Runnable() {
            public void run() {
                //do something
                if (app.getmLocation() != null) {
                    Log.d("gửi", "hhi");
                    if (mApplication.getUserDangNhap != null &&
                            mApplication.getUserDangNhap.getUserName() != null)
                        mSocket.emit(Constants.EVENT_STAFF_NAME, Constants.APP_ID + "," + mApplication.getUserDangNhap.getUserName());
                    Emitter emit1 = mSocket.emit(Constants.EVENT_LOCATION,
                            app.getmLocation().getLatitude() + "," + app.getmLocation().getLongitude());
                    Log.d("Kết quả vị trí", emit1.hasListeners(Constants.EVENT_LOCATION) + "");
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
        mSocket.on(Constants.EVENT_STAFF_NAME, onInfinity);
        mSocket.on(Constants.EVENT_LOCATION, onInfinity);

        mSocket.connect();
        mTxtUsername.setText("");
        mTxtPassword.setText("");

        Preference.getInstance().savePreferences(getString(R.string.preference_username), mApplication.getUserDangNhap.getUserName());
        Preference.getInstance().savePreferences(getString(R.string.preference_password), mApplication.getUserDangNhap.getPassWord());
        Preference.getInstance().savePreferences(getString(R.string.preference_displayname), mApplication.getUserDangNhap.getDisplayName());

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
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
