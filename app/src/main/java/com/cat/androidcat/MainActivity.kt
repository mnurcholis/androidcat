package com.cat.androidcat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cat.androidcat.ui.navigation.AppNavHost
import com.cat.androidcat.ui.theme.BackgroundLight
import com.cat.androidcat.ui.theme.CATAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CATAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundLight
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}
