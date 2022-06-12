package co.pisano.android.feedback.util

import co.pisano.android.feedback.ui.theme.*

object AppUtil {
    val backgroundColors = listOf(
        splashColorOne,
        splashColorTwo,
        splashColorThree,
        splashColorFour,
        splashColorFive,
        splashColorSix
    )

    fun changeBackgroundColor(value: Int): Int {
        return if (value == 5) 0 else value + 1
    }
}