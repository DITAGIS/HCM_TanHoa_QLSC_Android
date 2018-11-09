package vn.ditagis.com.tanhoa.qlsc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.ditagis.com.tanhoa.qlsc.async.CheckVersionAsycn;
import vn.ditagis.com.tanhoa.qlsc.async.LoginByAPIAsycn;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.utities.CheckConnectInternet;
import vn.ditagis.com.tanhoa.qlsc.utities.NotifyService;
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
    private DApplication mApplication;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mApplication = (DApplication) getApplication();
        mApplication.setChannelID(0);
        ButterKnife.bind(this);
        try {
            mTxtVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Button btnLogin = (findViewById(R.id.btnLogin));
        btnLogin.setOnClickListener(this);

//        mTxtUsername.setText("ditagis");
//        mTxtPassword.setText("ditagis@123");
        mTxtValidation = findViewById(R.id.txt_login_validation);
        create();

        Intent intent = new Intent(LoginActivity.this, NotifyService.class);
        startService(intent);
//        startService(new Intent(getBaseContext(), SocketServiceProvider.class));

    }


    private void create() {
        Preference.getInstance().setContext(this);
        String preference_userName = Preference.getInstance().loadPreference(getString(R.string.preference_username));
        if (preference_userName != null && !preference_userName.isEmpty()) {
            mTxtUsername.setText(Preference.getInstance().loadPreference(getString(R.string.preference_username)));
        }
        try {
            new CheckVersionAsycn(this, output -> {
                if (output != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                    builder.setCancelable(true)
                            .setPositiveButton("CẬP NHẬT", (dialogInterface, i) -> {
                                goURLBrowser(output.getLink());
                            }).setTitle("Có phiên bản mới");
                    boolean isDeveloper = false;
                    if (!output.getType().equals("RELEASE")) {
                        int anInt = Settings.Secure.getInt(this.getContentResolver(),
                                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
                        if (anInt != 0)
                            isDeveloper = true;

                    }
                    if (isDeveloper)
                        builder.setMessage("Bạn là người phát triển ứng dụng! Bạn có muốn cập nhật lên phiên bản ".concat(output.getVersionCode()).concat("?"));
                    else
                        builder.setMessage("Bạn có muốn cập nhật lên phiên bản ".concat(output.getVersionCode().concat("?")));
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Toast.makeText(LoginActivity.this, "Phiên bản hiện tại là mới nhất", Toast.LENGTH_LONG).show();
                }
            }).execute(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "Có lỗi xảy ra khi kiểm tra phiên bản", Toast.LENGTH_LONG).show();
        }

    }

    private void goURLBrowser(String url) {
        boolean result = false;
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        try {
            startActivity(intent);
            result = true;
        } catch (Exception ignored) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void login() {
        if (!CheckConnectInternet.isOnline(this)) {
            mTxtValidation.setText(R.string.validate_no_connect);
            mTxtValidation.setVisibility(View.VISIBLE);
            return;
        }
        mTxtValidation.setVisibility(View.GONE);

        String userName = mTxtUsername.getText().toString().trim();
        final String passWord = mTxtPassword.getText().toString().trim();
        if (userName.length() == 0 || passWord.length() == 0) {
            handleInfoLoginEmpty();
            return;
        }
        LoginByAPIAsycn loginAsycn = new LoginByAPIAsycn(this, () -> {
            if (mApplication.getUserDangNhap() != null)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleLoginSuccess() {
        mTxtUsername.setText("");
        mTxtPassword.setText("");

        Preference.getInstance().savePreferences(getString(R.string.preference_username), mApplication.getUserDangNhap().getUserName());
        Preference.getInstance().savePreferences(getString(R.string.preference_password), mApplication.getUserDangNhap().getPassWord());
        Preference.getInstance().savePreferences(getString(R.string.preference_displayname), mApplication.getUserDangNhap().getDisplayName());

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                login();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
