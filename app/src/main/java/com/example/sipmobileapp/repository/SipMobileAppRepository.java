package com.example.sipmobileapp.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.PatientResult;
import com.example.sipmobileapp.model.ServerDataTwo;
import com.example.sipmobileapp.model.UserResult;
import com.example.sipmobileapp.retrofit.AttachResultDeserializer;
import com.example.sipmobileapp.retrofit.NoConnectivityException;
import com.example.sipmobileapp.retrofit.PatientResultDeserializer;
import com.example.sipmobileapp.retrofit.RetrofitInstance;
import com.example.sipmobileapp.retrofit.SipMobileAppService;
import com.example.sipmobileapp.retrofit.UserResultDeserializer;
import com.example.sipmobileapp.room.ServerDataTwoDao;
import com.example.sipmobileapp.room.ServerDataTwoRoomDatabase;
import com.example.sipmobileapp.viewmodel.SingleLiveEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SipMobileAppRepository {
    @SuppressLint("StaticFieldLeak")
    public static SipMobileAppRepository sInstance;
    private Context context;
    private SipMobileAppService sipMobileAppService;


    private ServerDataTwoDao serverDataTwoDao;
    private LiveData<List<ServerDataTwo>> serverDataListMutableLiveData;

    public static final String TAG = SipMobileAppRepository.class.getSimpleName();

    private SingleLiveEvent<UserResult> loginResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<PatientResult> patientsResultSingleLiveEvent = new SingleLiveEvent<>();
    private MutableLiveData<AttachResult> patientAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<AttachResult> attachInfoResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<AttachResult> attachResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<AttachResult> deleteAttachResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> wrongIpAddressSingleLiveEvent = new SingleLiveEvent<>();

    private SipMobileAppRepository(Context context) {
        this.context = context;
        ServerDataTwoRoomDatabase db = ServerDataTwoRoomDatabase.getDatabase(context);
        serverDataTwoDao = db.serverDataTwoDao();
        serverDataListMutableLiveData = serverDataTwoDao.getServerDataList();
    }

    public static SipMobileAppRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SipMobileAppRepository(context.getApplicationContext());
        }
        return sInstance;
    }

    public void getServiceUserResult(String newBaseUrl) {
        RetrofitInstance.getNewBaseUrl(newBaseUrl);
        sipMobileAppService = RetrofitInstance.getRI(new TypeToken<UserResult>() {
        }.getType(), new UserResultDeserializer(), context).create(SipMobileAppService.class);
    }

    public void getServicePatientResult(String newBaseUrl) {
        RetrofitInstance.getNewBaseUrl(newBaseUrl);
        sipMobileAppService = RetrofitInstance.getRI(new TypeToken<PatientResult>() {
        }.getType(), new PatientResultDeserializer(), context).create(SipMobileAppService.class);
    }

    public void getServiceAttachResult(String newBaseUrl) {
        RetrofitInstance.getNewBaseUrl(newBaseUrl);
        sipMobileAppService = RetrofitInstance.getRI(new TypeToken<AttachResult>() {
        }.getType(), new AttachResultDeserializer(), context).create(SipMobileAppService.class);
    }

    public SingleLiveEvent<UserResult> getLoginResultSingleLiveEvent() {
        return loginResultSingleLiveEvent;
    }

    public SingleLiveEvent<PatientResult> getPatientsResultSingleLiveEvent() {
        return patientsResultSingleLiveEvent;
    }

    public MutableLiveData<AttachResult> getPatientAttachmentsResultSingleLiveEvent() {
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

    public SingleLiveEvent<String> getWrongIpAddressSingleLiveEvent() {
        return wrongIpAddressSingleLiveEvent;
    }

    public LiveData<List<ServerDataTwo>> getServerDataListMutableLiveData() {
        return serverDataListMutableLiveData;
    }

    public void login(String path, UserResult.UserParameter userParameter) {
        sipMobileAppService.login(path, userParameter).enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(@NonNull Call<UserResult> call, @NonNull Response<UserResult> response) {
                if (response.isSuccessful()) {
                    loginResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        assert response.errorBody() != null;
                        UserResult userResult = gson.fromJson(response.errorBody().string(), UserResult.class);
                        loginResultSingleLiveEvent.setValue(userResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResult> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_msg));
                } else {
                    wrongIpAddressSingleLiveEvent.setValue(context.getResources().getString(R.string.no_exist_server_msg));
                }
            }
        });
    }

    public void fetchPatients(String path, String userLoginKey, String patientName) {
        sipMobileAppService.fetchPatients(path, userLoginKey, patientName).enqueue(new Callback<PatientResult>() {
            @Override
            public void onResponse(@NonNull Call<PatientResult> call, @NonNull Response<PatientResult> response) {
                if (response.isSuccessful()) {
                    patientsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        assert response.errorBody() != null;
                        PatientResult patientResult = gson.fromJson(response.errorBody().string(), PatientResult.class);
                        patientsResultSingleLiveEvent.setValue(patientResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientResult> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_msg));
                } else {
                    Log.e(TAG, t.getMessage());
                }
            }
        });
    }

    public void fetchPatientAttachments(String path, String userLoginKey, int sickID) {
        sipMobileAppService.fetchPatientAttachments(path, userLoginKey, sickID).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(@NonNull Call<AttachResult> call, @NonNull Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    patientAttachmentsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        assert response.errorBody() != null;
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        patientAttachmentsResultSingleLiveEvent.setValue(attachResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AttachResult> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_msg));
                } else {
                    Log.e(TAG, t.getMessage());
                }
            }
        });
    }

    public void fetchAttachInfo(String path, String userLoginKey, int attachID) {
        sipMobileAppService.fetchAttachInfo(path, userLoginKey, attachID).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(@NonNull Call<AttachResult> call, @NonNull Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    attachInfoResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        assert response.errorBody() != null;
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        attachInfoResultSingleLiveEvent.setValue(attachResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AttachResult> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_msg));
                } else {
                    Log.e(TAG, t.getMessage());
                }
            }
        });
    }

    public void attach(String path, String userLoginKey, AttachResult.AttachParameter attachParameter) {
        sipMobileAppService.attach(path, userLoginKey, attachParameter).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(@NonNull Call<AttachResult> call, @NonNull Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    attachResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        assert response.errorBody() != null;
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        attachResultSingleLiveEvent.setValue(attachResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AttachResult> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_msg));
                } else {
                    Log.e(TAG, t.getMessage());
                }
            }
        });
    }

    public void deleteAttach(String path, String userLoginKey, int attachID) {
        sipMobileAppService.deleteAttach(path, userLoginKey, attachID).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(@NonNull Call<AttachResult> call, @NonNull Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    deleteAttachResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        assert response.errorBody() != null;
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        deleteAttachResultSingleLiveEvent.setValue(attachResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AttachResult> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_msg));
                } else {
                    Log.e(TAG, t.getMessage());
                }
            }
        });
    }

    public void insert(ServerDataTwo serverDataTwo) {
        new insertAsyncTask(serverDataTwoDao).execute(serverDataTwo);
    }

    private static class insertAsyncTask extends AsyncTask<ServerDataTwo, Void, Void> {

        private ServerDataTwoDao mAsyncTaskDao;

        insertAsyncTask(ServerDataTwoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ServerDataTwo... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public void delete(String centerName) {
        new deleteAsyncTask(serverDataTwoDao).execute(centerName);
    }

    private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {

        private ServerDataTwoDao mAsyncTaskDao;

        deleteAsyncTask(ServerDataTwoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    public ServerDataTwo getServerData(String centerName) {

        try {
            return new getAsyncTask(serverDataTwoDao).execute(centerName).get();
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private static class getAsyncTask extends AsyncTask<String, Void, ServerDataTwo> {

        private ServerDataTwoDao mAsyncTaskDao;

        getAsyncTask(ServerDataTwoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected ServerDataTwo doInBackground(final String... params) {
            return mAsyncTaskDao.getServerData(params[0]);
        }

        @Override
        protected void onPostExecute(ServerDataTwo serverDataTwo) {
            super.onPostExecute(serverDataTwo);
        }
    }
}
