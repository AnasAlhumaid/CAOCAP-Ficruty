package sa.gov.ksaa.dal.ui.fragments.wallet


import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import sa.gov.ksaa.dal.R

@SuppressLint("AppCompatCustomView")

class CropCircle : ImageView {

    var downX = 0.0F
    var downY = 0.0F
    var moveX = 0.0F
    var moveY = 0.0F

    constructor(context: Context) : super(context) {
        this.setBackgroundResource(R.drawable.circle)
        setOnTouchListener(onTouch())
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.setBackgroundResource(R.drawable.circle)
        setOnTouchListener(onTouch())
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.setBackgroundResource(R.drawable.circle)
        setOnTouchListener(onTouch())
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        this.setBackgroundResource(R.drawable.circle)
        setOnTouchListener(onTouch())
    }

    internal inner class onTouch : View.OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    moveX = event.x
                    moveY = event.y

                    val r = moveX - downX

                    val layoutParams = layoutParams
                    if (r < 120) {
                        downX = 120f
                        downY = 120f

                        layoutParams.width = downX.toInt()
                        layoutParams.height = downY.toInt()

                        return true
                    } else if (r > 360) {
                        downX = 360f
                        downY = 360f

                        layoutParams.width = downX.toInt()
                        layoutParams.height = downY.toInt()

                        return true
                    } else {
                        layoutParams.width = (downX + r).toInt()
                        layoutParams.height = (downY + r).toInt()

                        downX += r
                        downY += r
                    }
                    setLayoutParams(layoutParams)
                }
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                }
                else -> return false
            }

            return true
        }
    }
}