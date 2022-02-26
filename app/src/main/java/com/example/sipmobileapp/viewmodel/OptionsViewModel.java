package com.example.sipmobileapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.repository.SipMobileAppRepository;

import java.util.List;

public class OptionsViewModel extends AndroidViewModel {
    private SipMobileAppRepository repository;
    private LiveData<List<ServerData>> serverDataListMutableLiveData;
    private SingleLiveEvent<Boolean> yesSignOutClicked = new SingleLiveEvent<>();

    public OptionsViewModel(@NonNull Application application) {
        super(application);
        repository = SipMobileAppRepository.getInstance(getApplication());
        serverDataListMutableLiveData = repository.getServerDataListMutableLiveData();
    }

    public LiveData<List<ServerData>> getServerDataListMutableLiveData() {
        return serverDataListMutableLiveData;
    }

    public SingleLiveEvent<Boolean> getYesSignOutClicked() {
        return yesSignOutClicked;
    }
}
