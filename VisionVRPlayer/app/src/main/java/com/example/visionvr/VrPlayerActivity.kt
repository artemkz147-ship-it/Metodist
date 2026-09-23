package com.example.visionvr

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer

class VrPlayerActivity : Activity() {
    private lateinit var player: ExoPlayer
    private lateinit var glView: VrGlSurfaceView
    private lateinit var headTracker: HeadTracker
    private lateinit var renderer: SceneRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUi()

        val uri = Uri.parse(intent.getStringExtra(EXTRA_URI) ?: error("Missing video URI"))
        val mode = VrMode.valueOf(intent.getStringExtra(EXTRA_MODE) ?: VrMode.IMMERSIVE.name)
        val projection = VideoProjection.valueOf(intent.getStringExtra(EXTRA_PROJECTION) ?: VideoProjection.FLAT.name)
        val packing = StereoPacking.valueOf(intent.getStringExtra(EXTRA_PACKING) ?: StereoPacking.MONO.name)

        player = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
        headTracker = HeadTracker(this)

        glView = VrGlSurfaceView(this)
        renderer = SceneRenderer(player, headTracker, mode, projection, packing) {
            runOnUiThread { player.playWhenReady = true }
        }
        player.addListener(object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                renderer.setVideoAspect(videoSize.width, videoSize.height)
            }
        })
        glView.setRenderer(renderer)

        val root = FrameLayout(this)
        root.addView(glView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

        root.addView(TextView(this).apply {
            text = "Коснитесь двумя пальцами — центрировать взгляд"
            setTextColor(0xAAFFFFFF.toInt())
            textSize = 12f
            gravity = Gravity.CENTER
            setPadding(16, 6, 16, 6)
        }, FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
            topMargin = 10
        })
        setContentView(root)

        glView.onSingleTap = {
            if (player.isPlaying) player.pause() else player.play()
        }
        glView.onTwoFingerTap = { headTracker.recenter() }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        headTracker.start()
        glView.onResume()
        player.play()
    }

    override fun onPause() {
        player.pause()
        glView.onPause()
        headTracker.stop()
        super.onPause()
    }

    override fun onDestroy() {
        renderer.release()
        player.release()
        super.onDestroy()
    }

    private fun hideSystemUi() {
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        }
    }

    companion object {
        const val EXTRA_URI = "video_uri"
        const val EXTRA_MODE = "vr_mode"
        const val EXTRA_PROJECTION = "video_projection"
        const val EXTRA_PACKING = "stereo_packing"
    }
}

class VrGlSurfaceView(context: android.content.Context) : android.opengl.GLSurfaceView(context) {
    var onSingleTap: (() -> Unit)? = null
    var onTwoFingerTap: (() -> Unit)? = null
    private var downTime = 0L
    private var hadMultiTouch = false

    init {
        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downTime = event.eventTime
                hadMultiTouch = false
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                hadMultiTouch = true
                downTime = event.eventTime
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (hadMultiTouch && event.eventTime - downTime < 350) onTwoFingerTap?.invoke()
            }
            MotionEvent.ACTION_UP -> {
                if (!hadMultiTouch && event.eventTime - downTime < 300) onSingleTap?.invoke()
            }
        }
        return true
    }
}
