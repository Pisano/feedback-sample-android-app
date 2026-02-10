package co.pisano.android.feedback.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.pisano.feedback.data.helper.PisanoActions

object PisanoSdkState {
    private val _isInitialized = MutableLiveData(false)
    val isInitialized: LiveData<Boolean> = _isInitialized

    private val _lastAction = MutableLiveData<PisanoActions?>(null)
    val lastAction: LiveData<PisanoActions?> = _lastAction

    private val _lastError = MutableLiveData<SdkError?>(null)
    val lastError: LiveData<SdkError?> = _lastError

    fun setInitialized(value: Boolean) {
        _isInitialized.value = value
    }

    fun setLastAction(action: PisanoActions?) {
        _lastAction.value = action
    }

    fun setError(error: SdkError?) {
        _lastError.value = error
    }
}

sealed class SdkError {
    object ConfigMissing : SdkError()
    object InitFailed : SdkError()
}


