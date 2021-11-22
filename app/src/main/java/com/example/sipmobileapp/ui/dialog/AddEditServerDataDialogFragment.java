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
import com.example.sipmobileapp.viewmodel.LoginViewModel;

import java.util.List;
import java.util.Objects;

public class AddEditServerDataDialogFragment extends DialogFragment {
    private FragmentAddEditServerDataDialogBinding binding;
    private LoginViewModel viewModel;

    private String centerName;

    private static final String ARGS_CENTER_NAME = "centerName";
    private static final String ARGS_IP_ADDRESS = "ipAddress";
    private static final String ARGS_PORT = "port";
    private static final String ARGS_IS_ADD = "isAdd";

    public static final String TAG = AddEditServerDataDialogFragment.class.getSimpleName();

    public static AddEditServerDataDialogFragment newInstance(String centerName, String ipAddress, String port, boolean isAdd) {
        AddEditServerDataDialogFragment fragment = new AddEditServerDataDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_CENTER_NAME, centerName);
        args.putString(ARGS_IP_ADDRESS, ipAddress);
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
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_add_edit_server_data_dialog,
                null,
                false);

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
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
        String ipAddress = getArguments().getString(ARGS_IP_ADDRESS);
        String port = getArguments().getString(ARGS_PORT);

        binding.edTxtCenterName.setText(centerName);
        binding.edTxtIpAddress.setText(ipAddress);
        binding.edTxtPort.setText(port);

        binding.edTxtCenterName.setSelection(Objects.requireNonNull(binding.edTxtCenterName.getText()).length());
        binding.edTxtIpAddress.setSelection(Objects.requireNonNull(binding.edTxtIpAddress.getText()).length());
        binding.edTxtPort.setSelection(Objects.requireNonNull(binding.edTxtPort.getText()).length());
    }

    private void handleEvents() {
        binding.fabOk.setOnClickListener(view -> {
            String centerName = Objects.requireNonNull(binding.edTxtCenterName.getText()).toString();
            String ipAddress = Objects.requireNonNull(binding.edTxtIpAddress.getText()).toString();
            String port = Objects.requireNonNull(binding.edTxtPort.getText()).toString();
            if (centerName.isEmpty() || ipAddress.isEmpty() || port.isEmpty()) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance("لطفا موارد خواسته شده را تکمیل کنید");
                fragment.show(getChildFragmentManager(), ErrorDialogFragment.TAG);
            } else if (ipAddress.length() < 7 || !hasThreeDots(ipAddress) || hasEnglishLetter(ipAddress) || hasEnglishLetter(port)) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance("فرمت آدرس ip نادرست می باشد");
                fragment.show(getChildFragmentManager(), ErrorDialogFragment.TAG);
            } else {
                if (isRepeatedCenterName(centerName) & !centerName.equals(AddEditServerDataDialogFragment.this.centerName)) {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance("نام مرکز تکراری می باشد");
                    fragment.show(getChildFragmentManager(), ErrorDialogFragment.TAG);
                } else {
                    ServerData serverData = new ServerData(centerName, convertPerDigitToEn(ipAddress), convertPerDigitToEn(port));
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
                binding.edTxtIpAddress.requestFocus();
            }
            return false;
        });

        binding.edTxtIpAddress.setOnEditorActionListener((textView, actionID, keyEvent) -> {
            if (actionID == 0 || actionID == EditorInfo.IME_ACTION_DONE) {
                binding.edTxtPort.requestFocus();
            }
            return false;
        });
    }

    private boolean hasEnglishLetter(String ipAddressOrPort) {
        StringBuilder result = new StringBuilder();
        char[] chars = ipAddressOrPort.toCharArray();
        for (Character character : chars) {
            if (!String.valueOf(character).equals(".")) {
                result.append(character);
            }
        }

        return result.toString().matches(".*[a-zA-Z]+.*");
    }

    private boolean hasThreeDots(String ipAddress) {
        int numberOfDots = 0;
        char[] chars = ipAddress.toCharArray();
        for (Character character : chars) {
            if (String.valueOf(character).equals(".")) {
                numberOfDots++;
            }
        }
        return numberOfDots == 3;
    }

    private boolean isRepeatedCenterName(String centerName) {
        List<ServerData> serverDataList = viewModel.getServerDataList();
        assert serverDataList != null;
        if (serverDataList.size() > 0) {
            for (ServerData serverData : serverDataList) {
                if (serverData.getCenterName().equals(centerName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String convertPerDigitToEn(String ipAddressOrPort) {
        return ipAddressOrPort.replaceAll("۰", "0").
                replaceAll("۱", "1").
                replaceAll("۲", "2").
                replaceAll("۳", "3").
                replaceAll("۴", "4").
                replaceAll("۵", "5").
                replaceAll("۶", "6").
                replaceAll("۷", "7").
                replaceAll("۸", "8").
                replaceAll("۹", "9").
                replaceAll(" ", "");
    }
}