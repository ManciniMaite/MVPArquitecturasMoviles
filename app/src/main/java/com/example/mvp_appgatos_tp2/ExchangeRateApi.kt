package com.example.mvp_appgatos_tp2

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class ConversionResponse(
    val result: String,
    val conversion_rate: Double?,
    val conversion_result: Double?
)

interface ExchangeRateApi {
    @GET("v6/{api_key}/pair/{base}/{target}/{amount}")
    suspend fun convertCurrency(
        @Path("api_key") apiKey: String,
        @Path("base") base: String,
        @Path("target") target: String,
        @Path("amount") amount: Double
    ): Response<ConversionResponse>
}