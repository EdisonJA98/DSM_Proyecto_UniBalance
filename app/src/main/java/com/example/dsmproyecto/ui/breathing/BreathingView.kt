package com.example.dsmproyecto.ui.breathing

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.min

class BreathingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val outerCircleColor = Color.parseColor("#80CBC4")
    private val middleCircleColor = Color.parseColor("#FF8A65")
    private val innerCircleColor = Color.parseColor("#00897B")

    private val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = outerCircleColor
        style = Paint.Style.FILL
    }

    private val middlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = middleCircleColor
        style = Paint.Style.STROKE
        strokeWidth = 22f
    }

    private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = innerCircleColor
        style = Paint.Style.FILL
    }

    private var breathingScale = 1f
    private var baseRadius = 0f

    private var animator: ValueAnimator? = null
    private var breathCycleDuration = 8000L

    // 🔥 Nuevo: mantener estado correcto
    private var isPaused = false
    private var isRunning = false


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        baseRadius = min(w, h) * 0.25f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f

        val rOuter = baseRadius * 1.45f * breathingScale
        val rMiddle = baseRadius * 1.20f * breathingScale
        val rInner = baseRadius * breathingScale

        canvas.drawCircle(cx, cy, rOuter, outerPaint)
        canvas.drawCircle(cx, cy, rMiddle, middlePaint)
        canvas.drawCircle(cx, cy, rInner, innerPaint)
    }


    // 🚀 INICIAR O REANUDAR
    fun startBreathing() {
        when {
            // REANUDAR
            isPaused && animator?.isPaused == true -> {
                animator?.resume()
                isPaused = false
                isRunning = true
            }

            // INICIAR desde cero
            !isRunning -> {
                animator = ValueAnimator.ofFloat(1f, 1.25f, 1f).apply {
                    duration = breathCycleDuration
                    repeatCount = ValueAnimator.INFINITE
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener {
                        breathingScale = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }
                isRunning = true
            }
        }
    }

    // ⏸ PAUSAR SIN REINICIAR
    fun pauseBreathing() {
        if (isRunning && animator?.isRunning == true) {
            animator?.pause()
            isPaused = true
            isRunning = false
        }
    }

    // 🛑 DETENER Y REINICIAR COMPLETO (cuando termina el ejercicio)
    fun stopBreathing() {
        animator?.cancel()
        animator = null
        breathingScale = 1f
        isPaused = false
        isRunning = false
        invalidate()
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopBreathing()
    }
}
