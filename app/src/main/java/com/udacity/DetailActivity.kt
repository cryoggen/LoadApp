package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

const val STATUS = "STATUS"
const val FILE_NAME = "FILE_NAME"

class DetailActivity : AppCompatActivity() {

    private var filename = ""
    private var statusFile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        filename = intent.getSerializableExtra(FILE_NAME) as String
        statusFile = intent.getSerializableExtra(STATUS) as String

        findViewById<TextView>(R.id.fileNameTextView).text = filename
        findViewById<TextView>(R.id.statusTextView).text = statusFile
        findViewById<TextView>(R.id.statusTextView).setTextColor(if (statusFile == "Fail") Color.RED else Color.BLACK)

        notificationManager.cancelNotifications()

        buttonOk.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
