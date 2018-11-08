package vn.ditagis.com.tanhoa.qlsc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import vn.ditagis.com.tanhoa.qlsc.async.ChangePasswordAsycn;


public class DoiMatKhauActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEtxtOldPassord;
    private EditText mEtxtNewPassword;
    private EditText mEtxtNewPasswordConfirm;
    private TextView mTxtValidation;
    private TextView mTxtChangePassword;
    private LinearLayout mLayoutChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);
        mEtxtOldPassord = findViewById(R.id.etxt_change_password_old);
        mEtxtNewPassword = findViewById(R.id.etxt_change_password_new);
        mEtxtNewPasswordConfirm = findViewById(R.id.etxt_change_password_new_confirm);
        mTxtValidation = findViewById(R.id.txt_change_password_validation);
        mTxtChangePassword = findViewById(R.id.txt_change_password);
        mLayoutChangePassword = findViewById(R.id.layout_changepassword);

        findViewById(R.id.btn_change_password).setOnClickListener(this);
        View.OnKeyListener keyListener = new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    changPassword();
                }
                return false;
            }
        };
        mEtxtNewPasswordConfirm.setOnKeyListener(keyListener);

    }

    private boolean validate() {
        if (!mEtxtNewPassword.getText().toString().trim().equals(mEtxtNewPasswordConfirm.getText().toString().trim())) {
            mTxtValidation.setVisibility(View.VISIBLE);
            mTxtValidation.setText(getString(R.string.validate_change_password));
            return false;
        }
        if (mEtxtNewPassword.getText().toString().trim().length() < 6) {
            mTxtValidation.setVisibility(View.VISIBLE);
            mTxtValidation.setText(getString(R.string.validate_change_password_lack));
            return false;
        }
        mTxtValidation.setVisibility(View.GONE);
        return true;
    }

    private void changPassword() {
        if (validate()) {
            ChangePasswordAsycn asycn = new ChangePasswordAsycn(this, output -> {
                if (output) {
                    mTxtValidation.setVisibility(View.GONE);
                    Toast.makeText(DoiMatKhauActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    DoiMatKhauActivity.this.finish();

                } else {
                    mTxtValidation.setVisibility(View.VISIBLE);
                    mTxtValidation.setText(getString(R.string.validate_change_password_fail));
                }
            });
            asycn.execute(mEtxtOldPassord.getText().toString().trim(),
                    mEtxtNewPasswordConfirm.getText().toString().trim());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_password:
                changPassword();
                break;
        }
    }


}
