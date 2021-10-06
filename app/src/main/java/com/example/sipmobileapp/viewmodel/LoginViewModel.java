package com.example.sipmobileapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.model.UserResult;
import com.example.sipmobileapp.repository.SipMobileAppRepository;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {
    private SipMobileAppRepository mRepository;

    private SingleLiveEvent<Boolean> insertNotifySpinner = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> insertNotifyServerDataList = new SingleLiveEvent<>();

    private SingleLiveEvent<ServerData> editClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<ServerData> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> deleteNotifySpinner = new SingleLiveEvent<>();

    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> wrongIpAddressSingleLiveEvent;

    private SingleLiveEvent<UserResult> loginResultSingleLiveEvent;

    public LoginViewModel(@NonNull Application application) {
        super(application);

        mRepository = SipMobileAppRepository.getInstance(getApplication());

        noConnectionExceptionHappenSingleLiveEvent = mRepository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = mRepository.getTimeoutExceptionHappenSingleLiveEvent();
        wrongIpAddressSingleLiveEvent = mRepository.getWrongIpAddressSingleLiveEvent();

        loginResultSingleLiveEvent = mRepository.getLoginResultSingleLiveEvent();
    }

    public SingleLiveEvent<Boolean> getInsertNotifySpinner() {
        return insertNotifySpinner;
    }

    public SingleLiveEvent<Boolean> getInsertNotifyServerDataList() {
        return insertNotifyServerDataList;
    }

    public SingleLiveEvent<ServerData> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<ServerData> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<Boolean> getDeleteNotifySpinner() {
        return deleteNotifySpinner;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getWrongIpAddressSingleLiveEvent() {
        return wrongIpAddressSingleLiveEvent;
    }

    public SingleLiveEvent<UserResult> getLoginResultSingleLiveEvent() {
        return loginResultSingleLiveEvent;
    }

    public List<ServerData> getServerDataList() {
        return mRepository.getServerDataList();
    }

    public ServerData getServerData(String centerName) {
        return mRepository.getServerData(centerName);
    }

    public void insertServerData(ServerData serverData) {
        mRepository.insertServerData(serverData);
    }

    public void deleteServerData(ServerData serverData) {
        mRepository.deleteServerData(serverData);
    }

    public void login(String path, UserResult.UserParameter userParameter) {
        mRepository.login(path, userParameter);
    }

    public void getUserLoginService(String newBaseUrl) {
        mRepository.getServiceUserResult(newBaseUrl);
    }
}
