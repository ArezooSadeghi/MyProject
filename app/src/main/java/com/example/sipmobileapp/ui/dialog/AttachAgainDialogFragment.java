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
import com.example.sipmobileapp.databinding.FragmentQuestionDialogBinding;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

public class AttachAgainDialogFragment extends DialogFragment {
    private FragmentQuestionDialogBinding binding;
    private AttachmentViewModel viewModel;

    private static final String ARGS_MESSAGE = "message";
    public static final String TAG = AttachAgainDialogFragment.class.getSimpleName();

    public static AttachAgainDialogFragment newInstance(String message) {
        AttachAgainDialogFragment fragment = new AttachAgainDialogFragment();
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
                R.layout.fragment_question_dialog,
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
        String message = getArguments().getString(ARGS_MESSAGE);
        binding.txtQuestion.setText(message);
    }

    private void handleEvents() {
        binding.btnNo.setOnClickListener(view -> {
            viewModel.getNoAttachAgain().setValue(true);
            dismiss();
        });

        binding.btnYes.setOnClickListener(view -> {
            viewModel.getYesAttachAgain().setValue(true);
            dismiss();
        });
    }
}