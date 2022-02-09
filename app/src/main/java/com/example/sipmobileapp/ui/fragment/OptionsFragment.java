package com.example.sipmobileapp.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.RootLayoutBinding;

public class OptionsFragment extends Fragment {
    private RootLayoutBinding binding;

    public static OptionsFragment newInstance() {
        OptionsFragment fragment = new OptionsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.root_layout,
                container,
                false);

        if (isTablet()) {
            binding.btnClose.setIconSize(64);
            binding.btnFile.setIconSize(64);
            binding.btnLock.setIconSize(64);
            binding.btnColor.setIconSize(64);
            binding.btnCoin.setIconSize(64);
            binding.btnHelp.setIconSize(64);
            binding.btnPhone.setIconSize(64);
            binding.btnTalk.setIconSize(64);
            binding.btnSetting.setIconSize(64);

            binding.btnDate.setIconSize(32);
            binding.btnDate.setTextSize(12);
            binding.btnUserName.setIconSize(32);
            binding.btnUserName.setTextSize(12);
            binding.btnLanguage.setIconSize(32);
            binding.btnLanguage.setTextSize(12);

            binding.my.btnProfessional.setIconSize(144);
            binding.my.btnProfessional.setTextSize(18);
            binding.my.btnDoctor.setIconSize(144);
            binding.my.btnDoctor.setTextSize(18);
            binding.my.btnSecretory.setIconSize(144);
            binding.my.btnSecretory.setTextSize(18);
            binding.my.btnAnyDesk.setIconSize(144);
            binding.my.btnAnyDesk.setTextSize(18);
            binding.my.btnChangePassword.setIconSize(144);
            binding.my.btnChangePassword.setTextSize(18);
            binding.my.btnInsuranceList.setIconSize(144);
            binding.my.btnInsuranceList.setTextSize(18);
            binding.my.btnDownload.setIconSize(144);
            binding.my.btnDownload.setTextSize(18);
            binding.my.btnStore.setIconSize(144);
            binding.my.btnStore.setTextSize(18);
            binding.my.btnPhoneBook.setIconSize(144);
            binding.my.btnPhoneBook.setTextSize(18);
        } else {
            binding.btnClose.setIconSize(54);
            binding.btnFile.setIconSize(54);
            binding.btnLock.setIconSize(54);
            binding.btnColor.setIconSize(54);
            binding.btnCoin.setIconSize(54);
            binding.btnHelp.setIconSize(54);
            binding.btnPhone.setIconSize(54);
            binding.btnTalk.setIconSize(54);
            binding.btnSetting.setIconSize(54);

            binding.btnDate.setIconSize(32);
            binding.btnDate.setTextSize(10);
            binding.btnUserName.setIconSize(32);
            binding.btnUserName.setTextSize(10);
            binding.btnLanguage.setIconSize(32);
            binding.btnLanguage.setTextSize(10);

            binding.my.btnProfessional.setIconSize(128);
            binding.my.btnProfessional.setTextSize(12);
            binding.my.btnDoctor.setIconSize(128);
            binding.my.btnDoctor.setTextSize(12);
            binding.my.btnSecretory.setIconSize(128);
            binding.my.btnSecretory.setTextSize(12);
            binding.my.btnAnyDesk.setIconSize(128);
            binding.my.btnAnyDesk.setTextSize(12);
            binding.my.btnChangePassword.setIconSize(128);
            binding.my.btnChangePassword.setTextSize(12);
            binding.my.btnInsuranceList.setIconSize(128);
            binding.my.btnInsuranceList.setTextSize(12);
            binding.my.btnDownload.setIconSize(128);
            binding.my.btnDownload.setTextSize(12);
            binding.my.btnStore.setIconSize(128);
            binding.my.btnStore.setTextSize(12);
            binding.my.btnPhoneBook.setIconSize(128);
            binding.my.btnPhoneBook.setTextSize(12);
        }

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!isTablet()) {
                binding.my.btnProfessional.setIconSize(115);
                binding.my.btnDoctor.setIconSize(115);
                binding.my.btnSecretory.setIconSize(115);
                binding.my.btnAnyDesk.setIconSize(115);
                binding.my.btnChangePassword.setIconSize(115);
                binding.my.btnInsuranceList.setIconSize(115);
                binding.my.btnDownload.setIconSize(115);
                binding.my.btnStore.setIconSize(115);
                binding.my.btnPhoneBook.setIconSize(115);
            }

        } else {
            // In portrait
        }




        return binding.getRoot();
    }

    public boolean isTablet() {
        boolean xlarge = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }
}