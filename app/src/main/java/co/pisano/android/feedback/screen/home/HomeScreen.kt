package co.pisano.android.feedback.screen.home

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.pisano.android.feedback.BuildConfig
import co.pisano.android.feedback.R
import co.pisano.android.feedback.component.ColorBackgroundView
import co.pisano.android.feedback.component.EmailInput
import co.pisano.android.feedback.component.InputField
import co.pisano.android.feedback.component.PhoneNumberInput
import co.pisano.android.feedback.util.AppUtil
import co.pisano.android.feedback.util.FeedbackUtil
import co.pisano.feedback.data.helper.ActionListener
import co.pisano.feedback.data.helper.PisanoActions
import co.pisano.feedback.data.model.PisanoCustomer
import co.pisano.feedback.managers.PisanoSDK
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(navController: NavController) {
    val indexState = remember {
        mutableStateOf(0)
    }

    ColorBackgroundView(verticalArrangement = Arrangement.Top, indexState.value) {
        Surface(
            Modifier
                .padding(top = 30.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = Color.White
        ) {
            HomeContent()
        }
    }

    GlobalScope.launch {
        delay(500L)
        indexState.value = AppUtil.changeBackgroundColor(indexState.value)
    }
}

@Composable
fun HomeContent() {
    val actionState = rememberSaveable {
        mutableStateOf("")
    }

    FeedbackUtil.initFeedbackSDK(LocalContext.current, object : ActionListener {
        override fun action(action: PisanoActions) {
            actionState.value = when (action) {
                PisanoActions.CLOSED -> "Closed"
                PisanoActions.OPENED -> "Opened"
                PisanoActions.OUTSIDE -> "Outside"
                PisanoActions.SEND_FEEDBACK -> "Sent Feedback"
                PisanoActions.DISPLAY_ONCE -> "Survey won't be shown due to the customer saw it before."
                PisanoActions.PREVENT_MULTIPLE_FEEDBACK -> "Survey won't be shown due to customer already submitted a feedback in a given time period."
                else -> "Unknown"
            }

            Log.d("ActionState", "action: ${actionState.value}")
        }
    })

    val nameState = rememberSaveable {
        mutableStateOf("")
    }

    val emailState = rememberSaveable {
        mutableStateOf("")
    }

    val phoneNumberState = rememberSaveable {
        mutableStateOf("")
    }

    val isClickState = rememberSaveable {
        mutableStateOf(false)
    }

    val isEmailErrorState = rememberSaveable(emailState.value, isClickState.value) {
        mutableStateOf(!isValidEmailAddress(emailState.value) && isClickState.value && emailState.value.isNotEmpty())
    }

    val isValidState = rememberSaveable(emailState.value) {
        mutableStateOf(isValidEmailAddress(emailState.value) || emailState.value.isEmpty())
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        InputField(
            modifier = Modifier,
            valueState = nameState,
            labelId = stringResource(R.string.name),
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            leadingImageVector = Icons.Default.Person,
            enabled = true,
            onAction = KeyboardActions {

            }
        )

        EmailInput(
            modifier = Modifier.padding(top = 5.dp),
            emailState = emailState,
            onAction = KeyboardActions {

            },
            isError = isEmailErrorState.value
        )

        if (isEmailErrorState.value) {
            Text(
                text = stringResource(id = R.string.email_address_is_not_valid),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        PhoneNumberInput(
            modifier = Modifier.padding(top = 5.dp),
            phoneNumberState = phoneNumberState,
            onAction = KeyboardActions {

            })

        if (BuildConfig.DEBUG) {
            Text(
                text = "Action Status: ${actionState.value}",
                modifier = Modifier.padding(top = 50.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                onClick = {
                    isClickState.value = true

                    if (isValidState.value) {
                        PisanoSDK.show(
                            flowId = FeedbackUtil.flowId ?: "",
                            pisanoCustomer = PisanoCustomer(
                                name = nameState.value,
                                email = emailState.value,
                                phoneNumber = phoneNumberState.value
                            )
                        )
                    }
                }) {
                Text(text = stringResource(id = R.string.get_feedback))
            }
        }
    }
}

fun isValidEmailAddress(emailAddress: CharSequence?) =
    !TextUtils.isEmpty(emailAddress) && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()

