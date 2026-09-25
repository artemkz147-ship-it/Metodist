package com.example.visionvr

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : Activity() {

    private var selectedMode = VrMode.IMMERSIVE
    private var selectedProjection = VideoProjection.FLAT
    private var selectedPacking = StereoPacking.MONO

    private lateinit var chooseButton: Button
    private lateinit var statusView: TextView
    private val importing = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(48, 40, 48, 40)
            setBackgroundColor(Color.rgb(15, 17, 20))
        }

        root.addView(TextView(this).apply {
            text = "Vision VR Player"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }, fullWidth(64))

        root.addView(TextView(this).apply {
            text = "Выберите сцену и формат видео"
            textSize = 16f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }, fullWidth(54))

        root.addView(sectionTitle("VR-сцена"))
        val modeGroup = RadioGroup(this).apply { orientation = RadioGroup.VERTICAL }
        listOf(
            "Внутри видео" to VrMode.IMMERSIVE,
            "Кинотеатр" to VrMode.CINEMA,
            "Природа + огромный проектор" to VrMode.NATURE
        ).forEachIndexed { index, pair ->
            modeGroup.addView(RadioButton(this).apply {
                text = pair.first
                setTextColor(Color.WHITE)
                textSize = 17f
                isChecked = index == 0
                setOnClickListener { selectedMode = pair.second }
            })
        }
        root.addView(modeGroup, fullWidth(ViewGroup.LayoutParams.WRAP_CONTENT))

        root.addView(sectionTitle("Проекция видео"))
        val projectionSpinner = Spinner(this)
        projectionSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Обычное плоское видео", "VR 180°", "VR 360°")
        )
        projectionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedProjection = VideoProjection.entries[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        root.addView(projectionSpinner, fullWidth(58))

        root.addView(sectionTitle("Стереоупаковка"))
        val packingSpinner = Spinner(this)
        packingSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Mono / обычное", "Side-by-Side 3D", "Top/Bottom 3D")
        )
        packingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedPacking = StereoPacking.entries[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        root.addView(packingSpinner, fullWidth(58))

        statusView = TextView(this).apply {
            text = "Видео пока не выбрано"
            setTextColor(Color.LTGRAY)
            textSize = 14f
            setPadding(0, 24, 0, 16)
            gravity = Gravity.CENTER
        }
        root.addView(statusView, fullWidth(ViewGroup.LayoutParams.WRAP_CONTENT))

        chooseButton = Button(this).apply {
            text = "Выбрать видео с телефона"
            textSize = 18f
            setOnClickListener { openVideo() }
        }
        root.addView(chooseButton, fullWidth(64))

        root.addView(Button(this).apply {
            text = "Последний сбой / диагностика"
            setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Диагностика")
                    .setMessage(CrashReporterApp.collectReport(this@MainActivity))
                    .setPositiveButton("OK", null)
                    .show()
            }
        }, fullWidth(56))

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)
    }

    private fun sectionTitle(textValue: String) = TextView(this).apply {
        text = textValue
        setTextColor(Color.WHITE)
        textSize = 19f
        setPadding(0, 26, 0, 8)
    }

    private fun fullWidth(height: Int) = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        if (height == ViewGroup.LayoutParams.WRAP_CONTENT) height else dp(height)
    )

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun openVideo() {
        if (importing.get()) return
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivityForResult(intent, REQ_VIDEO)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQ_VIDEO || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        importVideo(uri)
    }

    private fun importVideo(uri: Uri) {
        if (!importing.compareAndSet(false, true)) return
        chooseButton.isEnabled = false
        statusView.text = "Импортирую видео во внутреннее хранилище приложения…"

        Thread {
            try {
                val importedDir = File(filesDir, "imported_video").apply { mkdirs() }
                importedDir.listFiles()?.forEach { it.delete() }

                val displayName = queryDisplayName(uri) ?: "video"
                val extension = safeExtension(displayName, contentResolver.getType(uri))
                val target = File(importedDir, "selected.$extension")

                contentResolver.openInputStream(uri).use { input ->
                    requireNotNull(input) { "Не удалось открыть выбранный файл" }
                    FileOutputStream(target).use { output ->
                        input.copyTo(output, bufferSize = 1024 * 1024)
                        output.fd.sync()
                    }
                }

                if (!target.exists() || target.length() <= 0L) {
                    error("Файл скопирован с нулевым размером")
                }

                runOnUiThread {
                    importing.set(false)
                    chooseButton.isEnabled = true
                    statusView.text = "Выбрано: $displayName\n${formatSize(target.length())}\nПроверяю обычное воспроизведение…"
                    launchPreview(target, displayName)
                }
            } catch (t: Throwable) {
                runOnUiThread {
                    importing.set(false)
                    chooseButton.isEnabled = true
                    statusView.text = "Ошибка импорта: ${t.message ?: t.javaClass.simpleName}"
                    AlertDialog.Builder(this)
                        .setTitle("Не удалось импортировать видео")
                        .setMessage(t.stackTraceToString().take(6000))
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }.start()
    }

    private fun launchPreview(file: File, displayName: String) {
        startActivity(Intent(this, PreviewActivity::class.java).apply {
            putExtra(PreviewActivity.EXTRA_FILE_PATH, file.absolutePath)
            putExtra(PreviewActivity.EXTRA_DISPLAY_NAME, displayName)
            putExtra(VrPlayerActivity.EXTRA_MODE, selectedMode.name)
            putExtra(VrPlayerActivity.EXTRA_PROJECTION, selectedProjection.name)
            putExtra(VrPlayerActivity.EXTRA_PACKING, selectedPacking.name)
        })
    }

    private fun queryDisplayName(uri: Uri): String? {
        return try {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }
        } catch (_: Throwable) {
            null
        }
    }

    private fun safeExtension(name: String, mime: String?): String {
        val fromName = name.substringAfterLast('.', "").lowercase()
            .filter { it.isLetterOrDigit() }
            .take(8)
        if (fromName.isNotBlank()) return fromName

        val fromMime = mime?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
            ?.lowercase()
            ?.filter { it.isLetterOrDigit() }
            ?.take(8)
        return if (fromMime.isNullOrBlank()) "mp4" else fromMime
    }

    private fun formatSize(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return String.format("%.1f МБ", mb)
    }

    companion object {
        private const val REQ_VIDEO = 42
    }
}
