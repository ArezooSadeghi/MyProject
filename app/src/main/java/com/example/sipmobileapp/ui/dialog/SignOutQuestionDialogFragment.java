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
import androidx.lifecycle.ViewModelProvider;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentQuestionDialogBinding;
import com.example.sipmobileapp.viewmodel.OptionsViewModel;

public class SignOutQuestionDialogFragment extends DialogFragment {
    private FragmentQuestionDialogBinding binding;
    private OptionsViewModel viewModel;

    private static final String ARGS_MSG = "msg";
    public static final String TAG = SignOutQuestionDialogFragment.class.getSimpleName();

    public static SignOutQuestionDialogFragment newInstance(String msg) {
        SignOutQuestionDialogFragment fragment = new SignOutQuestionDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_MSG, msg);
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
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_question_dialog,
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

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(OptionsViewModel.class);
    }

    private void initViews() {
        String msg = getArguments().getString(ARGS_MSG);
        binding.txtMsg.setText(msg);
    }

    private void handleEvents() {
        binding.btnNo.setOnClickListener(v -> {
            dismiss();
        });

        binding.btnYes.setOnClickListener(v -> {
            viewModel.getYesSignOutClicked().setValue(true);
            dismiss();
        });
    }
}