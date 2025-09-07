package com.ashu.clipsync.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ashu.clipsync.AppSession
import com.ashu.clipsync.services.ClipboardSync
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavController) {
    var isSyncEnabled by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("user@example.com") } // Placeholder
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        email = AppSession.user?.email?: "null"
        context.startForegroundService(Intent(context, ClipboardSync::class.java))
        isSyncEnabled = true
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sync Clipboard", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = isSyncEnabled,
                    onCheckedChange = {
                        isSyncEnabled = it
                        if (it) {
                            context.startForegroundService(Intent(context, ClipboardSync::class.java))
                        } else {
context.stopService(Intent(context, ClipboardSync::class.java))
}
                                      },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF4285F4),
                        uncheckedThumbColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    AppSession.user = null
                    context.stopService(Intent(context, ClipboardSync::class.java))
                    navController.navigate(Routes.SignIn)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
            ) {
                Text(text = "Logged in as $email", color = Color.White)
            }
        }
    }
}
