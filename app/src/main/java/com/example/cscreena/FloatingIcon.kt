package com.example.cscreena

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.view.*
import android.widget.ImageButton
import android.widget.MediaController
import com.google.android.material.internal.ContextUtils.getActivity

class FloatingIcon :Service(){
    private lateinit var floatView: ViewGroup
    private lateinit var floatWindowLayoutParams: WindowManager.LayoutParams
    private var LAYOUT_TYPE: Int? = null
    private lateinit var windowManager: WindowManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val metrics = applicationContext.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatView = inflater.inflate(R.layout.floating_layout, null) as ViewGroup

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        else LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST

        floatWindowLayoutParams = WindowManager.LayoutParams(
            (width * 0.12f).toInt(),
            (height * 0.08f).toInt(),
            LAYOUT_TYPE!!,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        floatWindowLayoutParams.gravity = Gravity.START
        floatWindowLayoutParams.x = 0
        floatWindowLayoutParams.y = 0

        windowManager.addView(floatView, floatWindowLayoutParams)
        floatView.setOnClickListener() {
            stopSelf()
            windowManager.removeView(floatView)
            val back = Intent(this@FloatingIcon, MainActivity::class.java)
            back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(back)
        }
        floatView.setOnTouchListener(object : View.OnTouchListener{
            val updatelocate = floatWindowLayoutParams
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = updatelocate.x.toDouble()
                        y = updatelocate.y.toDouble()
                        px = event.rawX.toDouble()
                        py = event.rawY.toDouble()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        updatelocate.x = (x + event.rawX - px).toInt()
                        updatelocate.y = (y + event.rawY - py).toInt()
                        windowManager.updateViewLayout(floatView, updatelocate)
                    }
                }
                return false
            }

        })
    }
}