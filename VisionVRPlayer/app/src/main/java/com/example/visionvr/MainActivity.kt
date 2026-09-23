package com.example.visionvr

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {

    private var selectedMode = VrMode.IMMERSIVE
    private var selectedProjection = VideoProjection.FLAT
    private var selectedPacking = StereoPacking.MONO

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
        projectionSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedProjection = VideoProjection.entries[position]
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        root.addView(projectionSpinner, fullWidth(58))

        root.addView(sectionTitle("Стереоупаковка"))
        val packingSpinner = Spinner(this)
        packingSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Mono / обычное", "Side-by-Side 3D", "Top/Bottom 3D")
        )
        packingSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedPacking = StereoPacking.entries[position]
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        root.addView(packingSpinner, fullWidth(58))

        root.addView(TextView(this).apply {
            text = "Для обычного 2D режим «Внутри» использует панорамную кривизну и движение головы. Настоящая геометрическая глубина для каждого объекта требует отдельной AI-карты глубины."
            setTextColor(Color.GRAY)
            textSize = 13f
            setPadding(0, 22, 0, 22)
        }, fullWidth(ViewGroup.LayoutParams.WRAP_CONTENT))

        root.addView(Button(this).apply {
            text = "Выбрать видео с телефона"
            textSize = 18f
            setOnClickListener { openVideo() }
        }, fullWidth(64))

        val urlField = EditText(this).apply {
            hint = "https://…  (MP4 / HLS / DASH)"
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            setSingleLine(true)
            setPadding(16, 8, 16, 8)
        }
        root.addView(urlField, fullWidth(58))

        root.addView(Button(this).apply {
            text = "Открыть ссылку"
            setOnClickListener {
                val raw = urlField.text.toString().trim()
                val remote = Uri.parse(raw)
                if (raw.isNotEmpty() && (remote.scheme == "https" || remote.scheme == "http")) {
                    launchVr(remote)
                } else {
                    Toast.makeText(this@MainActivity, "Введите корректную http/https ссылку", Toast.LENGTH_SHORT).show()
                }
            }
        }, fullWidth(58))

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
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
        }
        startActivityForResult(intent, REQ_VIDEO)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQ_VIDEO || resultCode != RESULT_OK) return
        val uri: Uri = data?.data ?: return
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: SecurityException) {
        }

        launchVr(uri)
    }

    private fun launchVr(uri: Uri) {
        startActivity(Intent(this, VrPlayerActivity::class.java).apply {
            putExtra(VrPlayerActivity.EXTRA_URI, uri.toString())
            putExtra(VrPlayerActivity.EXTRA_MODE, selectedMode.name)
            putExtra(VrPlayerActivity.EXTRA_PROJECTION, selectedProjection.name)
            putExtra(VrPlayerActivity.EXTRA_PACKING, selectedPacking.name)
        })
    }

    companion object {
        private const val REQ_VIDEO = 42
    }
}
