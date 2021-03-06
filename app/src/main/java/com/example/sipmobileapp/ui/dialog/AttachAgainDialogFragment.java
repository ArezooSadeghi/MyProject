package com.example.sipmobileapp.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

    private static final String ARGS_MSG = "msg";
    public static final String TAG = AttachAgainDialogFragment.class.getSimpleName();

    public static AttachAgainDialogFragment newInstance(String msg) {
        AttachAgainDialogFragment fragment = new AttachAgainDialogFragment();
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
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initViews() {
        assert getArguments() != null;
        String msg = getArguments().getString(ARGS_MSG);
        binding.txtMsg.setText(msg);

        String uri = "@drawable/ic_attachment";
        int imageResource = getResources().getIdentifier(uri, null, requireContext().getPackageName());
        @SuppressLint("UseCompatLoadingForDrawables") Drawable src = getResources().getDrawable(imageResource);
        binding.ivSrc.setImageDrawable(src);
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