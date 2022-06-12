package com.example.youtube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.youtube.Adapter.VideoAdapter
import com.example.youtube.DTO.VideoDTO
import com.example.youtube.Service.VideoService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 영상 목록 RecyclerView 초기화
        videoAdapter = VideoAdapter()
        findViewById<RecyclerView>(R.id.mainRecyclerView).apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }

        supportFragmentManager.beginTransaction()   // FrameLayout에 Fragment를 attach
            .replace(R.id.frameContainer, PlayerFragment())
            .commit()

        getVideoList()
    }

    private fun getVideoList() {
        val retrofit = Retrofit.Builder()   // retrofit 객체 생성
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).let { videoService ->
            videoService.listVideos()
                .enqueue(object: Callback<VideoDTO> {   // http 요청에 대한 응답을 비동기방식(enqueue) 으로
                    override fun onResponse(call: Call<VideoDTO>, response: Response<VideoDTO>) {
                        if(response.isSuccessful.not()) {   // 예외처리
                            Log.d("MainActivity", "RESPONSE FAIL")
                            return
                        }

                        response.body()?.let {
                            Log.d("MainActivity", it.toString())
                            videoAdapter.submitList(it.videos)
                        }
                    }

                    override fun onFailure(call: Call<VideoDTO>, t: Throwable) {
                        Log.d("MainActivity", "FAIL")
                    }
                })
        }
    }
}