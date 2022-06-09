package com.example.youtube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.youtube.DTO.VideoDTO
import com.example.youtube.Service.VideoService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()   // FrameLayout에 Fragment를 attach
            .replace(R.id.frameContainer, PlayerFragment())
            .commit()

        getVideoList()
    }

    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).let { videoService ->
            videoService.listVideos()
                .enqueue(object: Callback<VideoDTO> {
                    override fun onResponse(call: Call<VideoDTO>, response: Response<VideoDTO>) {
                        if(response.isSuccessful.not()) {   // 예외처리
                            Log.d("MainActivity", "RESPONSE FAIL")
                            return
                        }

                        response.body()?.let {
                            Log.d("MainActivity", it.toString())
                        }
                    }

                    override fun onFailure(call: Call<VideoDTO>, t: Throwable) {   // todo 예외처리

                    }
                })
        }
    }
}