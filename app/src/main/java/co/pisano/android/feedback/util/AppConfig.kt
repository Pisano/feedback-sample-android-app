package co.pisano.android.feedback.util

import co.pisano.android.feedback.BuildConfig

data class AppConfig(
    val appId: String,
    val accessKey: String,
    val code: String,
    val apiUrl: String,
    val feedbackUrl: String,
    val eventUrl: String,
) {
    val isValid: Boolean
        get() = appId.isNotBlank() &&
            accessKey.isNotBlank() &&
            code.isNotBlank() &&
            apiUrl.isNotBlank() &&
            feedbackUrl.isNotBlank()

    companion object {
        fun fromBuildConfig(): AppConfig = AppConfig(
            appId = BuildConfig.PISANO_APP_ID,
            accessKey = BuildConfig.PISANO_ACCESS_KEY,
            code = BuildConfig.PISANO_CODE,
            apiUrl = BuildConfig.PISANO_API_URL,
            feedbackUrl = BuildConfig.PISANO_FEEDBACK_URL,
            eventUrl = BuildConfig.PISANO_EVENT_URL,
        )
    }
}


