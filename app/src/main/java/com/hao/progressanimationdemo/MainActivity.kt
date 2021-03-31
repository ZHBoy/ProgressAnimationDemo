package com.hao.progressanimationdemo

import android.graphics.Color
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import me.samlss.bloom.Bloom
import me.samlss.bloom.effector.BloomEffector.Builder
import me.samlss.bloom.listener.BloomListener
import me.samlss.bloom.utils.ViewUtils
import java.util.*


/**
 * 带爆炸效果的进度条
 */
class MainActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null

    private var rlRoot: FrameLayout? = null
    private var progressCount = 0
    private var mRectF: RectF? = null
    private var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressBar)
        rlRoot = findViewById(R.id.rl_root)

        progressCount = progressBar?.progress ?: 0

        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Log.e("TAG", "没隔100毫秒执行一次操作");
                if (progressBar?.progress == 100) {
                    timer.cancel()
                    return
                }
                runOnUiThread {
                    progressBar?.progress = progressCount++
                    insertAnimation()
                }
            }
        }, 1000, 1000)
    }

    private fun getCurrentProgressRectF(): RectF? {
        progressBar?.let { pb ->

            val rectF1 = ViewUtils.getRectOnScreen(pb)

            val v_left: Float = pb.getLeft().toFloat()
            val v_top: Float = pb.getTop().toFloat()
            val v_right: Float = pb.getRight().toFloat()
            val v_botton: Float = pb.getBottom().toFloat()
            rectF1[v_left, v_top, v_right] = v_botton
            rectF1.right = rectF1.left + (rectF1.right - rectF1.left) * pb.progress / 100
            rectF1.left = rectF1.right - 10

            return rectF1
        }
        return null
    }

    private fun insertAnimation() {
        mRectF = getCurrentProgressRectF()

        val lp =
            FrameLayout.LayoutParams(mRectF!!.width().toInt() , mRectF!!.height().toInt())
        lp.leftMargin = mRectF!!.left.toInt()
        lp.topMargin = mRectF!!.top.toInt()

        view = View(this)
        view?.setBackgroundColor(Color.parseColor("#98FF0000"))

        rlRoot?.addView(view, lp)
        view?.post {
            Bloom.with(this)
                .setParticleRadius(2f)
                .setEffector(
                    Builder()
                        .setDuration(2000)
                        .setAnchor(view!!.width / 2.toFloat(), view!!.height / 2.toFloat())
                        .build()
                ).setBloomListener(object : BloomListener {
                    override fun onBegin() {
                        rlRoot?.removeView(view)
                    }

                    override fun onEnd() {
                    }

                })
                .boom(view)
        }
    }
}
