package com.example.sipmobileapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sipmobileapp.model.ServerDataTwo;
import com.example.sipmobileapp.model.UserResult;
import com.example.sipmobileapp.repository.SipMobileAppRepository;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {
    private SipMobileAppRepository repository;

    private SingleLiveEvent<Boolean> insertNotifyServerDataList = new SingleLiveEvent<>();

    private SingleLiveEvent<ServerDataTwo> editClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<ServerDataTwo> deleteClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> wrongIpAddressSingleLiveEvent;

    private SingleLiveEvent<UserResult> loginResultSingleLiveEvent;

    private LiveData<List<ServerDataTwo>> serverDataListMutableLiveData;

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

    public SingleLiveEvent<ServerDataTwo> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<ServerDataTwo> getDeleteClicked() {
        return deleteClicked;
    }

    public LiveData<List<ServerDataTwo>> getServerDataListMutableLiveData() {
        return serverDataListMutableLiveData;
    }

    public void insert(ServerDataTwo serverDataTwo) {
        repository.insert(serverDataTwo);
    }

    public void delete(String centerName) {
        repository.delete(centerName);
    }

    public ServerDataTwo getServerData(String centerName) {
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
