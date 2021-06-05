package com.example.loadapp.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.loadapp.R
import com.example.loadapp.ui.home.HomeFragment.ButtonState
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var buttonText = "Download"
    private var progress: Float = 0f
    private var valueAnimator = ValueAnimator()
    private val textRect = Rect()

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        if (new == ButtonState.Loading) {
            this.isEnabled = false
            buttonText = "We are Downloading"
            valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                this.addUpdateListener {
                    progress = this.animatedValue as Float
                    invalidate()
                }
                this.repeatMode = ValueAnimator.REVERSE
                this.repeatCount = ValueAnimator.INFINITE
                this.duration = 3000
                this.start()
            }
        } else if (new == ButtonState.Completed) {
            buttonText = "Downloaded"
            valueAnimator.cancel()
            this.isEnabled = true
        }
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingStatusButton, 0, 0)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cornerRadius = 10.0f
        val backgroundWidth = measuredWidth.toFloat()
        val backgroundHeight = measuredHeight.toFloat()

        textPaint.getTextBounds(buttonText, 0, buttonText.length, textRect)
        canvas.drawRoundRect(
            0f,
            0f,
            backgroundWidth,
            backgroundHeight,
            cornerRadius,
            cornerRadius,
            backgroundPaint
        )

        if (buttonState == ButtonState.Loading) {
            var progressVal = progress * measuredWidth.toFloat()
            canvas.drawRoundRect(
                0f,
                0f,
                progressVal,
                backgroundHeight,
                cornerRadius,
                cornerRadius,
                progressPaint
            )

            val arcDiameter = cornerRadius * 3
            val arcRectSize = measuredHeight.toFloat() - paddingBottom.toFloat() - arcDiameter

            progressVal = progress * 360f
            canvas.drawArc(
                paddingStart + arcDiameter,
                paddingTop.toFloat() + arcDiameter,
                arcRectSize,
                arcRectSize,
                0f,
                progressVal,
                true,
                arcPaint
            )
        }
        val centerX = measuredWidth.toFloat() / 2
        val centerY = measuredHeight.toFloat() / 2 - textRect.centerY()

        canvas.drawText(buttonText, centerX, centerY, textPaint)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.textSize = 50.0f
        this.color = Color.WHITE
        this.style = Paint.Style.FILL_AND_STROKE
        this.textAlign = Paint.Align.CENTER
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = ContextCompat.getColor(context, R.color.purple_500)
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = ContextCompat.getColor(context, R.color.purple_700)
    }

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.YELLOW
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minimumWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val width: Int = resolveSizeAndState(minimumWidth, widthMeasureSpec, 1)
        val height: Int = resolveSizeAndState(MeasureSpec.getSize(width), heightMeasureSpec, 0)
        widthSize = width
        heightSize = height
        setMeasuredDimension(width, height)
    }
}