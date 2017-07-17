package com.example.armen.pl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.armen.pl.R;
import com.example.armen.pl.io.bus.BusProvider;
import com.example.armen.pl.util.Preference;

/**
 * Created by Armen on 7/16/2017.
 */

public class SignInActivity  extends BaseActivity implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = SignInActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private Button mBtnSign;
    private TextInputLayout mTilEmail;
    private TextInputEditText mTietEmail;
    private TextInputLayout mTilPass;
    private TextInputEditText mTietPass;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.register(this);
        findViews();
        setListeners();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                String mail = mTietEmail.getText().toString();
                String pass = mTietPass.getText().toString();
                grabDataAndSingIn(mail, pass);
                break;
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {
        mBtnSign.setOnClickListener(this);
    }

    private void findViews() {
        mTilEmail = (TextInputLayout) findViewById(R.id.til_sign_in_email);
        mTietEmail = (TextInputEditText) findViewById(R.id.tiet_sign_in_email);
        mTilPass = (TextInputLayout) findViewById(R.id.til_sign_in_pass);
        mTietPass = (TextInputEditText) findViewById(R.id.tiet_sign_in_pass);
        mBtnSign = (Button) findViewById(R.id.btn_sign_in);
    }

    private void grabDataAndSingIn(String mail, String pass) {
        boolean isValidationSucceeded = true;

        // validate email (empty or not)
        if (mail.trim().length() == 0) {
            isValidationSucceeded = false;
            mTilEmail.setError(getString(R.string.msg_edt_error_empty_null));
        }

        // validate email length(  > 8)
//        if (mail.length() < 8) {
//            isValidationSucceeded = false;
//            mTilEmail.setError(getString(R.string.msg_edt_error_pass_length));
//        }

        // validate email (contain @)
//        if (!mail.contains("@")) {
//            isValidationSucceeded = false;
//            mTilEmail.setError(getString(R.string.msg_edt_error_valid_mail));
//        }

        // validate password (empty or not)
        if (pass.trim().length() == 0) {
            isValidationSucceeded = false;
            mTilPass.setError(getString(R.string.msg_edt_error_pass_empty_null));
        }

        // validate password length(  > 3)
//        if (pass.length() < 4) {
//            isValidationSucceeded = false;
//            mTilPass.setError(getString(R.string.msg_edt_error_pass_length));
//        }

        // if required fields are filled up then proceed with login
        if (isValidationSucceeded) {

            mTilEmail.setErrorEnabled(false);
            mTilPass.setErrorEnabled(false);

            Preference.getInstance(this).setUserMail(mail);
            Preference.getInstance(this).setUserPass(pass);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

// ===========================================================
// Inner and Anonymous Classes
// ===========================================================

}