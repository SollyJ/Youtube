package com.example.youtube.Service

import com.example.youtube.DTO.VideoDTO
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {
    @GET("/v3/e374b149-a65f-4890-97e1-b31c5c30401c")
    fun listVideos(): Call<VideoDTO>
}