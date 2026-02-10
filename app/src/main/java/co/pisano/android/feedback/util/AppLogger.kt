package co.pisano.android.feedback.util

import android.util.Log
import co.pisano.android.feedback.BuildConfig

object AppLogger {
    private const val TAG = "PisanoSample"

    fun d(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, message)
    }

    fun i(message: String) {
        if (BuildConfig.DEBUG) Log.i(TAG, message)
    }

    fun w(message: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.w(TAG, message, tr)
    }

    fun e(message: String, tr: Throwable? = null) {
        Log.e(TAG, message, tr)
    }

    fun mask(value: String, visibleTail: Int = 4): String {
        if (value.isBlank()) return "(empty)"
        if (value.length <= visibleTail) return "****"
        val tail = value.takeLast(visibleTail)
        return "****$tail"
    }
}


