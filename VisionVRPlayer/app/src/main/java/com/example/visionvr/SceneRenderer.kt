package com.example.visionvr

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Handler
import android.os.Looper
import android.view.Surface
import androidx.media3.exoplayer.ExoPlayer
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SceneRenderer(
    private val player: ExoPlayer,
    private val headTracker: HeadTracker,
    private val mode: VrMode,
    private val projectionType: VideoProjection,
    private val packing: StereoPacking,
    private val onSurfaceReady: () -> Unit,
    private val onRendererError: (String) -> Unit
) : GLSurfaceView.Renderer {

    private var videoTexture = 0
    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null
    private val frameAvailable = AtomicBoolean(false)
    private val textureMatrix = FloatArray(16).also { Matrix.setIdentityM(it, 0) }

    private var videoProgram = 0
    private var solidProgram = 0
    private lateinit var screen: Mesh
    private lateinit var curved: Mesh
    private lateinit var sphere: Mesh
    private lateinit var hemi: Mesh

    private var width = 1
    private var height = 1

    private val projection = FloatArray(16)
    private val headView = FloatArray(16)
    private val eyeView = FloatArray(16)
    private val model = FloatArray(16)
    private val mv = FloatArray(16)
    private val mvp = FloatArray(16)
    private val combinedView = FloatArray(16)
    @Volatile private var sourceAspect = 16f / 9f
    @Volatile private var rendererFailed = false

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        try {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)

        videoProgram = GlUtil.linkProgram(VERTEX_SHADER, VIDEO_FRAGMENT_SHADER)
        solidProgram = GlUtil.linkProgram(SOLID_VERTEX_SHADER, SOLID_FRAGMENT_SHADER)
        if (videoProgram == 0 || solidProgram == 0) {
            failRenderer("Телефон не смог создать VR-шейдер OpenGL ES 2.0")
            return
        }

        screen = Mesh.flatScreen()
        curved = Mesh.curvedScreen()
        sphere = Mesh.sphere()
        hemi = Mesh.hemisphere180()

        videoTexture = GlUtil.createExternalTexture()
        if (videoTexture == 0) {
            failRenderer("Телефон не смог создать видеотекстуру OpenGL")
            return
        }
        surfaceTexture = SurfaceTexture(videoTexture).apply {
            setOnFrameAvailableListener { frameAvailable.set(true) }
        }
        surface = Surface(surfaceTexture)
        val createdSurface = surface
        Handler(Looper.getMainLooper()).post {
            if (createdSurface != null) player.setVideoSurface(createdSurface)
            onSurfaceReady()
        }
        } catch (t: Throwable) {
            failRenderer(t.message ?: t.javaClass.simpleName)
        }
    }

    private fun failRenderer(message: String) {
        rendererFailed = true
        Handler(Looper.getMainLooper()).post {
            onRendererError(message)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, w: Int, h: Int) {
        width = w.coerceAtLeast(2)
        height = h.coerceAtLeast(1)
    }

    override fun onDrawFrame(gl: GL10?) {
        if (rendererFailed) {
            GLES20.glClearColor(0f, 0f, 0f, 1f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            return
        }
        if (frameAvailable.compareAndSet(true, false)) {
            surfaceTexture?.updateTexImage()
            surfaceTexture?.getTransformMatrix(textureMatrix)
        }

        val clear = when (mode) {
            VrMode.NATURE -> floatArrayOf(0.08f, 0.18f, 0.22f, 1f)
            VrMode.CINEMA -> floatArrayOf(0.008f, 0.008f, 0.012f, 1f)
            VrMode.IMMERSIVE -> floatArrayOf(0f, 0f, 0f, 1f)
        }
        GLES20.glClearColor(clear[0], clear[1], clear[2], clear[3])
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        headTracker.viewRotation(headView)
        val halfW = width / 2
        val eyeAspect = halfW.toFloat() / height.toFloat()
        Matrix.perspectiveM(projection, 0, 92f, eyeAspect, 0.05f, 100f)

        drawEye(0, 0, halfW, false)
        drawEye(halfW, 0, width - halfW, true)
    }

    private fun drawEye(x: Int, y: Int, w: Int, rightEye: Boolean) {
        GLES20.glViewport(x, y, w, height)

        Matrix.setIdentityM(eyeView, 0)
        val isPanorama = mode == VrMode.IMMERSIVE && projectionType != VideoProjection.FLAT
        val eyeOffset = if (isPanorama) 0f else if (rightEye) -IPD_METERS / 2f else IPD_METERS / 2f
        Matrix.translateM(eyeView, 0, eyeOffset, 0f, 0f)
        Matrix.multiplyMM(combinedView, 0, eyeView, 0, headView, 0)
        System.arraycopy(combinedView, 0, eyeView, 0, 16)

        when (mode) {
            VrMode.IMMERSIVE -> drawImmersive(rightEye)
            VrMode.CINEMA -> drawCinema(rightEye)
            VrMode.NATURE -> drawNature(rightEye)
        }
    }

    private fun drawImmersive(rightEye: Boolean) {
        Matrix.setIdentityM(model, 0)
        when (projectionType) {
            VideoProjection.VR_360 -> drawVideoMesh(sphere, rightEye)
            VideoProjection.VR_180 -> drawVideoMesh(hemi, rightEye)
            VideoProjection.FLAT -> {
                val targetAspect = 16f / 9f
                Matrix.scaleM(model, 0, 1f, targetAspect / effectiveAspect(), 1f)
                drawVideoMesh(curved, rightEye)
            }
        }
    }

    private fun drawCinema(rightEye: Boolean) {
        drawRoom()
        Matrix.setIdentityM(model, 0)
        Matrix.translateM(model, 0, 0f, 0.25f, -4.2f)
        Matrix.scaleM(model, 0, effectiveAspect() / (16f / 9f), 1f, 1f)
        drawVideoMesh(screen, rightEye)
    }

    private fun drawNature(rightEye: Boolean) {
        drawNatureEnvironment()
        Matrix.setIdentityM(model, 0)
        Matrix.translateM(model, 0, 0f, 1.25f, -5.2f)
        val xScale = 1.7f * effectiveAspect() / (16f / 9f)
        Matrix.scaleM(model, 0, xScale, 1.7f, 1.7f)
        drawVideoMesh(screen, rightEye)
    }

    private fun drawVideoMesh(mesh: Mesh, rightEye: Boolean) {
        GLES20.glUseProgram(videoProgram)
        val pos = GLES20.glGetAttribLocation(videoProgram, "aPosition")
        val uv = GLES20.glGetAttribLocation(videoProgram, "aUv")
        val mvpLoc = GLES20.glGetUniformLocation(videoProgram, "uMvp")
        val texMatrixLoc = GLES20.glGetUniformLocation(videoProgram, "uTexMatrix")
        val layoutLoc = GLES20.glGetUniformLocation(videoProgram, "uLayout")
        val eyeLoc = GLES20.glGetUniformLocation(videoProgram, "uEye")

        Matrix.multiplyMM(mv, 0, eyeView, 0, model, 0)
        Matrix.multiplyMM(mvp, 0, projection, 0, mv, 0)

        GLES20.glUniformMatrix4fv(mvpLoc, 1, false, mvp, 0)
        GLES20.glUniformMatrix4fv(texMatrixLoc, 1, false, textureMatrix, 0)
        GLES20.glUniform1i(layoutLoc, when (packing) {
            StereoPacking.SIDE_BY_SIDE -> 1
            StereoPacking.TOP_BOTTOM -> 2
            StereoPacking.MONO -> 0
        })
        GLES20.glUniform1i(eyeLoc, if (rightEye) 1 else 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, videoTexture)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(videoProgram, "uVideo"), 0)
        mesh.draw(pos, uv)
    }

    private fun drawRoom() {
        drawSolidPlane(y = -1.25f, z = -4f, sx = 8f, sz = 10f, r = 0.045f, g = 0.045f, b = 0.055f)
        drawSolidBackdrop(z = -6.2f, sy = 4.5f, sx = 9f, r = 0.018f, g = 0.018f, b = 0.024f)
    }

    private fun drawNatureEnvironment() {
        drawSolidPlane(y = -1.25f, z = -4f, sx = 18f, sz = 18f, r = 0.05f, g = 0.16f, b = 0.07f)
        drawSolidBackdrop(z = -10f, sy = 7f, sx = 18f, r = 0.09f, g = 0.17f, b = 0.22f)
    }

    private fun drawSolidPlane(y: Float, z: Float, sx: Float, sz: Float, r: Float, g: Float, b: Float) {
        val plane = Mesh.flatScreen(1f, 1f)
        Matrix.setIdentityM(model, 0)
        Matrix.translateM(model, 0, 0f, y, z)
        Matrix.rotateM(model, 0, -90f, 1f, 0f, 0f)
        Matrix.scaleM(model, 0, sx, sz, 1f)
        drawSolid(plane, r, g, b)
    }

    private fun drawSolidBackdrop(z: Float, sy: Float, sx: Float, r: Float, g: Float, b: Float) {
        val plane = Mesh.flatScreen(1f, 1f)
        Matrix.setIdentityM(model, 0)
        Matrix.translateM(model, 0, 0f, 0.5f, z)
        Matrix.scaleM(model, 0, sx, sy, 1f)
        drawSolid(plane, r, g, b)
    }

    private fun drawSolid(mesh: Mesh, r: Float, g: Float, b: Float) {
        GLES20.glUseProgram(solidProgram)
        val pos = GLES20.glGetAttribLocation(solidProgram, "aPosition")
        val uv = GLES20.glGetAttribLocation(solidProgram, "aUv")
        Matrix.multiplyMM(mv, 0, eyeView, 0, model, 0)
        Matrix.multiplyMM(mvp, 0, projection, 0, mv, 0)
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(solidProgram, "uMvp"), 1, false, mvp, 0)
        GLES20.glUniform3f(GLES20.glGetUniformLocation(solidProgram, "uColor"), r, g, b)
        mesh.draw(pos, uv)
    }

    fun setVideoAspect(width: Int, height: Int) {
        if (width > 0 && height > 0) sourceAspect = width.toFloat() / height.toFloat()
    }

    private fun effectiveAspect(): Float = when (packing) {
        StereoPacking.MONO -> sourceAspect
        StereoPacking.SIDE_BY_SIDE -> sourceAspect / 2f
        StereoPacking.TOP_BOTTOM -> sourceAspect * 2f
    }.coerceIn(0.45f, 3.2f)

    fun release() {
        surface?.let { player.clearVideoSurface(it) }
        surface?.release()
        surface = null
        surfaceTexture?.release()
        surfaceTexture = null
    }

    companion object {
        private const val IPD_METERS = 0.064f

        private const val VERTEX_SHADER = """
            uniform mat4 uMvp;
            attribute vec3 aPosition;
            attribute vec2 aUv;
            varying vec2 vUv;
            void main() {
                gl_Position = uMvp * vec4(aPosition, 1.0);
                vUv = aUv;
            }
        """

        private const val VIDEO_FRAGMENT_SHADER = """
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            uniform samplerExternalOES uVideo;
            uniform mat4 uTexMatrix;
            uniform int uLayout;
            uniform int uEye;
            varying vec2 vUv;

            vec2 applyStereoLayout(vec2 uv) {
                if (uLayout == 1) {
                    uv.x = uv.x * 0.5 + (uEye == 1 ? 0.5 : 0.0);
                } else if (uLayout == 2) {
                    uv.y = uv.y * 0.5 + (uEye == 1 ? 0.5 : 0.0);
                }
                return uv;
            }

            void main() {
                vec2 uv = applyStereoLayout(vUv);
                vec4 tx = uTexMatrix * vec4(uv, 0.0, 1.0);
                gl_FragColor = texture2D(uVideo, tx.xy);
            }
        """

        private const val SOLID_VERTEX_SHADER = """
            uniform mat4 uMvp;
            attribute vec3 aPosition;
            attribute vec2 aUv;
            void main() {
                gl_Position = uMvp * vec4(aPosition, 1.0);
            }
        """

        private const val SOLID_FRAGMENT_SHADER = """
            precision mediump float;
            uniform vec3 uColor;
            void main() {
                gl_FragColor = vec4(uColor, 1.0);
            }
        """
    }
}
