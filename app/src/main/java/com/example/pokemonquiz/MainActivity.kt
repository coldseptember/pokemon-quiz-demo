package com.example.pokemonquiz

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokemonquiz.ui.screens.detail.DetailScreen
import com.example.pokemonquiz.ui.screens.search.SearchScreen
import com.example.pokemonquiz.ui.screens.splash.SplashScreen
import com.example.pokemonquiz.ui.theme.PokemonQuizTheme
import dagger.hilt.android.AndroidEntryPoint

private const val PREFS_NAME = "app_prefs"
private const val KEY_SPLASH_SHOWN = "splash_shown"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read once: skip splash if already shown before
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val splashShown = prefs.getBoolean(KEY_SPLASH_SHOWN, false)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.WHITE
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        insetsController.isAppearanceLightNavigationBars = true
        setContent {
            PokemonQuizTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val startDest = remember(splashShown) {
                    if (splashShown) "search" else "splash"
                }

                NavHost(
                    navController = navController,
                    startDestination = startDest,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
                                // Mark splash as shown so next launch skips it
                                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                    .edit()
                                    .putBoolean(KEY_SPLASH_SHOWN, true)
                                    .apply()
                                navController.navigate("search") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("search") {
                        SearchScreen(navController = navController)
                    }

                    composable(
                        "detail/{speciesId}",
                        arguments = listOf(navArgument("speciesId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val speciesId = backStackEntry.arguments?.getInt("speciesId") ?: 0
                        DetailScreen(navController = navController)
                    }
                }
            }
        }
    }
}
