package co.pisano.android.feedback.util

import android.content.Context
import co.pisano.feedback.data.helper.ActionListener
import co.pisano.feedback.data.helper.PisanoActions
import co.pisano.feedback.managers.PisanoSDK
import co.pisano.feedback.managers.PisanoSDKManager

object PisanoSdkBootstrapper {
    @Volatile
    private var initialized = false

    fun ensureInitialized(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return

            val config = AppConfig.fromBuildConfig()
            if (!config.isValid) {
                PisanoSdkState.setInitialized(false)
                PisanoSdkState.setError(SdkError.ConfigMissing)
                AppLogger.w("SDK init skipped: config missing (values are not logged).")
                initialized = false
                return
            }

            AppLogger.i("SDK init start")

            val manager = PisanoSDKManager.Builder(context)
                .setApplicationId(config.appId)
                .setAccessKey(config.accessKey)
                .setApiUrl(config.apiUrl)
                .setFeedbackUrl(config.feedbackUrl)
                .apply {
                    if (config.eventUrl.isNotBlank()) {
                        setEventUrl(config.eventUrl)
                    }
                }
                .setCloseStatusCallback(object : ActionListener {
                    override fun action(action: PisanoActions) {
                        PisanoSdkState.setLastAction(action)
                        AppLogger.d("SDK action: $action")
                    }
                })
                .build()

            try {
                PisanoSDK.init(manager)
                PisanoSdkState.setInitialized(true)
                PisanoSdkState.setError(null)
                initialized = true
                AppLogger.i("SDK init end (success)")
            } catch (t: Throwable) {
                PisanoSdkState.setInitialized(false)
                PisanoSdkState.setError(SdkError.InitFailed)
                initialized = false
                AppLogger.e("SDK init end (failed)", t)
            }
        }
    }
}


