package com.app.dz.quranapp.fix_new_futers.ai_commands;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    public static final String BASE_URL = "https://futuresoftdz.me/";
    private static Retrofit sRetrofit;
    private static final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static final HttpLoggingInterceptor logger = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);


    public static Retrofit getInstance() {

        httpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original
                        .newBuilder()
                                .header("Authorization", WitAiRequest.BEARER_TOKEN)
                                .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.addNetworkInterceptor(logger).readTimeout(3, TimeUnit.MINUTES)
                .connectTimeout(3, TimeUnit.MINUTES)
                .build();

        if (sRetrofit == null) {
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(WitAiRequest.WIT_AI_API_URL1)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
        }
        return sRetrofit;
    }

    public RetrofitInstance() {
    }

}