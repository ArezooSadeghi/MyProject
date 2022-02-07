package com.example.sipmobileapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipmobileapp.model.PatientResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.repository.SipMobileAppRepository;

public class ScanViewModel extends AndroidViewModel {
    private SipMobileAppRepository repository;
    private SingleLiveEvent<Boolean> closeClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<PatientResult> patientsResultSingleLiveEvent;

    public ScanViewModel(@NonNull Application application) {
        super(application);
        repository = SipMobileAppRepository.getInstance(getApplication());
        patientsResultSingleLiveEvent = repository.getPatientsResultSingleLiveEvent();
    }

    public SingleLiveEvent<PatientResult> getPatientsResultSingleLiveEvent() {
        return patientsResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getCloseClicked() {
        return closeClicked;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getServicePatientResult(String newBaseUrl) {
        repository.getServicePatientResult(newBaseUrl);
    }

    public void fetchPatients(String path, String userLoginKey, String patientName) {
        repository.fetchPatients(path, userLoginKey, patientName);
    }
}
