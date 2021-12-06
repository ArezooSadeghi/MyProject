package com.example.sipmobileapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.sipmobileapp.R;

public class RequiredServerDataDialogFragment extends DialogFragment {

    public static final String TAG = RequiredServerDataDialogFragment.class.getSimpleName();

    public static RequiredServerDataDialogFragment newInstance() {
        RequiredServerDataDialogFragment fragment = new RequiredServerDataDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage(getString(R.string.required_ip))
                .setPositiveButton(getString(R.string.confirmation_btn_txt), (dialogInterface, i) -> {
                    AddEditServerDataDialogFragment fragment = AddEditServerDataDialogFragment.newInstance("", "", "", true);
                    fragment.show(getParentFragmentManager(), AddEditServerDataDialogFragment.TAG);
                    dismiss();
                })
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}