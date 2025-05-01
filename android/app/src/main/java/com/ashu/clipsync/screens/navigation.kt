package com.ashu.clipsync.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNav(navController: NavHostController){
    NavHost(navController = navController, startDestination = Routes.SignIn) {

    }
}

