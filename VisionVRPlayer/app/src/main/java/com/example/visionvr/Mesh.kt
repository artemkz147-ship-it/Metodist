package com.example.visionvr

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.*

class Mesh private constructor(
    vertices: FloatArray,
    indices: ShortArray
) {
    private val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer().apply { put(vertices); position(0) }
    private val indexBuffer: ShortBuffer = ByteBuffer.allocateDirect(indices.size * 2)
        .order(ByteOrder.nativeOrder()).asShortBuffer().apply { put(indices); position(0) }
    private val indexCount = indices.size

    fun draw(positionLoc: Int, uvLoc: Int) {
        vertexBuffer.position(0)
        GLES30.glEnableVertexAttribArray(positionLoc)
        GLES30.glVertexAttribPointer(positionLoc, 3, GLES30.GL_FLOAT, false, STRIDE, vertexBuffer)
        vertexBuffer.position(3)
        GLES30.glEnableVertexAttribArray(uvLoc)
        GLES30.glVertexAttribPointer(uvLoc, 2, GLES30.GL_FLOAT, false, STRIDE, vertexBuffer)
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexCount, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
        GLES30.glDisableVertexAttribArray(positionLoc)
        GLES30.glDisableVertexAttribArray(uvLoc)
    }

    companion object {
        private const val STRIDE = 5 * 4

        fun flatScreen(width: Float = 3.4f, height: Float = 1.92f): Mesh {
            val w = width / 2f
            val h = height / 2f
            return Mesh(
                floatArrayOf(
                    -w, -h, 0f, 0f, 1f,
                     w, -h, 0f, 1f, 1f,
                     w,  h, 0f, 1f, 0f,
                    -w,  h, 0f, 0f, 0f
                ),
                shortArrayOf(0, 1, 2, 0, 2, 3)
            )
        }

        fun curvedScreen(radius: Float = 2.6f, arcDegrees: Float = 155f, height: Float = 2.25f, segments: Int = 64): Mesh {
            val verts = ArrayList<Float>()
            val inds = ArrayList<Short>()
            val half = Math.toRadians((arcDegrees / 2f).toDouble())
            for (i in 0..segments) {
                val t = i.toFloat() / segments
                val a = -half + 2.0 * half * t
                val x = (sin(a) * radius).toFloat()
                val z = (-cos(a) * radius).toFloat()
                verts += x; verts += -height / 2f; verts += z; verts += t; verts += 1f
                verts += x; verts +=  height / 2f; verts += z; verts += t; verts += 0f
            }
            for (i in 0 until segments) {
                val b = (i * 2).toShort()
                inds += b; inds += (b + 1).toShort(); inds += (b + 3).toShort()
                inds += b; inds += (b + 3).toShort(); inds += (b + 2).toShort()
            }
            return Mesh(verts.toFloatArray(), inds.toShortArray())
        }

        fun sphere(radius: Float = 6f, lonSegments: Int = 72, latSegments: Int = 40): Mesh {
            val verts = ArrayList<Float>()
            val inds = ArrayList<Short>()
            for (y in 0..latSegments) {
                val v = y.toFloat() / latSegments
                val phi = v * Math.PI
                for (x in 0..lonSegments) {
                    val u = x.toFloat() / lonSegments
                    val theta = u * Math.PI * 2.0 - Math.PI
                    val sx = (sin(phi) * sin(theta) * radius).toFloat()
                    val sy = (cos(phi) * radius).toFloat()
                    val sz = (-sin(phi) * cos(theta) * radius).toFloat()
                    verts += sx; verts += sy; verts += sz; verts += u; verts += v
                }
            }
            val row = lonSegments + 1
            for (y in 0 until latSegments) {
                for (x in 0 until lonSegments) {
                    val a = (y * row + x).toShort()
                    val b = (a + row).toShort()
                    inds += a; inds += b; inds += (a + 1).toShort()
                    inds += (a + 1).toShort(); inds += b; inds += (b + 1).toShort()
                }
            }
            return Mesh(verts.toFloatArray(), inds.toShortArray())
        }

        fun hemisphere180(radius: Float = 6f, lonSegments: Int = 48, latSegments: Int = 40): Mesh {
            val verts = ArrayList<Float>()
            val inds = ArrayList<Short>()
            for (y in 0..latSegments) {
                val v = y.toFloat() / latSegments
                val phi = v * Math.PI
                for (x in 0..lonSegments) {
                    val u = x.toFloat() / lonSegments
                    val theta = -Math.PI / 2.0 + u * Math.PI
                    val sx = (sin(phi) * sin(theta) * radius).toFloat()
                    val sy = (cos(phi) * radius).toFloat()
                    val sz = (-sin(phi) * cos(theta) * radius).toFloat()
                    verts += sx; verts += sy; verts += sz; verts += u; verts += v
                }
            }
            val row = lonSegments + 1
            for (y in 0 until latSegments) {
                for (x in 0 until lonSegments) {
                    val a = (y * row + x).toShort()
                    val b = (a + row).toShort()
                    inds += a; inds += b; inds += (a + 1).toShort()
                    inds += (a + 1).toShort(); inds += b; inds += (b + 1).toShort()
                }
            }
            return Mesh(verts.toFloatArray(), inds.toShortArray())
        }
    }
}
