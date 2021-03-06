package com.example.sipmobileapp.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentScanBinding;
import com.example.sipmobileapp.ui.dialog.OptionDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.ScanViewModel;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private FragmentScanBinding binding;
    private ScanViewModel viewModel;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 0;

    public static ScanFragment newInstance() {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ScanViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_scan,
                container,
                false);

        if (hasCameraPermission())
            binding.ZXingScannerView.startCamera();
        else
            requestCameraPermission();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.ZXingScannerView.setResultHandler(this);
        binding.ZXingScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.ZXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        SipMobileAppPreferences.setQrCode(getContext(), rawResult.getText());
        binding.ZXingScannerView.stopCamera();
        OptionDialogFragment fragment = OptionDialogFragment.newInstance();
        fragment.show(getParentFragmentManager(), OptionDialogFragment.TAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
                binding.ZXingScannerView.startCamera();
            }
        }
    }

    private boolean hasCameraPermission() {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
    }

    private void setupObserver() {
        viewModel.getCloseClicked().observe(getViewLifecycleOwner(), closeClicked -> {
            binding.ZXingScannerView.startCamera();
            binding.ZXingScannerView.setResultHandler(this);
        });
    }
}
