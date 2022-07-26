package com.fduhole.danxinative.repository.opentreehole

import com.fduhole.danxinative.model.opentreehole.*
import retrofit2.http.*


interface FDUHoleApiService {

    @GET("divisions")
    suspend fun getDivisions(): List<OTDivision>

    @PUT("divisions/{id}")
    suspend fun getDivision(@Path("id") id: Int?): OTDivision

    @GET("divisions/{division_id}/holes")
    suspend fun getHoles(
        @Path("division_id") divisionId: Int, @Query("offset") offsetOrId: String?, @Query("size") size: Int?,
    ): List<OTHole>

    @GET("holes/{id}")
    suspend fun getHole(
        @Path("id") holdId: Int,
    ): OTHole

    @GET("holes/{hole_id}/floors")
    suspend fun getFloors(
        @Path("hole_id") holeId: Int, @Query("offset") offset: Int?, @Query("order_by") orderBy: String?, @Query("size") size: Int?, @Query("sort") sort: String?,
    ): List<OTFloor>

    @GET("floors/{id}")
    suspend fun getFloor(
        @Path("id") id: Int,
    ): OTFloor

    @GET("tags")
    suspend fun getTags(): List<OTTag>

    @POST("divisions/{division_id}/holes")
    suspend fun postHole(
        @Path("division_id") divisionId: Int, @Body json: OTNewHole,
    ): OTHole

}