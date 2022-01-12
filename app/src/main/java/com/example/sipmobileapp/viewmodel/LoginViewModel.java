package com.example.sipmobileapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.model.UserResult;
import com.example.sipmobileapp.repository.SipMobileAppRepository;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {
    private SipMobileAppRepository repository;

    private SingleLiveEvent<Boolean> insertNotifyServerDataList = new SingleLiveEvent<>();

    private SingleLiveEvent<ServerData> editClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<ServerData> deleteClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> wrongIpAddressSingleLiveEvent;

    private SingleLiveEvent<UserResult> loginResultSingleLiveEvent;

    private LiveData<List<ServerData>> serverDataListMutableLiveData;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = SipMobileAppRepository.getInstance(getApplication());
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        wrongIpAddressSingleLiveEvent = repository.getWrongIpAddressSingleLiveEvent();
        loginResultSingleLiveEvent = repository.getLoginResultSingleLiveEvent();
        serverDataListMutableLiveData = repository.getServerDataListMutableLiveData();
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

    public LiveData<List<ServerData>> getServerDataListMutableLiveData() {
        return serverDataListMutableLiveData;
    }

    public void insert(ServerData serverData) {
        repository.insert(serverData);
    }

    public void delete(String centerName) {
        repository.delete(centerName);
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
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

    public void login(String path, UserResult.UserParameter userParameter) {
        repository.login(path, userParameter);
    }

    public void getUserLoginService(String newBaseUrl) {
        repository.getServiceUserResult(newBaseUrl);
    }
}
