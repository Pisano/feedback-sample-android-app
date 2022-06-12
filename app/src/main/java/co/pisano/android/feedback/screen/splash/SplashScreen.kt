package co.pisano.android.feedback.screen.splash

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import co.pisano.android.feedback.component.ColorBackgroundView
import co.pisano.android.feedback.navigation.FeedbackAppScreens
import co.pisano.android.feedback.util.AppUtil.changeBackgroundColor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SplashScreen(navController: NavController) {
    val indexState = remember {
        mutableStateOf(0)
    }

    val countState = remember {
        mutableStateOf(0)
    }

    if (indexState.value == 5) {
        countState.value = countState.value + 1
    }

    if (countState.value == 1) {
        navController.navigate(FeedbackAppScreens.HomeScreen.name) {
            popUpTo(FeedbackAppScreens.SplashScreen.name) {
                inclusive = true
            }
        }
    }

    ColorBackgroundView(
        colorIndex = indexState.value,
        verticalArrangement = Arrangement.Center
    ) {

    }

    GlobalScope.launch {
        delay(500L)

        if (indexState.value < 5) {
            indexState.value = changeBackgroundColor(indexState.value)
        }
    }
}