package com.udacity

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val animationDuration = 1000L
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()
    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        calculateTextPoint()
        calculateCircleFrame()
        invalidate()
    }

    private val circlePaint: Paint = Paint()
    private lateinit var circleFrame: RectF
    private var progress: Float = 0f
    private var circleSize: Float = 100f
    private var viewBackgroundColor: Int =
        ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private var viewBackgroundColorDark =
        ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    private var colorCircle = ResourcesCompat.getColor(resources, R.color.colorAccent, null)

    private val rectanglePaint: Paint = Paint()


    private var titleColor: Int =
        ResourcesCompat.getColor(resources, R.color.white, null)
    private var titleSize: Float = 25f
    private val textPaint: Paint = Paint()
    private var textPoint: PointF = PointF()
    private var textBounds = Rect()
    private var titleNormal = context.getString(R.string.button_normal)
    private var titleLoading = context.getString(R.string.button_loading)


    init {
        initData(attrs)
    }

    private fun setState(state: ButtonState) {
        if (state is ButtonState.Completed) {
            progress = 0f
        }
        buttonState = state
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        circleSize = h * 0.4f
        calculateTextPoint()
        calculateCircleFrame()
    }

    override fun performClick(): Boolean {
        setState(ButtonState.Clicked)
        setProgress(1f)
        return super.performClick()
    }

    private fun initData(attrs: AttributeSet? = null) {
        attrs?.let { attributeSet ->
            context.withStyledAttributes(attributeSet, R.styleable.LoadingButton) {
                getColor(R.styleable.LoadingButton_ld_background_normal, 0).takeIf { it != 0 }
                    ?.let { color ->
                        viewBackgroundColor = color
                    }
                getColor(R.styleable.LoadingButton_ld_background_loading, 0).takeIf { it != 0 }
                    ?.let { color ->
                        viewBackgroundColorDark = color
                    }
                getColor(R.styleable.LoadingButton_ld_highlight_color, 0).takeIf { it != 0 }
                    ?.let { color ->
                        colorCircle = color
                    }
                getColor(R.styleable.LoadingButton_ld_title_color, 0).takeIf { it != 0 }
                    ?.let { color ->
                        titleColor = color
                    }
                getString(R.styleable.LoadingButton_ld_title_normal)?.let {
                    titleNormal = it
                }
                getString(R.styleable.LoadingButton_ld_title_loading)?.let {
                    titleLoading = it
                }
                titleSize = getDimension(R.styleable.LoadingButton_ld_title_size, 25f)
            }
        }

        circleFrame = RectF(0f, 0f, circleSize, circleSize)
        circlePaint.isAntiAlias = true
        circlePaint.color = colorCircle
        circlePaint.style = Paint.Style.FILL

        rectanglePaint.isAntiAlias = true
        rectanglePaint.color = viewBackgroundColorDark
        rectanglePaint.style = Paint.Style.FILL

        textPaint.isAntiAlias = true
        textPaint.color = titleColor
        textPaint.style = Paint.Style.FILL
        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = titleSize
    }

    private fun setProgress(progressValue: Float) {
        setState(ButtonState.Loading)
        val currentProgress = this.progress
        if (valueAnimator.isRunning) {
            valueAnimator.cancel()
        }
        valueAnimator.apply {
            setValues(
                PropertyValuesHolder.ofFloat(
                    "percent",
                    currentProgress, progressValue
                )
            )
            duration = animationDuration
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener {
                val newValue = it.getAnimatedValue("percent") as Float
                progress = newValue
                if (progress >= 1f) {
                    this@LoadingButton.setState(ButtonState.Completed)
                }
                invalidate()
            }
        }
        valueAnimator.start()
    }

    private fun getTitle(): String {
        return when (buttonState) {
            is ButtonState.Loading -> titleLoading
            is ButtonState.Completed -> titleNormal
            is ButtonState.Clicked -> titleNormal
        }
    }

    private fun moveCircleToPoint(pointF: PointF) {
        circleFrame.left = (pointF.x - circleSize / 2)
        circleFrame.top = (pointF.y - circleSize / 2)
        circleFrame.right = (pointF.x + circleSize / 2)
        circleFrame.bottom = (pointF.y + circleSize / 2)
    }

    private fun calculateTextPoint() {
        textPaint.getTextBounds(getTitle(), 0, getTitle().length, textBounds)
        val textWidth = textBounds.width()
        val textHeight = textBounds.height()
        val remainSpaceX = widthSize - textWidth
        val remainSpaceY = heightSize - textHeight
        textPoint.x = remainSpaceX / 2f
        textPoint.y = remainSpaceY.toFloat() / 2f + textHeight
    }

    private fun calculateCircleFrame() {
        val circleXAfterText = textPoint.x + textBounds.width() + (circleSize * 0.8f)
        moveCircleToPoint(PointF(circleXAfterText, heightSize / 2f))
    }

    private fun drawCircle(canvas: Canvas) {
        canvas.drawArc(
            circleFrame,
            0f,
            progress * 360,
            true,
            circlePaint
        )
    }

    private fun drawRectangle(canvas: Canvas) {
        canvas.drawRect(0f, 0f, progress * widthSize, heightSize.toFloat(), rectanglePaint)
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText(getTitle(), textPoint.x, textPoint.y, textPaint)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            canvas.drawColor(viewBackgroundColor)
            drawRectangle(it)
            drawCircle(it)
            drawText(it)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}