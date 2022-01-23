package com.example.sipmobileapp.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    public static String BASE_URL = "";

    public static Retrofit getRI(Type type, Object typeAdapter, Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ConnectivityInterceptor(context))
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cache(null)
                .build();

        return new Retrofit.Builder()
                .baseUrl("http://" + BASE_URL)
                .addConverterFactory(createConverter(type, typeAdapter))
                .client(client)
                .build();
    }

    public static Converter.Factory createConverter(Type type, Object typeAdapter) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, typeAdapter);
        Gson gson = gsonBuilder.create();
        return GsonConverterFactory.create(gson);
    }

    public static void getNewBaseUrl(String newBaseUrl) {
        BASE_URL = newBaseUrl;
    }
}
