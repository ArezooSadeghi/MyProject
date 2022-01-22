package com.example.sipmobileapp.ui.fragment;

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
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentLoginBinding;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.model.UserResult;
import com.example.sipmobileapp.ui.dialog.ErrorDialogFragment;
import com.example.sipmobileapp.ui.dialog.WarningDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.LoginViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;
    private String centerName;

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

    private void setupSpinner(List<ServerData> serverDataList) {
        List<String> centerNames = new ArrayList<>();
        for (ServerData serverData : serverDataList) {
            centerNames.add(serverData.getCenterName());
        }
        binding.spinner.setItems(centerNames);
        if (centerNames.size() > 0)
            centerName = centerNames.get(0);
    }

    private void login(UserResult.UserParameter parameter) {
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getUserLoginService(serverData.getIp() + ":" + serverData.getPort());
        String path = "/api/v1/users/login/";
        viewModel.login(path, parameter);
    }

    private void handleError(String msg) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(msg);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void handleEvents() {
        binding.ivMore.setOnClickListener(view -> {
            NavDirections action = LoginFragmentDirections.actionLoginFragmentToServerDataFragment();
            NavHostFragment.findNavController(this).navigate(action);
        });

        binding.edTxtUserName.setOnEditorActionListener((textView, actionID, keyEvent) -> {
            if (actionID == 0 || actionID == EditorInfo.IME_ACTION_DONE) {
                binding.edTxtPassword.requestFocus();
            }
            return false;
        });

        binding.btnLogin.setOnClickListener(view -> {
            if (Objects.requireNonNull(binding.edTxtUserName.getText()).toString().isEmpty() || Objects.requireNonNull(binding.edTxtPassword.getText()).toString().isEmpty()) {
                handleError(getString(R.string.fill_required_fields));
            } else {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                binding.edTxtUserName.setEnabled(false);
                binding.edTxtPassword.setEnabled(false);
                binding.btnLogin.setEnabled(false);
                binding.ivMore.setEnabled(false);

                String userName = binding.edTxtUserName.getText().toString();
                String password = binding.edTxtPassword.getText().toString();

                UserResult.UserParameter parameter = new UserResult().new UserParameter(userName, password);
                login(parameter);
            }
        });

        binding.spinner.setOnItemSelectedListener((view, position, id, item) -> centerName = (String) item);
    }

    private void setupObserver() {
        viewModel.getLoginResultSingleLiveEvent().observe(getViewLifecycleOwner(), userResult -> {
            if (userResult != null) {
                if (userResult.getErrorCode().equals("0")) {
                    SipMobileAppPreferences.setUserLoginKey(getContext(), userResult.getUsers()[0].getUserLoginKey());
                    SipMobileAppPreferences.setCenterName(getContext(), centerName);
                    NavDirections action = LoginFragmentDirections.actionLoginFragmentToPatientFragment();
                    NavHostFragment.findNavController(this).navigate(action);
                } else {
                    binding.progressBarLoading.setVisibility(View.GONE);
                    binding.edTxtUserName.setEnabled(true);
                    binding.edTxtPassword.setEnabled(true);
                    binding.btnLogin.setEnabled(true);
                    binding.ivMore.setEnabled(true);
                    handleError(userResult.getError());
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), msg -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.edTxtUserName.setEnabled(true);
            binding.edTxtPassword.setEnabled(true);
            binding.btnLogin.setEnabled(true);
            binding.ivMore.setEnabled(true);
            handleError(msg);
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), msg -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.edTxtUserName.setEnabled(true);
            binding.edTxtPassword.setEnabled(true);
            binding.btnLogin.setEnabled(true);
            binding.ivMore.setEnabled(true);
            handleError(msg);
        });

        viewModel.getWrongIpAddressSingleLiveEvent().observe(getViewLifecycleOwner(), msg -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.edTxtUserName.setEnabled(true);
            binding.edTxtPassword.setEnabled(true);
            binding.btnLogin.setEnabled(true);
            binding.ivMore.setEnabled(true);
            handleError(msg);
        });

        viewModel.getServerDataListMutableLiveData().observe(getViewLifecycleOwner(), serverDataList -> {
            setupSpinner(serverDataList);
            if (serverDataList.size() == 0) {
                WarningDialogFragment fragment = WarningDialogFragment.newInstance(getString(R.string.required_ip));
                fragment.show(getParentFragmentManager(), WarningDialogFragment.TAG);
            }
        });
    }
}