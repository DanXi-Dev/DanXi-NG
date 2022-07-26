package com.fduhole.danxinative.repository.opentreehole

import com.fduhole.danxinative.model.opentreehole.OTLoginInfo
import com.fduhole.danxinative.model.opentreehole.OTRegisterInfo
import com.fduhole.danxinative.model.opentreehole.OTVerifyCode
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface FDUHoleAuthApiService {
    @GET("verify/apikey")
    suspend fun getRegisterStatus(@Query("apikey") apiKey: String, @Query("email") email: String, @Query("check_register") checkRegister: Int = 1): Response

    @GET("verify/apikey")
    suspend fun getVerifyCode(@Query("apikey") apiKey: String, @Query("email") email: String): OTVerifyCode

    @GET("verify/email")
    suspend fun requestEmailVerifyCode(@Query("email") email: String): Response

    @POST("register")
    suspend fun register(@Body registerInfo: OTRegisterInfo)

    @POST("login")
    suspend fun login(@Body loginInfo: OTLoginInfo)

}