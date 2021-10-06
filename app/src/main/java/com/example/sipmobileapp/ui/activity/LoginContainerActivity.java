package com.example.sipmobileapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sipmobileapp.ui.fragment.LoginFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;

public class LoginContainerActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return LoginFragment.newInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SipMobileAppPreferences.getUserLoginKey(this) != null) {
            Intent starter = PatientContainerActivity.start(this);
            startActivity(starter);
            finish();
        }
    }

    public static Intent start(Context context) {
        return new Intent(context, LoginContainerActivity.class);
    }
}