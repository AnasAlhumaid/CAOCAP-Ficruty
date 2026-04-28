package sa.gov.ksaa.dal.ui.fragments.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView

@SuppressLint("AppCompatCustomView")
class CropImage : ImageView {
    var downY = 0.0F
    var downX = 0.0F

    constructor(context: Context) : super(context) {
        setOnTouchListener(onMoveImage())
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setOnTouchListener(onMoveImage())
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setOnTouchListener(onMoveImage())
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        setOnTouchListener(onMoveImage())
    }

    internal inner class onMoveImage : View.OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> animate()
                    .x(event.rawX + downX)
                    .y(event.rawY + downY)
                    .setDuration(0).start()
                MotionEvent.ACTION_DOWN -> {
                    downX = x - event.rawX
                    downY = y - event.rawY
                }
                else -> return false
            }

            return true
        }
    }
}