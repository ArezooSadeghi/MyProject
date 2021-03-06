package com.example.sipmobileapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.adapter.PatientAdapter;
import com.example.sipmobileapp.databinding.FragmentPatientBinding;
import com.example.sipmobileapp.model.PatientResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.ui.dialog.ErrorDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.PatientViewModel;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PatientFragment extends Fragment {
    private FragmentPatientBinding binding;
    private PatientViewModel viewModel;
    private ServerData serverData;
    private String userLoginKey;
    private List<PatientResult.PatientInfo> patientInfoList;

    public static PatientFragment newInstance() {
        PatientFragment fragment = new PatientFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
        initVariables();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_patient,
                container,
                false);

        initViews();
        handleEvents();

        if (patientInfoList.size() != 0) {
            binding.recyclerViewPatient.setVisibility(View.VISIBLE);
            binding.progressBarLoading.setVisibility(View.GONE);
            setupAdapter(patientInfoList.toArray(new PatientResult.PatientInfo[patientInfoList.size()]));
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(PatientViewModel.class);
    }

    private void initVariables() {
        String centerName = SipMobileAppPreferences.getCenterName(getContext());
        serverData = viewModel.getServerData(centerName);
        userLoginKey = SipMobileAppPreferences.getUserLoginKey(getContext());
        patientInfoList = new ArrayList<>();
    }

    private void initViews() {
        binding.recyclerViewPatient.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireContext(), R.drawable.custom_divider_recycler_view)));
        binding.recyclerViewPatient.addItemDecoration(dividerItemDecoration);
    }

    private void handleEvents() {
        binding.ivMore.setOnClickListener(view -> {
            PowerMenu powerMenu = new PowerMenu.Builder(requireContext())
                    .addItem(new PowerMenuItem(getString(R.string.logout_item_title)))
                    .addItem(new PowerMenuItem(getString(R.string.scan_item_title)))
                    .build();

            powerMenu.setOnMenuItemClickListener((position, item) -> {
                if (position == 0) {
                    SipMobileAppPreferences.setUserLoginKey(getContext(), null);
                    NavDirections action = PatientFragmentDirections.actionPatientFragmentToLoginFragment();
                    NavHostFragment.findNavController(this).navigate(action);
                } else if (position == 1) {
                    NavDirections action = PatientFragmentDirections.actionPatientFragmentToScanFragment();
                    NavHostFragment.findNavController(this).navigate(action);
                }
                powerMenu.dismiss();
            });
            powerMenu.showAsDropDown(view);
        });

        binding.btnSearch.setOnClickListener(view -> {
            if (binding.edTxtSearch.getText().toString().isEmpty()) {
                handleError(getString(R.string.search_patient_name));
            } else {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                binding.txtNoSearchResult.setVisibility(View.GONE);
                fetchPatients(binding.edTxtSearch.getText().toString());
            }
        });

        binding.edTxtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                binding.txtNoSearchResult.setVisibility(View.GONE);
                fetchPatients(binding.edTxtSearch.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void fetchPatients(String query) {
        viewModel.getServicePatientResult(serverData.getIp() + ":" + serverData.getPort());
        String path = "/api/v1/patients/search/";
        viewModel.fetchPatients(path, userLoginKey, query);
    }

    private void setupObserver() {
        viewModel.getPatientsResultSingleLiveEvent().observe(getViewLifecycleOwner(), patientResult -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            if (patientResult != null) {
                if (patientResult.getErrorCode().equals("0")) {
                    if (patientResult.getPatients().length == 0) {
                        binding.txtNoSearchResult.setVisibility(View.VISIBLE);
                        binding.recyclerViewPatient.setVisibility(View.GONE);
                    } else {
                        binding.txtNoSearchResult.setVisibility(View.GONE);
                        binding.recyclerViewPatient.setVisibility(View.VISIBLE);
                        setupAdapter(patientResult.getPatients());
                        patientInfoList = Arrays.asList(patientResult.getPatients());
                    }
                } else {
                    handleError(patientResult.getError());
                }
            }
        });

        viewModel.getAttachmentsItemClicked().observe(getViewLifecycleOwner(), sickID -> {
            PatientFragmentDirections.ActionPatientFragmentToPhotoGalleryFragment action = PatientFragmentDirections.actionPatientFragmentToPhotoGalleryFragment();
            action.setSickID(sickID);
            NavHostFragment.findNavController(this).navigate(action);
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), msg -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.recyclerViewPatient.setVisibility(View.GONE);
            binding.txtNoSearchResult.setVisibility(View.VISIBLE);
            handleError(msg);
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), msg -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.recyclerViewPatient.setVisibility(View.GONE);
            binding.txtNoSearchResult.setVisibility(View.VISIBLE);
            handleError(msg);
        });

        viewModel.getItemClicked().observe(getViewLifecycleOwner(), sickID -> {
            PatientFragmentDirections.ActionPatientFragmentToPhotoGalleryFragment action = PatientFragmentDirections.actionPatientFragmentToPhotoGalleryFragment();
            action.setSickID(sickID);
            NavHostFragment.findNavController(this).navigate(action);
        });
    }

    private void setupAdapter(PatientResult.PatientInfo[] patients) {
        List<PatientResult.PatientInfo> patientInfoList = Arrays.asList(patients);
        PatientAdapter adapter = new PatientAdapter(viewModel, patientInfoList);
        binding.recyclerViewPatient.setAdapter(adapter);
    }

    private void handleError(String msg) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(msg);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }
}