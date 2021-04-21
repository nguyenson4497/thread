package com.example.threadkotlin

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener,
    View.OnLongClickListener {
    private val handlerThread: HandlerThread = HandlerThread("HandlerThread")
    private lateinit var mHandler: Handler
    private lateinit var countDown: Runnable
    private lateinit var up: Runnable
    private lateinit var down: Runnable
    private var number: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handlerThread.start()
        mHandler = Handler(handlerThread.looper)
        initViews()
        countDown()
        upWhileHoldPress()
        downWhileHoldPress()
    }

    private fun initViews() {
        btn_plus.setOnClickListener(this)
        btn_plus.setOnLongClickListener(this)
        btn_minus.setOnClickListener(this)
        btn_minus.setOnLongClickListener(this)
        layout_number.setOnTouchListener(this)
    }

    private fun countDown() {
        countDown = Runnable {
            if (number > 0) {
                number--
                this.runOnUiThread {
                    tv_number.text = number.toString()
                }
                mHandler.postDelayed(countDown, 100)
            } else if (number < 0) {
                number++
                this.runOnUiThread {
                    tv_number.text = number.toString()
                }
                mHandler.postDelayed(countDown, 100)
            } else {
                mHandler.removeCallbacks(countDown)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            btn_plus -> {
                increaseNumber()
            }
            btn_minus -> {
                decreaseNumber()
            }
        }
    }

    private fun increaseNumber() {
        mHandler.removeCallbacks(countDown)
        number++
        tv_number.text = number.toString()
        changeNumberColor(tv_number, number)
        this.runOnUiThread {
            tv_number.text
        }
        mHandler.postDelayed(countDown, 2000)
    }

    private fun decreaseNumber() {
        mHandler.removeCallbacks(countDown)
        number--
        tv_number.text = number.toString()
        changeNumberColor(tv_number, number)
        this.runOnUiThread {
            tv_number.text
        }
        mHandler.postDelayed(countDown, 2000)
    }

    override fun onLongClick(view: View?): Boolean {
        when (view) {
            btn_plus -> {
                mHandler.post(up)
            }
            btn_minus -> {
                mHandler.post(down)
            }
        }
        return false
    }

    private fun upWhileHoldPress() {
        up = Runnable {
            mHandler.removeCallbacks(countDown)
            number++
            when {
                btn_plus.isPressed -> {
                    mHandler.postDelayed(up, 100)
                    changeNumberColor(tv_number, number)
                    this.runOnUiThread {
                        tv_number.text = number.toString()
                    }
                }
                !btn_plus.isPressed -> {
                    mHandler.removeCallbacks(up)
                    mHandler.postDelayed(countDown, 2000)
                }
            }
        }
    }

    private fun downWhileHoldPress() {
        down = Runnable {
            mHandler.removeCallbacks(countDown)
            number--
            when {
                btn_minus.isPressed -> {
                    mHandler.postDelayed(down, 100)
                    changeNumberColor(tv_number, number)
                    this.runOnUiThread {
                        tv_number.text = number.toString()
                    }
                }
                !btn_minus.isPressed -> {
                    mHandler.removeCallbacks(down)
                    mHandler.postDelayed(countDown, 2000)
                }
            }
        }
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        if (view == layout_number && motionEvent?.action == MotionEvent.ACTION_MOVE) {
            when {
                motionEvent.y < 0.5 -> {
                    increaseNumber()
                }
                motionEvent.y > 0.5 -> {
                    decreaseNumber()
                }
            }
        }
        return true
    }

    private fun changeNumberColor(textView: TextView?, number: Int) {
        var random = Random()
        var color: Int = Color.argb(
            255,
            random.nextInt(256), random.nextInt(256),
            random.nextInt(256)
        )
        this.runOnUiThread {
            if (number % 50 == 0) {
                textView?.setTextColor(color)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quit()
    }
}