package com.fduhole.danxinative.repository.opentreehole

import com.fduhole.danxinative.model.opentreehole.OTJWTToken
import com.fduhole.danxinative.model.opentreehole.OTLoginInfo
import com.fduhole.danxinative.model.opentreehole.OTRegisterInfo
import com.fduhole.danxinative.model.opentreehole.OTVerifyCode
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse


interface FDUHoleAuthApiService {
    @GET("verify/apikey")
    suspend fun getRegisterStatus(
        @Query("apikey") apiKey: String,
        @Query("email") email: String,
        @Query("check_register") checkRegister: Int = 1
    ): HttpResponse

    @GET("verify/apikey")
    suspend fun getVerifyCode(@Query("apikey") apiKey: String, @Query("email") email: String): OTVerifyCode

    @GET("verify/email")
    suspend fun requestEmailVerifyCode(@Query("email") email: String): HttpResponse

    @POST("register")
    suspend fun register(@Body registerInfo: OTRegisterInfo): OTJWTToken

    @POST("login")
    suspend fun login(@Body loginInfo: OTLoginInfo): OTJWTToken

}