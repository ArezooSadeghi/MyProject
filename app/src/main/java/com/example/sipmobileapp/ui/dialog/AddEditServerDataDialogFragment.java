package com.example.sipmobileapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentAddEditServerDataDialogBinding;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.utils.Converter;
import com.example.sipmobileapp.utils.Others;
import com.example.sipmobileapp.viewmodel.LoginViewModel;

import java.util.List;
import java.util.Objects;

public class AddEditServerDataDialogFragment extends DialogFragment {
    private FragmentAddEditServerDataDialogBinding binding;
    private LoginViewModel viewModel;
    private String centerName;

    private static final String ARGS_CENTER_NAME = "centerName";
    private static final String ARGS_IP = "ip";
    private static final String ARGS_PORT = "port";
    private static final String ARGS_IS_ADD = "isAdd";

    public static final String TAG = AddEditServerDataDialogFragment.class.getSimpleName();

    public static AddEditServerDataDialogFragment newInstance(String centerName, String ip, String port, boolean isAdd) {
        AddEditServerDataDialogFragment fragment = new AddEditServerDataDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_CENTER_NAME, centerName);
        args.putString(ARGS_IP, ip);
        args.putString(ARGS_PORT, port);
        args.putBoolean(ARGS_IS_ADD, isAdd);
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
                R.layout.fragment_add_edit_server_data_dialog,
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

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    private void initViews() {
        assert getArguments() != null;
        centerName = getArguments().getString(ARGS_CENTER_NAME);
        String ip = getArguments().getString(ARGS_IP);
        String port = getArguments().getString(ARGS_PORT);

        binding.edTxtCenterName.setText(centerName);
        binding.edTxtIp.setText(ip);
        binding.edTxtPort.setText(port);

        binding.edTxtCenterName.setSelection(Objects.requireNonNull(binding.edTxtCenterName.getText()).length());
        binding.edTxtIp.setSelection(Objects.requireNonNull(binding.edTxtIp.getText()).length());
        binding.edTxtPort.setSelection(Objects.requireNonNull(binding.edTxtPort.getText()).length());
    }

    private void handleEvents() {
        binding.btnSave.setOnClickListener(view -> {
            String centerName = Objects.requireNonNull(binding.edTxtCenterName.getText()).toString();
            String ipAddress = Objects.requireNonNull(binding.edTxtIp.getText()).toString();
            String port = Objects.requireNonNull(binding.edTxtPort.getText()).toString();
            if (centerName.isEmpty() || ipAddress.isEmpty() || port.isEmpty()) {
                handleError(getString(R.string.fill_required_fields));
            } else if (ipAddress.length() < 7 || !Others.hasThreeDots(ipAddress) || Others.hasEnglishLetter(ipAddress) || Others.hasEnglishLetter(port)) {
                handleError(getString(R.string.wrong_ip_format));
            } else {
                if (duplicateCenterName(centerName) & !centerName.equals(AddEditServerDataDialogFragment.this.centerName)) {
                    handleError(getString(R.string.duplicate_name));
                } else {
                    ServerData serverData = new ServerData(centerName, Converter.perToEnDigitConverter(ipAddress), Converter.perToEnDigitConverter(port));
                    assert getArguments() != null;
                    boolean isAdd = getArguments().getBoolean(ARGS_IS_ADD);
                    if (!isAdd) {
                        ServerData preServerData = viewModel.getServerData(AddEditServerDataDialogFragment.this.centerName);
                        viewModel.deleteServerData(preServerData);
                    }
                    viewModel.insertServerData(serverData);
                    viewModel.getInsertNotifySpinner().setValue(true);
                    viewModel.getInsertNotifyServerDataList().setValue(true);
                    dismiss();
                }
            }
        });

        binding.edTxtCenterName.setOnEditorActionListener((textView, actionID, keyEvent) -> {
            if (actionID == 0 || actionID == EditorInfo.IME_ACTION_DONE) {
                binding.edTxtIp.requestFocus();
            }
            return false;
        });

        binding.edTxtIp.setOnEditorActionListener((textView, actionID, keyEvent) -> {
            if (actionID == 0 || actionID == EditorInfo.IME_ACTION_DONE) {
                binding.edTxtPort.requestFocus();
            }
            return false;
        });
    }

    private boolean duplicateCenterName(String input) {
        List<ServerData> serverDataList = viewModel.getServerDataList();
        assert serverDataList != null;
        if (serverDataList.size() > 0) {
            for (ServerData serverData : serverDataList) {
                if (serverData.getCenterName().equals(input)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void handleError(String msg) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(msg);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }
}