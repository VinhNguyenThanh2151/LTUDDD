package com.example.run_core.data.remote

import com.example.run_core.data.local.DirectionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface DirectionsApiService {
    @GET
    fun getDirections(@Url url: String): Call<DirectionsResponse>
}