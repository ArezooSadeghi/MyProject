package com.example.sipmobileapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.repository.SipMobileAppRepository;

public class AttachmentViewModel extends AndroidViewModel {
    private SipMobileAppRepository repository;
    private SingleLiveEvent<AttachResult> patientAttachmentsResultSingleLiveEvent;
    private SingleLiveEvent<AttachResult> attachInfoResultSingleLiveEvent;
    private SingleLiveEvent<AttachResult> attachResultSingleLiveEvent;
    private SingleLiveEvent<AttachResult> deleteAttachResultSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> doneWrite = new SingleLiveEvent<>();
    private SingleLiveEvent<String> photoClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> yesDeleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> showAttachAgainDialog = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> noAttachAgain = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> yesAttachAgain = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> closeClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> newDoneWrite = new SingleLiveEvent<>();

    public AttachmentViewModel(@NonNull Application application) {
        super(application);
        repository = SipMobileAppRepository.getInstance(getApplication());
        patientAttachmentsResultSingleLiveEvent = repository.getPatientAttachmentsResultSingleLiveEvent();
        attachInfoResultSingleLiveEvent = repository.getAttachInfoResultSingleLiveEvent();
        attachResultSingleLiveEvent = repository.getAttachResultSingleLiveEvent();
        deleteAttachResultSingleLiveEvent = repository.getDeleteAttachResultSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<AttachResult> getPatientAttachmentsResultSingleLiveEvent() {
        return patientAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachInfoResultSingleLiveEvent() {
        return attachInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultSingleLiveEvent() {
        return attachResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getDeleteAttachResultSingleLiveEvent() {
        return deleteAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDoneWrite() {
        return doneWrite;
    }

    public SingleLiveEvent<String> getPhotoClicked() {
        return photoClicked;
    }

    public SingleLiveEvent<Boolean> getYesDeleteClicked() {
        return yesDeleteClicked;
    }

    public SingleLiveEvent<Boolean> getShowAttachAgainDialog() {
        return showAttachAgainDialog;
    }

    public SingleLiveEvent<Boolean> getNoAttachAgain() {
        return noAttachAgain;
    }

    public SingleLiveEvent<Boolean> getYesAttachAgain() {
        return yesAttachAgain;
    }

    public SingleLiveEvent<Boolean> getSuccessDialogDismissed() {
        return closeClicked;
    }

    public SingleLiveEvent<Boolean> getNewDoneWrite() {
        return newDoneWrite;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getServiceAttachResult(String newBaseUrl) {
        repository.getServiceAttachResult(newBaseUrl);
    }

    public void fetchPatientAttachments(String path, String userLoginKey, int sickID) {
        repository.fetchPatientAttachments(path, userLoginKey, sickID);
    }

    public void fetchAttachInfo(String path, String userLoginKey, int attachID) {
        repository.fetchAttachInfo(path, userLoginKey, attachID);
    }

    public void attach(String path, String userLoginKey, AttachResult.AttachParameter attachParameter) {
        repository.attach(path, userLoginKey, attachParameter);
    }

    public void deleteAttach(String path, String userLoginKey, int attachID) {
        repository.deleteAttach(path, userLoginKey, attachID);
    }
}
