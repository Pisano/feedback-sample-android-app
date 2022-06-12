package co.pisano.android.feedback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.pisano.android.feedback.navigation.FeedbackNavigationApp
import co.pisano.android.feedback.ui.theme.FeedbackAppTheme
import co.pisano.android.feedback.util.FeedbackUtil.flowId

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseDeepLink()
        setContent {
            FeedbackAppTheme {
                FeedbackApp()
            }
        }
    }

    private fun parseDeepLink() {
        intent.data?.let { data ->
            if (data.host == "show") {
                flowId = intent.data?.getQueryParameter("flow_id")
            }
        }
    }
}

@Composable
fun FeedbackApp() {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FeedbackNavigationApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedbackAppTheme {
        FeedbackApp()
    }
}