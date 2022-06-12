package co.pisano.android.feedback.util

import android.content.Context
import co.pisano.feedback.data.helper.ActionListener
import co.pisano.feedback.managers.PisanoSDK
import co.pisano.feedback.managers.PisanoSDKManager

object FeedbackUtil {
    var flowId: String? = null

    fun initFeedbackSDK(context: Context, actionListener: ActionListener) {
        val manager = PisanoSDKManager.Builder(context)
            .setApplicationId("")
            .setAccessKey("")
            .setApiUrl("")
            .setFeedbackUrl("")
            .setEventUrl("")
            .setCloseStatusCallback(actionListener)
            .build()

        PisanoSDK.init(manager)
    }
}