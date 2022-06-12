package com.example.youtube

import android.content.Context
import android.gesture.Gesture
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

class CustomMotionLayout(context: Context, attributeSet: AttributeSet? = null): MotionLayout(context, attributeSet) {
    private var motionTouchStarted = false
    private val mainContainerLayout by lazy {
        findViewById<View>(R.id.mainContainerLayout)
    }
    private val hitRect = Rect()

    init{   // 트랜지션이 완료 되면 초기화
        setTransitionListener(object: TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                motionTouchStarted = false
            }
            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })
    }

    // 터치 이벤트가 mainContainerLayout에서 일어나는지
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                motionTouchStarted = false
                return super.onTouchEvent(event)
            }
        }

        if(!motionTouchStarted) {
            mainContainerLayout.getHitRect(hitRect)   // mainContainerLayout의 rect영역을 얻어와서 hitRect로 설정
            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt())   // hitRect 영역 안에서 일어난 이벤트이냐
        }

        return super.onTouchEvent(event) && motionTouchStarted
    }

    private val gestureListener by lazy {
        object: GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                mainContainerLayout.getHitRect(hitRect)
                return hitRect.contains(e1.x.toInt(), e2.y.toInt())
            }
        }
    }

    private val gestureDetector by lazy {
        GestureDetector(context, gestureListener)
    }

    // 스크롤 제스처 탐색
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

}