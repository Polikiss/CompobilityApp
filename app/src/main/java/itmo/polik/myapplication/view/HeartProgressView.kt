package itmo.polik.myapplication.view
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.PathParser
import kotlin.math.min

class HeartProgressView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)
{
    private val heartPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progress: Float = 0f
    private val heartPathData = "M12,21.35l-1.45-1.32C5.4,15.36 2,12.28 2,8.5 2,5.42 4.42,3 7.5,3c1.74,0 3.41,0.81 4.5,2.09C13.09,3.81 14.76,3 16.5,3 19.58,3 22,5.42 22,8.5c0,3.78-3.4,6.86-8.55,11.54L12,21.35z"

    init {
        heartPaint.color = Color.LTGRAY
        heartPaint.style = Paint.Style.STROKE
        heartPaint.strokeWidth = 4f

        fillPaint.color = ContextCompat.getColor(context, android.R.color.holo_red_light)
        fillPaint.style = Paint.Style.FILL
    }

    fun animateProgress(targetProgress: Float) {
        var scaledTargetProgress = targetProgress
        if (targetProgress < 0.35 && targetProgress > 0){
            scaledTargetProgress = (targetProgress + 0.1).toFloat()
        }
        val animator = ValueAnimator.ofFloat(progress, scaledTargetProgress).apply {
            duration = 1000
            interpolator = OvershootInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            println("prgress $progress")

        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val heartPath = getScaledHeartPath()
        val bounds = RectF()
        heartPath.computeBounds(bounds, true) // Получаем реальные границы сердца

        // Отрисовка контура
        canvas.drawPath(heartPath, heartPaint)

        // Расчёт заполнения относительно высоты сердца
        val heartHeight = bounds.height()
        val fillHeight = heartHeight * (1 - progress) // Инвертируем прогресс

        // Клиппинг только в области сердца
        canvas.save()
        canvas.clipRect(
            bounds.left,
            bounds.top + fillHeight, // Начинаем заполнение сверху
            bounds.right,
            bounds.bottom
        )
        canvas.drawPath(heartPath, fillPaint)
        canvas.restore()
    }

    private fun getScaledHeartPath(): Path {
        val originalPath = createHeartPath()
        val matrix = Matrix()

        // Масштабирование пути под размер View
        val scale = min(width / 24f, height / 24f)
        matrix.postScale(scale, scale)
        matrix.postTranslate(width / 2f - 12f * scale, height / 2f - 12f * scale)

        originalPath.transform(matrix)
        return originalPath
    }

    private fun createHeartPath(): Path {
        return PathParser.createPathFromPathData(heartPathData)
    }

    fun setProgress(progress: Float) {
        this.progress = progress.coerceIn(0f, 1f)
        invalidate()
    }
}