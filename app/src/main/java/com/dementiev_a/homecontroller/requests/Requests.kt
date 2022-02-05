package com.dementiev_a.homecontroller.requests

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


object Requests {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://home-controller-api.herokuapp.com/")
        .build()

    private interface Service {
        @POST("verify")
        fun verify(@Body body: VerifyRequest): Call<VerifyResponse>
    }

    fun verifyKey(key: String): Response<VerifyResponse> {
        val service = retrofit.create(Service::class.java)
        return service.verify(VerifyRequest(key)).execute()
    }
}