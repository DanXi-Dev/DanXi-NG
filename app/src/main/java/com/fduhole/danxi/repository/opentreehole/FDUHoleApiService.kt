package com.fduhole.danxi.repository.opentreehole

import com.fduhole.danxi.model.opentreehole.OTDivision
import com.fduhole.danxi.model.opentreehole.OTFloor
import com.fduhole.danxi.model.opentreehole.OTHole
import com.fduhole.danxi.model.opentreehole.OTNewHole
import com.fduhole.danxi.model.opentreehole.OTTag
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query


interface FDUHoleApiService {

    @GET("divisions")
    suspend fun getDivisions(): List<OTDivision>


    @GET("divisions/{division_id}/holes")
    suspend fun getHoles(
        @Path("division_id") divisionId: Int,
        @Query("offset") offsetOrId: String?,
        @Query("size") size: Int?,
    ): List<OTHole>

    @GET("holes/{id}")
    suspend fun getHole(
        @Path("id") holeId: Int,
    ): OTHole

    @GET("holes/{hole_id}/floors")
    suspend fun getFloors(
        @Path("hole_id") holeId: Int,
        @Query("offset") offset: Int?,
        @Query("order_by") orderBy: String?,
        @Query("size") size: Int?,
        @Query("sort") sort: String?,
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