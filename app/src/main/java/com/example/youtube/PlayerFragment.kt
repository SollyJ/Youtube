package com.example.youtube

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtube.Adapter.VideoAdapter
import com.example.youtube.DTO.VideoDTO
import com.example.youtube.Service.VideoService
import com.example.youtube.databinding.FragmentPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.firestore.EventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class PlayerFragment: Fragment(R.layout.fragment_player) {
    private var binding: FragmentPlayerBinding? = null
    private lateinit var videoAdapter: VideoAdapter
    private var player: ExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initMotionLayout(fragmentPlayerBinding)
        initRecyclerView(fragmentPlayerBinding)
        initPlayer(fragmentPlayerBinding)
        initVideoController(fragmentPlayerBinding)

        getVideoList()
    }

    override fun onStop() {
        super.onStop()

        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
        player?.release()
    }

    // playerMotionLayout과 mainMotionLayout을 연결
    private fun initMotionLayout(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playerMotionLayout.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}

            // main의 MotionLayout과 같이 변화
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                binding?.let {
                    (activity as MainActivity).also { mainActivity ->   // fragment의 activity는 fragment가 붙여져있는 activity이다.
                        mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress = abs(progress)
                    }
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {}
            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })
    }

    // 리사이클러뷰 초기화
    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding) {
        videoAdapter = VideoAdapter(callback = { url, title ->
            play(url, title)
        })

        fragmentPlayerBinding.fragmentRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    // ExoPlayer 초기화
    private fun initPlayer(fragmentPlayerBinding: FragmentPlayerBinding) {
        context?.let { context ->
            player = ExoPlayer.Builder(context).build()
        }

        fragmentPlayerBinding.playerView.player = player

        binding?.let {
            player?.addListener(object: Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {   // Playing 여부가 바뀔때마다 함수 호출
                    super.onIsPlayingChanged(isPlaying)

                    if(isPlaying) {
                        it.bottomVideoController.setImageResource(R.drawable.ic_baseline_pause_24)
                    } else {
                        it.bottomVideoController.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }
            })
        }

    }

    // VideoController를 누르면 재생/일시정지하게끔
    private fun initVideoController(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.bottomVideoController.setOnClickListener {
            val player = this.player ?: return@setOnClickListener

            if(player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
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

    fun play(url: String, title: String) {
        // url -> data source -> media source
        context?.let { context ->
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            val dataSourceFactory = DefaultDataSource.Factory(context)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)

            player?.apply {
                setMediaSource(mediaSource)
                prepare()
                play()
            }
        }

        binding?.let {
            it.playerMotionLayout.transitionToEnd()   // MotionLayout의 End상태로 변함
            it.bottomVideoTitle.text = title
        }
    }

}