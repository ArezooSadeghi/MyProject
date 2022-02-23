package com.example.sipmobileapp.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentOptionsBinding;
import com.example.sipmobileapp.ui.dialog.SignOutQuestionDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.OptionsViewModel;

public class OptionsFragment extends Fragment {
    private FragmentOptionsBinding binding;
    private OptionsViewModel viewModel;
    private int topBarIconSize, bottomBarIconSize, iconSize;

    public static OptionsFragment newInstance() {
        OptionsFragment fragment = new OptionsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_options,
                container,
                false);

        handleEvents();

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            topBarIconSize = getScreenWidth() / 18;
            bottomBarIconSize = getScreenWidth() / 24;
            iconSize = getScreenWidth() / 6;
        } else {
            topBarIconSize = getScreenWidth() / 27;
            bottomBarIconSize = getScreenWidth() / 30;
            iconSize = getScreenWidth() / 10;
        }

        binding.btnSetting.setIconSize(topBarIconSize);
        binding.btnTalk.setIconSize(topBarIconSize);
        binding.btnPhone.setIconSize(topBarIconSize);
        binding.btnHelp.setIconSize(topBarIconSize);
        binding.btnCoin.setIconSize(topBarIconSize);
        binding.btnColor.setIconSize(topBarIconSize);
        binding.btnLock.setIconSize(topBarIconSize);
        binding.btnFile.setIconSize(topBarIconSize);
        binding.btnClose.setIconSize(topBarIconSize);

        binding.btnUser.setIconSize(bottomBarIconSize);
        binding.ivClock.setIconSize(bottomBarIconSize);

        binding.btnSecretory.setIconSize(iconSize);
        binding.btnDoctor.setIconSize(iconSize);
        binding.btnProfessional.setIconSize(iconSize);
        binding.btnInsuranceList.setIconSize(iconSize);
        binding.btnChangePassword.setIconSize(iconSize);
        binding.btnAnyDesk.setIconSize(iconSize);
        binding.btnPhoneBook.setIconSize(iconSize);
        binding.btnStore.setIconSize(iconSize);
        binding.btnDownload.setIconSize(iconSize);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(OptionsViewModel.class);
    }

    private void handleEvents() {
        binding.btnClose.setOnClickListener(v -> {
            SignOutQuestionDialogFragment fragment = SignOutQuestionDialogFragment.newInstance("آیا می خواهید از برنامه خارج شوید؟");
            fragment.show(getParentFragmentManager(), SignOutQuestionDialogFragment.TAG);
        });
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private void setupObserver() {
        viewModel.getYesSignOutClicked().observe(getViewLifecycleOwner(), yesSignOutClicked -> {
            SipMobileAppPreferences.setUserLoginKey(getContext(), null);
            NavDirections action = OptionsFragmentDirections.actionOptionsFragmentToLoginFragment();
            NavHostFragment.findNavController(this).navigate(action);
        });
    }
}