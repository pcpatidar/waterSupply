package com.example.berylsystems.watersupply.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class Validation {
    public static  boolean validateName(EditText mName) {
        if (mName.getText().toString().trim().isEmpty() || mName.getText().toString().trim().length() < 5) {
            return false;
        }
        return true;
    }

    public static  boolean validateMobile(EditText mMobile) {
        if (mMobile.getText().toString().trim().isEmpty() || mMobile.getText().toString().length() != 10) {
            return false;
        }
        return true;
    }

    public static boolean validateEmail(EditText mEmail) {
        String emailAdd = mEmail.getText().toString().trim();
        if (emailAdd.isEmpty() || !isValidEmail(emailAdd)) {
            return false;
        }
        return true;
    }

    public static boolean validatePassword(EditText mPassword) {
        if (mPassword.getText().toString().trim().isEmpty()) {
            return false;
        } else if (mPassword.getText().toString().trim().length() < 5) {
            return false;
        } else {
        }

        return true;
    }

    public static  boolean validateConfirmPassword(EditText confirmPassword,EditText mPassword) {
        if (!confirmPassword.getText().toString().trim().equals(mPassword.getText().toString().trim())) {
            return false;
        } else {
        }
        return true;
    }

    public  static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static  void requestFocus(Activity context,View view) {
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

}
