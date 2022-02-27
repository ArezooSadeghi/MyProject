package com.example.sipmobileapp.ui.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import com.example.sipmobileapp.viewmodel.LoginViewModel;

public class QuestionDeleteServerDataDialogFragment extends DialogFragment {
    private FragmentQuestionDialogBinding binding;
    private LoginViewModel viewModel;

    private static final String ARGS_MSG = "msg";
    public static final String TAG = QuestionDeleteServerDataDialogFragment.class.getSimpleName();

    public static QuestionDeleteServerDataDialogFragment newInstance(String msg) {
        QuestionDeleteServerDataDialogFragment fragment = new QuestionDeleteServerDataDialogFragment();
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

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    private void initViews() {
        String msg = getArguments().getString(ARGS_MSG);
        binding.txtMsg.setText(msg);

        String uri = "@drawable/ic_delete";
        int imageResource = getResources().getIdentifier(uri, null, getContext().getPackageName());
        Drawable drawable = getResources().getDrawable(imageResource);
        binding.ivSrc.setImageDrawable(drawable);
    }

    private void handleEvents() {
        binding.btnNo.setOnClickListener(v -> dismiss());

        binding.btnYes.setOnClickListener(v -> {
            viewModel.getYesDeleteClicked().setValue(true);
            dismiss();
        });
    }
}