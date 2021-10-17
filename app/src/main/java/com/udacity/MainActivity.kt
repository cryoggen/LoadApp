package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 0

    //flag whether to continue loading
    private var downloading = false

    private var downloadStatus = ""
    private var downloadFileName = ""

    private var bytesDownloaded = 0L
    private var bytesTotal = 0L

    private lateinit var cursor: Cursor
    private lateinit var query: DownloadManager.Query
    private var progressProc = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification)
        )

        custom_button.setOnClickListener {
            custom_button.changeButtonState(ButtonState.Clicked)
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButtonGlide -> {
                    downloadFileName = getString(R.string.glide_radio_button_name)
                    download("https://github.com/bumptech/glide")
                }
                R.id.radioButtonLoadingApp -> {
                    downloadFileName = getString(R.string.load_app_radio_button_name)
                    download("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter")
                }
                R.id.radioButtonRetrofit -> {
                    downloadFileName = getString(R.string.retrofit_radio_button_name)
                    download("https://github.com/square/retrofit")
                }
                else -> {
                    custom_button.changeButtonProgress(0)
                    custom_button.changeButtonProgress(100)
                    Toast.makeText(
                        applicationContext, R.string.radio_button_not_change,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        downloading = true
        progress()
    }

    @SuppressLint("ResourceType")
    private fun progress() {

        //the animation is drawn based on the number of bytes loaded. everything is done in a separate thread
        thread {
            var chekTimeDownloading = 0
            while (downloading) {
                chekStatusDownload()
                chekTimeDownloading++

                //if the download is a very long download, then we consider that the download failed
                if (chekTimeDownloading == 1000) {

                    runOnUiThread {
                        startNotification()

                        //bring the loading bar animation to the end
                        custom_button.changeButtonProgress(100)
                    }
                }
                progressProc = ((bytesDownloaded * 100L) / bytesTotal)
                runOnUiThread {
                    if ((progressProc >= 0) and (downloading)) {
                        custom_button.changeButtonProgress(progressProc)
                    }
                }
                cursor.close()
            }
        }
    }

    //checks if file download has finished
    private fun chekStatusDownload() {
        query = DownloadManager.Query()
        query.setFilterById(downloadID)

        cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            bytesDownloaded =
                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

            bytesTotal =
                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                downloadStatus = getString(R.string.success)

                runOnUiThread {
                    startNotification()
                    custom_button.changeButtonProgress(100)

                }
            } else {
                downloadStatus = getString(R.string.fail)
            }
        }
    }

    private fun startNotification() {

        //stops the thread
        downloading = false

        val notificationManager =
            ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager

        notificationManager.sendNotification(
            getString(R.string.notification_description),
            applicationContext,
            downloadStatus,
            downloadFileName
        )
    }

    //creates a notification channel
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // change importance
                NotificationManager.IMPORTANCE_LOW
            )// disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }

    }
}



