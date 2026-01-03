package co.pisano.android.feedback.util

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import co.pisano.android.feedback.R

object AppUtil {
    @ColorInt
    fun backgroundColors(context: Context): IntArray {
        return intArrayOf(
            ContextCompat.getColor(context, R.color.splashColorOne),
            ContextCompat.getColor(context, R.color.splashColorTwo),
            ContextCompat.getColor(context, R.color.splashColorThree),
            ContextCompat.getColor(context, R.color.splashColorFour),
            ContextCompat.getColor(context, R.color.splashColorFive),
            ContextCompat.getColor(context, R.color.splashColorSix),
        )
    }

    fun changeBackgroundColor(value: Int): Int {
        return if (value == 5) 0 else value + 1
    }
}