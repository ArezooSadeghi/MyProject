package com.example.sipmobileapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentLoginBinding;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.model.UserResult;
import com.example.sipmobileapp.ui.activity.PatientContainerActivity;
import com.example.sipmobileapp.ui.dialog.ErrorDialogFragment;
import com.example.sipmobileapp.ui.dialog.RequiredServerDataDialogFragment;
import com.example.sipmobileapp.ui.dialog.ServerDataListDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.LoginViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;
    private String lastValueSpinner;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_login,
                container,
                false);

        if (viewModel.getServerDataList().size() == 0 || viewModel.getServerDataList() == null) {
            RequiredServerDataDialogFragment fragment = RequiredServerDataDialogFragment.newInstance();
            fragment.show(getParentFragmentManager(), RequiredServerDataDialogFragment.TAG);
        } else {
            setupSpinner();
        }

        handleEvents();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    private void setupSpinner() {
        List<ServerData> serverDataList = viewModel.getServerDataList();
        List<String> centerNameList = new ArrayList<>();
        for (ServerData serverData : serverDataList) {
            centerNameList.add(serverData.getCenterName());
        }
        binding.spinnerServerData.setItems(centerNameList);
        if (centerNameList.size() > 0) {
            lastValueSpinner = centerNameList.get(0);
        }
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void handleEvents() {
        binding.ivMore.setOnClickListener(view -> {
            ServerDataListDialogFragment fragment = ServerDataListDialogFragment.newInstance();
            fragment.show(getParentFragmentManager(), ServerDataListDialogFragment.TAG);
        });

        binding.edTxtUserName.setOnEditorActionListener((textView, actionID, keyEvent) -> {
            if (actionID == 0 || actionID == EditorInfo.IME_ACTION_DONE) {
                binding.edTxtPassword.requestFocus();
            }
            return false;
        });

        binding.btnLogin.setOnClickListener(view -> {
            if (viewModel.getServerDataList() == null || viewModel.getServerDataList().size() == 0) {
                RequiredServerDataDialogFragment fragment = RequiredServerDataDialogFragment.newInstance();
                fragment.show(getParentFragmentManager(), RequiredServerDataDialogFragment.TAG);
            } else if (Objects.requireNonNull(binding.edTxtUserName.getText()).toString().isEmpty() || Objects.requireNonNull(binding.edTxtPassword.getText()).toString().isEmpty()) {
                handleError("نام کاربری و رمز عبور را وارد نمایید");
            } else {
                binding.loadingLayout.setVisibility(View.VISIBLE);
                binding.edTxtUserName.setEnabled(false);
                binding.edTxtPassword.setEnabled(false);
                binding.btnLogin.setEnabled(false);

                String userName = binding.edTxtUserName.getText().toString();
                String password = binding.edTxtPassword.getText().toString();

                UserResult.UserParameter userParameter = new UserResult().new UserParameter(userName, password);
                ServerData serverData = viewModel.getServerData(lastValueSpinner);

                viewModel.getUserLoginService(serverData.getIpAddress() + ":" + serverData.getPort());
                String path = "/api/v1/users/login/";
                viewModel.login(path, userParameter);
            }
        });

        binding.spinnerServerData.setOnItemSelectedListener((view, position, id, item) -> lastValueSpinner = (String) item);
    }

    private void setupObserver() {
        viewModel.getInsertNotifySpinner().observe(getViewLifecycleOwner(), isInsertServerData -> setupSpinner());

        viewModel.getDeleteNotifySpinner().observe(getViewLifecycleOwner(), isDeleteServerData -> setupSpinner());

        viewModel.getLoginResultSingleLiveEvent().observe(getViewLifecycleOwner(), userResult -> {
            if (userResult != null) {
                if (userResult.getErrorCode().equals("0")) {
                    SipMobileAppPreferences.setUserLoginKey(getContext(), userResult.getUsers()[0].getUserLoginKey());
                    SipMobileAppPreferences.setCenterName(getContext(), lastValueSpinner);
                    Intent intent = PatientContainerActivity.start(getContext());
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    binding.loadingLayout.setVisibility(View.GONE);
                    binding.edTxtUserName.setEnabled(true);
                    binding.edTxtPassword.setEnabled(true);
                    binding.btnLogin.setEnabled(true);
                    handleError(userResult.getError());
                }
            }

        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), message -> {
            binding.loadingLayout.setVisibility(View.GONE);
            binding.edTxtUserName.setEnabled(true);
            binding.edTxtPassword.setEnabled(true);
            binding.btnLogin.setEnabled(true);
            handleError(message);
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), message -> {
            binding.loadingLayout.setVisibility(View.GONE);
            binding.edTxtUserName.setEnabled(true);
            binding.edTxtPassword.setEnabled(true);
            binding.btnLogin.setEnabled(true);
            handleError(message);
        });

        viewModel.getWrongIpAddressSingleLiveEvent().observe(getViewLifecycleOwner(), message -> {
            binding.loadingLayout.setVisibility(View.GONE);
            binding.edTxtUserName.setEnabled(true);
            binding.edTxtPassword.setEnabled(true);
            binding.btnLogin.setEnabled(true);
            handleError(message);
        });
    }
}