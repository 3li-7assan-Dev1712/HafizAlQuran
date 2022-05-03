package app.netlify.dev_ali_hassan.hafizalquran.util

import android.os.CountDownTimer

abstract class CountDownTimerUtil (var mMillisInFuture: Long, var mInterval: Long){

    private lateinit var countDownTimer: CountDownTimer
    private var remainingTime: Long = 0
    private var isTimerPaused: Boolean = true

    init {
        this.remainingTime = mMillisInFuture
    }

    @Synchronized
    fun start() {
        if (isTimerPaused) {
            countDownTimer = object : CountDownTimer(remainingTime, mInterval) {
                override fun onTick(p0: Long) {
                    remainingTime = p0
                    onTimerTick(p0)
                }

                override fun onFinish() {
                     onTimerFinish()
                }
            }.apply {
                start()
            }
            isTimerPaused = false

        }
    }

    fun pause() {
        if (!isTimerPaused) {
            countDownTimer.cancel()
        }
        isTimerPaused = true

    }

    fun restart() {
        countDownTimer.cancel()
        remainingTime = mMillisInFuture
        isTimerPaused = true
    }


    abstract fun onTimerTick(millisUntilFinished: Long)
    abstract fun onTimerFinish()


}