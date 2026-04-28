package sa.gov.ksaa.dal.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.core.view.isVisible
import sa.gov.ksaa.dal.data.webservices.newDal.DalWebService

class ViewUlils {
    companion object {
        private var _instance: ViewUlils? = null
        fun getInstance(): ViewUlils {
            if (_instance == null)
                _instance = ViewUlils()
            return _instance!!
        }
    }


    fun togleVisisbilityAndGg(targetView: View, label: View) {
        if (targetView.isVisible) {
//            targetView.visibility = View.GONE
            collapse(targetView)
//            label.setBackgroundColor(resources.getColor(android.R.color.transparent))
            label.isSelected = false
        } else {
//            targetView.visibility = View.VISIBLE
            expand(targetView)
//            label.setBackgroundColor(resources.getColor(R.color.light_gray))
            label.isSelected = true
        }
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun expand(v: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

}