package com.besmart.myvisualizer

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

open class MainActivity : AppCompatActivity() {

    private val AUDIO_PERMISSION_REQUEST_CODE = 102

    private val WRITE_EXTERNAL_STORAGE_PERMS = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(WRITE_EXTERNAL_STORAGE_PERMS, AUDIO_PERMISSION_REQUEST_CODE)
        } else {
            setPlayer()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("cclin", "页面销毁")
        barVisualizer.release()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            AUDIO_PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                setPlayer()
            } else {
                finish()
            }
        }
    }

    private lateinit var barVisualizer: BarVisualizer

    private fun setPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.red)
        mediaPlayer?.isLooping = false

        barVisualizer = findViewById(R.id.visualizer)

        // set custom color to the line.

        // set custom color to the line.
        barVisualizer.setColor(ContextCompat.getColor(this, R.color.custom))

        // define custom number of bars you want in the visualizer between (10 - 256).

        // define custom number of bars you want in the visualizer between (10 - 256).
        barVisualizer.setDensity(50F)

        // Set your media player to the visualizer.

        // Set your media player to the visualizer.
        mediaPlayer?.audioSessionId?.let { barVisualizer.setPlayer(it) }

        val playPause: ImageButton = findViewById(R.id.ib_play_pause)
        val replay: ImageButton = findViewById(R.id.ib_replay)
        playPause.setOnClickListener(this::onClick)
        replay.setOnClickListener(this::onClick)
    }

    private fun onClick(view: View) {
        when(view.id){
            R.id.ib_play_pause -> {
                val btnPlayPause = view as ImageButton
                if (mediaPlayer != null) {
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                        btnPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_play_red_48dp
                            )
                        )
                    } else {
                        mediaPlayer?.start()
                        btnPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_pause_red_48dp
                            )
                        )
                    }
                }
            }
            R.id.ib_replay -> {
                mediaPlayer?.seekTo(0)
            }
            else ->{

            }
        }

    }

}