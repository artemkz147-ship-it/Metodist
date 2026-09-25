package com.example.visionvr

import android.app.ActivityManager
import android.app.Application
import android.app.ApplicationExitInfo
import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashReporterApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                File(filesDir, CRASH_FILE).writeText(
                    "Time: ${System.currentTimeMillis()}\n" +
                        "Thread: ${thread.name}\n" +
                        "Device: ${Build.MANUFACTURER} ${Build.MODEL}\n" +
                        "Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})\n\n" +
                        sw.toString()
                )
            } catch (_: Throwable) {
            }
            previous?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        private const val CRASH_FILE = "last_crash.txt"

        fun collectReport(context: Context): String {
            val out = StringBuilder()
            out.appendLine("Устройство: ${Build.MANUFACTURER} ${Build.MODEL}")
            out.appendLine("Android: ${Build.VERSION.RELEASE} / SDK ${Build.VERSION.SDK_INT}")

            if (Build.VERSION.SDK_INT >= 30) {
                try {
                    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    val exits = am.getHistoricalProcessExitReasons(context.packageName, 0, 5)
                    val lastBad = exits.firstOrNull {
                        it.reason == ApplicationExitInfo.REASON_CRASH ||
                            it.reason == ApplicationExitInfo.REASON_CRASH_NATIVE ||
                            it.reason == ApplicationExitInfo.REASON_ANR
                    }
                    if (lastBad != null) {
                        val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                            .format(Date(lastBad.timestamp))
                        out.appendLine()
                        out.appendLine("Последнее системное завершение:")
                        out.appendLine("Время: $stamp")
                        out.appendLine("Причина: ${exitReasonName(lastBad.reason)}")
                        out.appendLine("Статус: ${lastBad.status}")
                        if (!lastBad.description.isNullOrBlank()) {
                            out.appendLine("Описание: ${lastBad.description}")
                        }
                    }
                } catch (t: Throwable) {
                    out.appendLine()
                    out.appendLine("ApplicationExitInfo недоступен: ${t.message}")
                }
            }

            val crashFile = File(context.filesDir, CRASH_FILE)
            if (crashFile.exists()) {
                out.appendLine()
                out.appendLine("Java/Kotlin crash:")
                out.appendLine(crashFile.readText().take(7000))
            } else {
                out.appendLine()
                out.appendLine("Сохранённого Java/Kotlin stack trace нет.")
            }

            return out.toString()
        }

        private fun exitReasonName(reason: Int): String = when (reason) {
            ApplicationExitInfo.REASON_CRASH -> "CRASH"
            ApplicationExitInfo.REASON_CRASH_NATIVE -> "CRASH_NATIVE"
            ApplicationExitInfo.REASON_ANR -> "ANR"
            ApplicationExitInfo.REASON_LOW_MEMORY -> "LOW_MEMORY"
            ApplicationExitInfo.REASON_EXCESSIVE_RESOURCE_USAGE -> "EXCESSIVE_RESOURCE_USAGE"
            else -> "reason=$reason"
        }
    }
}
