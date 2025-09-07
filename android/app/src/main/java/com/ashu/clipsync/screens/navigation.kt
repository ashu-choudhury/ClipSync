package com.ashu.clipsync.screens

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashu.clipsync.AppSession
import com.ashu.clipsync.services.ClipboardSync

@Composable
fun AppNav(navController: NavHostController){
    NavHost(navController = navController, startDestination = Routes.SignIn) {
        composable(Routes.Home) {
            HomeScreen(navController)
        }

        composable(Routes.SignIn) {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                if (AppSession.user != null) {
                    navController.navigate(Routes.Home)
                }
            }
            SignInScreen {
                val intent = Intent(context, ClipboardSync::class.java)
                context.startForegroundService(intent)
                if (AppSession.user != null) navController.navigate(Routes.Home)
            }
        }
    }

}

