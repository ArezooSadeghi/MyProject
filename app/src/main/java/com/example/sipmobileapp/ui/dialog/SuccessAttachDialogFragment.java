package com.example.sipmobileapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentSuccessDialogBinding;
import com.example.sipmobileapp.utils.Converter;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

public class SuccessAttachDialogFragment extends DialogFragment {
    private FragmentSuccessDialogBinding binding;
    private AttachmentViewModel viewModel;

    private static final String ARGS_MESSAGE = "message";
    public static final String TAG = SuccessAttachDialogFragment.class.getSimpleName();

    public static SuccessAttachDialogFragment newInstance(String message) {
        SuccessAttachDialogFragment fragment = new SuccessAttachDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_success_dialog,
                null,
                false);

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initViews() {
        assert getArguments() != null;
        String message = Converter.letterConverter(getArguments().getString(ARGS_MESSAGE));
        binding.txtSuccessMessage.setText(message);
    }

    private void handleEvents() {
        binding.btnClose.setOnClickListener(view -> {
            viewModel.getShowAttachAgainDialog().setValue(true);
            dismiss();
        });
    }
}