package com.example.exchangerates.service;

import com.example.exchangerates.entity.ExchangeRateData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JSONPlaceHolderApi {

         //   /posts/{id}
    @GET("exchange_rates?json")
    public Call<ExchangeRateData> getPost(@Query("date") String date);


}
