package com.nativeknights.leetflow.data.remote

import com.nativeknights.leetflow.data.models.ContestResponse
import com.nativeknights.leetflow.data.models.GraphQLRequest
import com.nativeknights.leetflow.data.models.StatsResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface LeetCodeGraphQLService {

    @POST("graphql/")
    @Headers(
        "Content-Type: application/json",
        "Referer: https://leetcode.com",
        "Origin: https://leetcode.com"
    )
    suspend fun getUserStats(@Body request: GraphQLRequest): StatsResponse

    @POST("graphql/")
    @Headers(
        "Content-Type: application/json",
        "Referer: https://leetcode.com",
        "Origin: https://leetcode.com"
    )
    suspend fun getContestStats(@Body request: GraphQLRequest): ContestResponse
}

object LeetCodeClient {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: LeetCodeGraphQLService = Retrofit.Builder()
        .baseUrl("https://leetcode.com/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(LeetCodeGraphQLService::class.java)
}
