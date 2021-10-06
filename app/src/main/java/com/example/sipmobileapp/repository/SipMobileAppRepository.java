package com.example.sipmobileapp.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.database.SipMobileAppDBHelper;
import com.example.sipmobileapp.database.SipMobileAppSchema;
import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.PatientResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.model.UserResult;
import com.example.sipmobileapp.retrofit.AttachResultDeserializer;
import com.example.sipmobileapp.retrofit.NoConnectivityException;
import com.example.sipmobileapp.retrofit.PatientResultDeserializer;
import com.example.sipmobileapp.retrofit.RetrofitInstance;
import com.example.sipmobileapp.retrofit.SipMobileAppService;
import com.example.sipmobileapp.retrofit.UserResultDeserializer;
import com.example.sipmobileapp.viewmodel.SingleLiveEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SipMobileAppRepository {
    public static SipMobileAppRepository sInstance;
    private Context context;
    private SQLiteDatabase mDatabase;
    private SipMobileAppService sipMobileAppService;

    public static final String TAG = SipMobileAppRepository.class.getSimpleName();

    private SingleLiveEvent<UserResult> loginResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<PatientResult> patientsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<AttachResult> patientAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<AttachResult> attachInfoResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<AttachResult> attachResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<AttachResult> deleteAttachResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> wrongIpAddressSingleLiveEvent = new SingleLiveEvent<>();

    private SipMobileAppRepository(Context context) {
        this.context = context;
        SipMobileAppDBHelper helper = new SipMobileAppDBHelper(context);
        mDatabase = helper.getWritableDatabase();
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

    public SingleLiveEvent<String> getWrongIpAddressSingleLiveEvent() {
        return wrongIpAddressSingleLiveEvent;
    }

    public void insertServerData(ServerData serverData) {
        ContentValues values = new ContentValues();

        values.put(SipMobileAppSchema.ServerDataTable.Cols.CENTER_NAME, serverData.getCenterName());
        values.put(SipMobileAppSchema.ServerDataTable.Cols.IP_ADDRESS, serverData.getIpAddress());
        values.put(SipMobileAppSchema.ServerDataTable.Cols.PORT, serverData.getPort());

        mDatabase.insert(SipMobileAppSchema.ServerDataTable.NAME, null, values);
    }

    public List<ServerData> getServerDataList() {
        List<ServerData> serverDataList = new ArrayList<>();
        Cursor cursor = mDatabase.query(
                SipMobileAppSchema.ServerDataTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor == null || cursor.getCount() == 0) {
            return serverDataList;
        }

        try {

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                String centerName = cursor.getString(cursor.getColumnIndex(SipMobileAppSchema.ServerDataTable.Cols.CENTER_NAME));
                String ipAddress = cursor.getString(cursor.getColumnIndex(SipMobileAppSchema.ServerDataTable.Cols.IP_ADDRESS));
                String port = cursor.getString(cursor.getColumnIndex(SipMobileAppSchema.ServerDataTable.Cols.PORT));

                ServerData serverData = new ServerData(centerName, ipAddress, port);

                serverDataList.add(serverData);

                cursor.moveToNext();
            }

        } finally {
            cursor.close();
        }
        return serverDataList;
    }

    public ServerData getServerData(String centerName) {
        ServerData serverData = new ServerData();
        String selection = "centerName=?";
        String[] selectionArgs = {centerName};
        Cursor cursor = mDatabase.query(
                SipMobileAppSchema.ServerDataTable.NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor == null || cursor.getCount() == 0) {
            return serverData;
        }

        try {

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                String ipAddress = cursor.getString(cursor.getColumnIndex(SipMobileAppSchema.ServerDataTable.Cols.IP_ADDRESS));
                String port = cursor.getString(cursor.getColumnIndex(SipMobileAppSchema.ServerDataTable.Cols.PORT));

                serverData.setCenterName(centerName);
                serverData.setIpAddress(ipAddress);
                serverData.setPort(port);

                cursor.moveToNext();
            }

        } finally {
            cursor.close();
        }
        return serverData;
    }

    public void deleteServerData(ServerData serverData) {
        String whereClause = "centerName=?";
        String whereArgs[] = {serverData.getCenterName()};
        mDatabase.delete(SipMobileAppSchema.ServerDataTable.NAME, whereClause, whereArgs);
    }

    public void login(String path, UserResult.UserParameter userParameter) {
        sipMobileAppService.login(path, userParameter).enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                if (response.isSuccessful()) {
                    loginResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        UserResult userResult = gson.fromJson(response.errorBody().string(), UserResult.class);
                        loginResultSingleLiveEvent.setValue(userResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    wrongIpAddressSingleLiveEvent.setValue(context.getResources().getString(R.string.no_exist_server_message));
                }
            }
        });
    }

    public void fetchPatients(String path, String userLoginKey, String patientName) {
        sipMobileAppService.fetchPatients(path, userLoginKey, patientName).enqueue(new Callback<PatientResult>() {
            @Override
            public void onResponse(Call<PatientResult> call, Response<PatientResult> response) {
                if (response.isSuccessful()) {
                    patientsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PatientResult patientResult = gson.fromJson(response.errorBody().string(), PatientResult.class);
                        patientsResultSingleLiveEvent.setValue(patientResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PatientResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage());
                }
            }
        });
    }

    public void fetchPatientAttachments(String path, String userLoginKey, int sickID) {
        sipMobileAppService.fetchPatientAttachments(path, userLoginKey, sickID).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    patientAttachmentsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        patientAttachmentsResultSingleLiveEvent.setValue(attachResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage());
                }
            }
        });
    }

    public void fetchAttachInfo(String path, String userLoginKey, int attachID) {
        sipMobileAppService.fetchAttachInfo(path, userLoginKey, attachID).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    attachInfoResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        attachInfoResultSingleLiveEvent.setValue(attachResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage());
                }
            }
        });
    }

    public void attach(String path, String userLoginKey, AttachResult.AttachParameter attachParameter) {
        sipMobileAppService.attach(path, userLoginKey, attachParameter).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    attachResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        attachResultSingleLiveEvent.setValue(attachResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage());
                }
            }
        });
    }

    public void deleteAttach(String path, String userLoginKey, int attachID) {
        sipMobileAppService.deleteAttach(path, userLoginKey, attachID).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    deleteAttachResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        deleteAttachResultSingleLiveEvent.setValue(attachResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage());
                }
            }
        });
    }
}
