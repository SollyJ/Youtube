package com.example.youtube

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import com.example.youtube.databinding.FragmentPlayerBinding
import kotlin.math.abs

class PlayerFragment: Fragment(R.layout.fragment_player) {
    private var binding: FragmentPlayerBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

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

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}