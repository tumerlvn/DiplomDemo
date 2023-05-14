package com.example.diplomdemo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.example.diplomdemo.DemoCommunity
import com.example.diplomdemo.R
import nl.tudelft.ipv8.android.IPv8Android
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream

class GenerateDFActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Здесь мы можем выбирать из чего будет состоять наш ЦС и затем генерировать его.
        // После генерации ЦС подписывается и отправляется контактам из нашего списка
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_dfactivity)

        val userActivityCheckBox = findViewById<CheckBox>(R.id.userActivityCB)
        val imageCheckBox = findViewById<CheckBox>(R.id.imageCB)
        val gpsCheckBox = findViewById<CheckBox>(R.id.gpsCB)

        val community = IPv8Android.getInstance().getOverlay<DemoCommunity>()!!

        val generateDFBtn: Button = findViewById<Button>(R.id.generateDFBtn)
        generateDFBtn.setOnClickListener {
            val json = JSONObject()
            if (userActivityCheckBox.isChecked) {
                val bufferedReader: BufferedReader = community.file.bufferedReader()
                val str = bufferedReader.use { it.readText() }
                json.put("activity", str)
            }
            if (imageCheckBox.isChecked) {
                val path = this.filesDir
                val letDirectory = File(path, "Images")
                val image = File(letDirectory, "photo.jpg")
                if (image.exists()) {
                    val str = image.toString()
                    json.put("image", str)
                } else {
                    Log.d("Demo.generate", "Image doesn't exists")
                }
            }
            if (gpsCheckBox.isChecked) {
                // Todo: сделать определение местоположения
            }
            if (!userActivityCheckBox.isChecked and !imageCheckBox.isChecked
                and !gpsCheckBox.isChecked) {
                Toast.makeText(this, "Choose at least one option", Toast.LENGTH_SHORT).show()
            } else {
                val path = this.filesDir
                val letDirectory = File(path, "DemoDirectory")
                val file = File(letDirectory, "DF.json")
                if (!file.exists()) {
                    val os = FileOutputStream(file)
                    os.write(json.toString(4).toByteArray())
                }
            }
        }
    }
}