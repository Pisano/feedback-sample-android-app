package co.pisano.android.feedback.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.pisano.android.feedback.screen.home.HomeScreen
import co.pisano.android.feedback.screen.splash.SplashScreen

@Composable
fun FeedbackNavigationApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = FeedbackAppScreens.SplashScreen.name) {
        composable(FeedbackAppScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }

        composable(FeedbackAppScreens.HomeScreen.name) {
            HomeScreen(navController = navController)
        }
    }
}