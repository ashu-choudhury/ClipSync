package com.ashu.clipsync.screens

sealed class Routes(val path: String) {
    object SignIn: Routes("/SignIn")
    object Home: Routes("/home")
}
