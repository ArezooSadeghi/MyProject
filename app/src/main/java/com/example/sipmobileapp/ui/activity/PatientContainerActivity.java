package com.example.sipmobileapp.ui.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipmobileapp.ui.fragment.PatientFragment;

public class PatientContainerActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return PatientFragment.newInstance();
    }

    public static Intent start(Context context) {
        return new Intent(context, PatientContainerActivity.class);
    }
}
