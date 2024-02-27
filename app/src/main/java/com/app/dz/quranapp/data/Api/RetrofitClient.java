package com.app.dz.quranapp.data.Api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

//    private static final String AUTH = "Basic " + Base64.encodeToString(("belalkhan:123456").getBytes(),Base64.NO_WRAP);
    private static final String TOKEN = "SqD712P3E82xnwOAEOkGd5JZH8s9wRR24TqNFzjk";
    public static final String BASE_URL = "https://api.sunnah.com/v1/";
    public static final String TIME_BASE_URL = "http://api.aladhan.com/v1/";

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient(String baseUrl) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        chain -> {
                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder()
                                    .addHeader("x-api-key",TOKEN)
                                    .method(original.method(), original.body());

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                ).build();

        retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).
                addCallAdapterFactory(RxJava2CallAdapterFactory.create()).
                client(okHttpClient).
                build();
    }


    public static synchronized RetrofitClient getInstance(String baseUrl) {
        if (mInstance == null) {
            mInstance = new RetrofitClient(baseUrl);
        }
        return mInstance;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }
}
