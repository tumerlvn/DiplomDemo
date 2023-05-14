package com.example.diplomdemo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.diplomdemo.DemoCommunity
import com.example.diplomdemo.R
import com.example.diplomdemo.TimeInformer
import com.example.diplomdemo.contacts.Contact
import nl.tudelft.ipv8.android.IPv8Android
import java.io.File


// Этот activity отвечает за связь с другим контактом
class DialWithContactActivity : AppCompatActivity() {
//    inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
//        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
//        else -> @Suppress("DEPRECATION") getSerializable(key) as? T
//    }
//
//    inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
//        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
//        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dial_with_contact)

        val currentJsonContact = intent.getByteArrayExtra("contact") as ByteArray

        val (currentContact, _) = Contact.deserialize(currentJsonContact, 0)

        val editTextView = findViewById<EditText>(R.id.messageET)
        val sendButton = findViewById<Button>(R.id.sendBtn)
        val showOutgoingButton = findViewById<Button>(R.id.outgoingBtn)
        val showIncomingButton = findViewById<Button>(R.id.incomingBtn)
        val sendFileButton = findViewById<Button>(R.id.sendFileBtn)
        val showFilesButton = findViewById<Button>(R.id.showFilesBtn)

        val community = IPv8Android.getInstance().getOverlay<DemoCommunity>()!!

        val path = this.filesDir
        val letDirectory = File(path, "DemoDirectory")
        letDirectory.mkdirs()
        val file = File(letDirectory, "Records.txt")
        file.appendText("record goes here")
//        if (isExternalStorageWritable()) {
//
//        } else {
//            Log.d("Demo.dial", "External storage is not writable")
//        }


        sendButton.setOnClickListener {

            val text = editTextView.text.toString()
            Log.d("Demo.myMessage", text)
            community.sendMessageToContact(currentContact, text)
        }

        showOutgoingButton.setOnClickListener {
            val intent = Intent(this, ShowMessagesActivity::class.java)
            intent.putExtra("outgoing", true)
            intent.putExtra("contact", currentJsonContact)
            startActivity(intent).apply {

            }
        }

        showIncomingButton.setOnClickListener {
            val intent = Intent(this, ShowMessagesActivity::class.java)
            intent.putExtra("outgoing", false)
            intent.putExtra("contact", currentJsonContact)
            startActivity(intent).apply {

            }
        }

        sendFileButton.setOnClickListener {
            community.sendFileToContact(currentContact, file)

        }

        showFilesButton.setOnClickListener {
            // Todo: сделать показ файлов полученных от контакта
            try {
                Log.d("Demo.time", TimeInformer().getTime().toString())
            } catch (exception: Exception) {
                Log.e("Demo.time", exception.toString())
            }
        }


    }

    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
}