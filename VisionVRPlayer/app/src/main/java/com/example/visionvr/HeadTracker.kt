package com.example.visionvr

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Matrix

class HeadTracker(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        ?: sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val rotation = FloatArray(16).also { Matrix.setIdentityM(it, 0) }
    private val recenter = FloatArray(16).also { Matrix.setIdentityM(it, 0) }
    private val tmp = FloatArray(16)

    @Volatile private var needsRecenter = true

    fun start() {
        sensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun recenter() {
        needsRecenter = true
    }

    fun viewRotation(out: FloatArray) {
        synchronized(rotation) {
            Matrix.multiplyMM(tmp, 0, recenter, 0, rotation, 0)
            Matrix.invertM(out, 0, tmp, 0)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        synchronized(rotation) {
            val r3 = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(r3, event.values)
            SensorManager.getOrientation(r3, FloatArray(3))

            val android4 = FloatArray(16)
            Matrix.setIdentityM(android4, 0)
            android4[0] = r3[0]; android4[1] = r3[1]; android4[2] = r3[2]
            android4[4] = r3[3]; android4[5] = r3[4]; android4[6] = r3[5]
            android4[8] = r3[6]; android4[9] = r3[7]; android4[10] = r3[8]

            val landscapeFix = FloatArray(16)
            Matrix.setRotateM(landscapeFix, 0, 90f, 0f, 0f, 1f)
            Matrix.multiplyMM(rotation, 0, landscapeFix, 0, android4, 0)

            if (needsRecenter) {
                Matrix.invertM(recenter, 0, rotation, 0)
                needsRecenter = false
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
