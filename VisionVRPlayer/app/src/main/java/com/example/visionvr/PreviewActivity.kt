package com.example.visionvr

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

class PreviewActivity : Activity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var status: TextView
    private lateinit var vrButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val path = intent.getStringExtra(EXTRA_FILE_PATH)
        if (path.isNullOrBlank()) {
            finish()
            return
        }

        val file = File(path)
        if (!file.exists() || file.length() <= 0L) {
            finish()
            return
        }

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
        }

        playerView = PlayerView(this).apply {
            useController = true
            setBackgroundColor(Color.BLACK)
        }
        root.addView(
            playerView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 14, 24, 24)
            setBackgroundColor(0xB3000000.toInt())
        }

        status = TextView(this).apply {
            text = "Проверяю декодирование видео…"
            setTextColor(Color.WHITE)
            textSize = 14f
            gravity = Gravity.CENTER
        }
        panel.addView(
            status,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        vrButton = Button(this).apply {
            text = "Запустить VR"
            isEnabled = false
            setOnClickListener { launchVr(file) }
        }
        panel.addView(
            vrButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            panel,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
        )

        setContentView(root)

        val renderersFactory = DefaultRenderersFactory(this)
            .setEnableDecoderFallback(true)

        player = ExoPlayer.Builder(this, renderersFactory).build()
        playerView.player = player

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> status.text = "Открываю видео…"
                    Player.STATE_READY -> {
                        status.text = "Обычное воспроизведение работает. Можно запускать VR."
                        vrButton.isEnabled = true
                    }
                    Player.STATE_ENDED -> status.text = "Видео закончено. VR доступен."
                    Player.STATE_IDLE -> Unit
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                status.text = "Ошибка декодирования: ${error.errorCodeName}\n${error.message ?: "без описания"}"
                vrButton.isEnabled = false
            }
        })

        player.setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
        player.prepare()
        player.playWhenReady = true
    }

    private fun launchVr(file: File) {
        player.pause()
        startActivity(android.content.Intent(this, VrPlayerActivity::class.java).apply {
            putExtra(VrPlayerActivity.EXTRA_URI, Uri.fromFile(file).toString())
            putExtra(
                VrPlayerActivity.EXTRA_MODE,
                intent.getStringExtra(VrPlayerActivity.EXTRA_MODE) ?: VrMode.IMMERSIVE.name
            )
            putExtra(
                VrPlayerActivity.EXTRA_PROJECTION,
                intent.getStringExtra(VrPlayerActivity.EXTRA_PROJECTION) ?: VideoProjection.FLAT.name
            )
            putExtra(
                VrPlayerActivity.EXTRA_PACKING,
                intent.getStringExtra(VrPlayerActivity.EXTRA_PACKING) ?: StereoPacking.MONO.name
            )
        })
    }

    override fun onStart() {
        super.onStart()
        if (::player.isInitialized) player.playWhenReady = true
    }

    override fun onStop() {
        if (::player.isInitialized) player.playWhenReady = false
        super.onStop()
    }

    override fun onDestroy() {
        if (::player.isInitialized) {
            playerView.player = null
            player.release()
        }
        super.onDestroy()
    }

    companion object {
        const val EXTRA_FILE_PATH = "preview_file_path"
        const val EXTRA_DISPLAY_NAME = "preview_display_name"
    }
}
