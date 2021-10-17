package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //the width and height of the rectangle that creates the loading animation
    private var widthSizeRect = 0
    private var heightSizeRect = 0

    //parameter to animate the circle
    private var sweepAngle = 0f

    private lateinit var rect: Rect

    private val backgroundColor =
        ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)

    private val rectangleColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)

    private val circleColor = ResourcesCompat.getColor(resources, R.color.colorAccent, null)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.NORMAL)
    }

    private var textButton = resources.getString(R.string.button_name)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Clicked) { _, _, new ->
        when (new) {
            ButtonState.Completed -> {
                completedAnimation()
            }
            ButtonState.Clicked -> {
                clickedAnimation()
            }
        }
    }

    private var buttonProgress: Int by Delegates.observable(0) { _, old, new ->
        //create animation depending on the progress of the file upload
        if (old <= new) loadingAnimation(old, new)
    }

    init {
        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(backgroundColor)
        paint.color = rectangleColor
        canvas.drawRect(rect, paint)
        paint.color = Color.WHITE
        canvas.drawText(
            textButton,
            (widthSizeRect / 2).toFloat(),
            ((heightSizeRect / 2).toFloat() - ((paint.descent() + paint.ascent()) / 2)),
            paint
        )//((paint.descent() + paint.ascent()) / 2) is the distance from the baseline to the center.
        paint.color = circleColor
        paint.strokeWidth = 10F

        canvas.drawArc(
            (widthSizeRect / 2).toFloat() + 200,
            (heightSizeRect / 2).toFloat() - 25,
            (widthSizeRect / 2).toFloat() + 250,
            (heightSizeRect / 2).toFloat() + 25,
            0f,
            sweepAngle,
            true,
            paint
        )
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSizeRect = w
        heightSizeRect = h

        //creates a rectangle equal to the size of the button
        rect = Rect(0, 0, widthSizeRect, heightSizeRect)

        setMeasuredDimension(w, h)
    }

    private fun clickedAnimation() {
        textButton = resources.getString(R.string.button_loading)
        isEnabled = false
    }

    private fun completedAnimation() {
        rect.left = 0
        sweepAngle = 0f
        isEnabled = true
        textButton = resources.getString(R.string.button_name)
        invalidate()
    }

    private fun loadingAnimation(old: Int, new: Int) {

        val valuesStartAnimationRect = (widthSizeRect / 100) * old
        val valuesEndAnimationRect = if (new == 100) widthSizeRect else (widthSizeRect / 100) * new
        val animatorButton = ValueAnimator.ofInt(
            valuesStartAnimationRect,
            valuesEndAnimationRect
        )

        animatorButton.duration = 1000

        animatorButton.addUpdateListener { animation ->
            val value = animation.animatedValue
            rect.left = value as Int
            invalidate()
        }

        val valuesStartAnimationCircle = (360 / 100) * old.toFloat()
        val valuesEndAnimationCircle = if (new == 100) 360F else (360 / 100) * new.toFloat()

        val animatorCircle =
            ValueAnimator.ofFloat(valuesStartAnimationCircle, valuesEndAnimationCircle)
        animatorCircle.duration = 1000
        animatorCircle.addUpdateListener { animation ->
            sweepAngle = animation.animatedValue as Float
        }

        //if the download bar has reached the end, then we return the button view to the original
        if (new == 100) animatorButton.resetButtonDisplay()

        animatorCircle.start()
        animatorButton.start()
    }

    private fun ValueAnimator.resetButtonDisplay() {

        addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                buttonState = ButtonState.Completed
            }
        })
    }

    fun changeButtonState(buttonState: ButtonState) {

        this.buttonState = buttonState
    }

    fun changeButtonProgress(buttonProgress: Long) {
        this.buttonProgress = buttonProgress.toInt()
    }



}