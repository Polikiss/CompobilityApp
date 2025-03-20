package itmo.polik.myapplication.network

import itmo.polik.myapplication.model.ApiResponse
import itmo.polik.myapplication.model.AstroRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AstroApiService {
    @POST("planets")
    suspend fun getPlanets(
        @Body request: AstroRequest
    ): ApiResponse
}