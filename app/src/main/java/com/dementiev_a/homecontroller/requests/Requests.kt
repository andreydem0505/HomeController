package com.dementiev_a.homecontroller.requests

import com.dementiev_a.homecontroller.dto.NotifyRequest
import com.dementiev_a.homecontroller.dto.VerifyRequest
import com.dementiev_a.homecontroller.dto.VerifyResponse
import okhttp3.ResponseBody
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

        @POST("notify")
        fun notify(@Body body: NotifyRequest): Call<ResponseBody>
    }

    fun verifyKey(key: String): Response<VerifyResponse> {
        val service = retrofit.create(Service::class.java)
        return service.verify(VerifyRequest(key)).execute()
    }

    fun notify(key: String, sensor: String): Response<ResponseBody> {
        val service = retrofit.create(Service::class.java)
        return service.notify(NotifyRequest(key, sensor)).execute()
    }
}