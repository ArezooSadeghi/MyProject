package com.example.sipmobileapp.retrofit;

import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.PatientResult;
import com.example.sipmobileapp.model.UserResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SipMobileAppService {

    @POST("{path}")
    Call<UserResult> login(@Path("path") String path, @Body UserResult.UserParameter userParameter);

    @GET("{path}")
    Call<PatientResult> fetchPatients(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("patientName") String patientName);

    @GET("{path}")
    Call<AttachResult> fetchPatientAttachments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("sickID") int sickID);

    @GET("{path}")
    Call<AttachResult> fetchAttachInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("attachID") int attachID);

    @POST("{path}")
    Call<AttachResult> attach(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body AttachResult.AttachParameter attachParameter);

    @DELETE("{path}")
    Call<AttachResult> deleteAttach(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("attachID") int attachID);
}
