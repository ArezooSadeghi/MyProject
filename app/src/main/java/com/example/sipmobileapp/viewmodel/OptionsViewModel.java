package com.example.sipmobileapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class OptionsViewModel extends AndroidViewModel {
    private SingleLiveEvent<Boolean> yesSignOutClicked = new SingleLiveEvent<>();

    public OptionsViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<Boolean> getYesSignOutClicked() {
        return yesSignOutClicked;
    }
}
