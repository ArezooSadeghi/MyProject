package com.example.sipmobileapp.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.BaseLayoutBinding;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.ui.dialog.SignOutQuestionDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.OptionsViewModel;

import java.util.ArrayList;
import java.util.List;

public class OptionsFragment extends Fragment {
    private BaseLayoutBinding binding;
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
                R.layout.base_layout,
                container,
                false);

        handleEvents();

        int orientation = getOrientation();
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            int screenWidth = getScreenWidth();
            topBarIconSize = screenWidth / 18;
            bottomBarIconSize = screenWidth / 24;
            iconSize = screenWidth / 6;

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.btnClock.getLayoutParams();
            layoutParams.width = screenWidth / 9;
            layoutParams.height = screenWidth / 9;
            binding.btnClock.setLayoutParams(layoutParams);

        } else {
            int screenHeight = getScreenHeight();
            topBarIconSize = screenHeight / 18;
            bottomBarIconSize = screenHeight / 20;
            iconSize = screenHeight / 6;

            int height = screenHeight / 9;
            ViewGroup.LayoutParams layoutParamsContainerOne = binding.containerOne.getLayoutParams();
            layoutParamsContainerOne.height = height;
            binding.containerOne.setLayoutParams(layoutParamsContainerOne);

            ViewGroup.LayoutParams layoutParamsContainerThree = binding.containerThree.getLayoutParams();
            layoutParamsContainerThree.height = height;
            binding.containerThree.setLayoutParams(layoutParamsContainerThree);

            ViewGroup.LayoutParams layoutParamsContainerTwo = binding.spinner.getLayoutParams();

            float totalSize = (2 * height + (layoutParamsContainerTwo.height) + 20);
            float remainingSize = screenHeight - totalSize;

            ViewGroup.LayoutParams layoutParamsInclude = binding.includeOptions.containerMain.getLayoutParams();
            layoutParamsInclude.height = (int) (remainingSize);
            binding.includeOptions.containerMain.setLayoutParams(layoutParamsInclude);

            ViewGroup.LayoutParams layoutParamsInnerContainerOne = binding.includeOptions.innerContainerOne.getLayoutParams();
            layoutParamsInnerContainerOne.height = (int) ((remainingSize) / 2);
            binding.includeOptions.innerContainerOne.setLayoutParams(layoutParamsInnerContainerOne);

            ViewGroup.LayoutParams layoutParamsInnerContainerTwo = binding.includeOptions.innerContainerTwo.getLayoutParams();
            layoutParamsInnerContainerTwo.height = (int) ((remainingSize) / 2);
            binding.includeOptions.innerContainerTwo.setLayoutParams(layoutParamsInnerContainerTwo);

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.btnClock.getLayoutParams();
            layoutParams.width = height;
            layoutParams.height = height;
            binding.btnClock.setLayoutParams(layoutParams);
        }

        binding.btnClose.setIconSize(topBarIconSize);
        binding.btnFile.setIconSize(topBarIconSize);
        binding.btnLock.setIconSize(topBarIconSize);
        binding.btnColor.setIconSize(topBarIconSize);
        binding.btnCoin.setIconSize(topBarIconSize);
        binding.btnHelp.setIconSize(topBarIconSize);
        binding.btnPhone.setIconSize(topBarIconSize);
        binding.btnTalk.setIconSize(topBarIconSize);
        binding.btnSetting.setIconSize(topBarIconSize);

        binding.btnUser.setIconSize(bottomBarIconSize);
        binding.btnClock.setIconSize(bottomBarIconSize);

        binding.includeOptions.btnSecretory.setIconSize(iconSize);
        binding.includeOptions.btnDoctor.setIconSize(iconSize);
        binding.includeOptions.btnProfessional.setIconSize(iconSize);
        binding.includeOptions.btnInsuranceList.setIconSize(iconSize);
        binding.includeOptions.btnChangePassword.setIconSize(iconSize);
        binding.includeOptions.btnAnyDesk.setIconSize(iconSize);
        binding.includeOptions.btnPhoneBook.setIconSize(iconSize);
        binding.includeOptions.btnStore.setIconSize(iconSize);
        binding.includeOptions.btnDownload.setIconSize(iconSize);

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

    private void setupSpinner(List<ServerData> serverDataList) {
        List<String> centerNames = new ArrayList<>();
        for (ServerData serverData : serverDataList) {
            centerNames.add(serverData.getCenterName());
        }
        binding.spinner.setItems(centerNames);
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

    private int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private int getOrientation() {
        return getResources().getConfiguration().orientation;
    }

    private void setupObserver() {
        viewModel.getYesSignOutClicked().observe(getViewLifecycleOwner(), yesSignOutClicked -> {
            SipMobileAppPreferences.setUserLoginKey(getContext(), null);
            NavDirections action = OptionsFragmentDirections.actionOptionsFragmentToLoginFragment();
            NavHostFragment.findNavController(this).navigate(action);
        });

        viewModel.getServerDataListMutableLiveData().observe(getViewLifecycleOwner(), serverDataList -> {
            setupSpinner(serverDataList);
        });
    }
}