package com.example.sipmobileapp.ui.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentSuccessDialogBinding;

public class SuccessDialogFragment extends DialogFragment {
    private FragmentSuccessDialogBinding binding;

    private static final String ARGS_MESSAGE = "message";
    public static final String TAG = SuccessDialogFragment.class.getSimpleName();

    public static SuccessDialogFragment newInstance(String message) {
        SuccessDialogFragment fragment = new SuccessDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_MESSAGE, message);
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
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_success_dialog,
                null,
                false);

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void initViews() {
        assert getArguments() != null;
        String message = getArguments().getString(ARGS_MESSAGE);
        binding.txtSuccessMessage.setText(message);
    }

    private void handleEvents() {
        binding.btnClose.setOnClickListener(v -> {
            getActivity().finish();
            dismiss();
        });
    }
}