package com.example.pokemonquiz

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.WHITE
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        insetsController.isAppearanceLightNavigationBars = true
        setContent {
            PokemonQuizTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
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
