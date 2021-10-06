package com.example.sipmobileapp.ui.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipmobileapp.ui.fragment.AttachmentFragment;

public class AttachmentContainerActivity extends SingleFragmentActivity {

    private static final String EXTRA_SICK_ID = "sickID";

    @Override
    public Fragment createFragment() {
        int sickID = getIntent().getIntExtra(EXTRA_SICK_ID, 0);
        return AttachmentFragment.newInstance(sickID);
    }

    public static Intent start(Context context, int sickID) {
        Intent starter = new Intent(context, AttachmentContainerActivity.class);
        starter.putExtra(EXTRA_SICK_ID, sickID);
        return starter;
    }
}