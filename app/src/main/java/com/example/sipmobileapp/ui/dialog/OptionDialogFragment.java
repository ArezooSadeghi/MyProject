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
import com.example.sipmobileapp.databinding.FragmentOptionDialogBinding;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.utils.Converter;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.ScanViewModel;

public class OptionDialogFragment extends DialogFragment {
    private FragmentOptionDialogBinding binding;
    private ScanViewModel viewModel;
    public static final String TAG = OptionDialogFragment.class.getSimpleName();

    public static OptionDialogFragment newInstance() {
        OptionDialogFragment fragment = new OptionDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
        fetchPatientInfo();
        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_option_dialog,
                null,
                false);

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
        viewModel = new ViewModelProvider(requireActivity()).get(ScanViewModel.class);
    }

    private void fetchPatientInfo() {
        String centerName = SipMobileAppPreferences.getCenterName(getContext());
        String userLoginKey = SipMobileAppPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getServicePatientResult(serverData.getIp() + ":" + serverData.getPort());
        String path = "/api/v1/patients/search/";
        viewModel.fetchPatients(path, userLoginKey, String.valueOf(3387));
    }

    private void handleError(String msg) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(msg);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void handleEvents() {
        binding.btnClose.setOnClickListener(v -> {
            viewModel.getCloseClicked().setValue(true);
            dismiss();
        });
    }

    private void setupObserver() {
        viewModel.getPatientsResultSingleLiveEvent().observe(this, patientResult -> {
            if (patientResult != null) {
                if (patientResult.getErrorCode().equals("0")) {
                    binding.tvPatientName.setText(Converter.letterConverter(patientResult.getPatients()[0].getPatientName()));
                    binding.tvPatientServices.setText(Converter.letterConverter(patientResult.getPatients()[0].getServices()));
                    binding.tvDate.setText(patientResult.getPatients()[0].getDate());
                    binding.tvBarCode.setText("بارکد:3387");
                } else {
                    handleError(patientResult.getError());
                }
            }
        });
    }
}