package com.example.sipmobileapp.ui.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipmobileapp.ui.dialog.ServerDataFragment;

public class ServerDataContainerActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return ServerDataFragment.newInstance();
    }

    public static Intent start(Context context) {
        return new Intent(context, ServerDataContainerActivity.class);
    }
}