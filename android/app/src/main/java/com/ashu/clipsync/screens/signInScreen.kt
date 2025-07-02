package com.ashu.clipsync.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ashu.clipsync.AppSession
import com.ashu.clipsync.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


@Composable
fun GoogleSignInHandler(
    onSuccess: (FirebaseUser) -> Unit,
    onFail: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    // Prepare the sign-in intent launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val auth = FirebaseAuth.getInstance()

            auth.signInWithCredential(credential)
                .addOnCompleteListener { taskResult ->
                    if (taskResult.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            AppSession.user = user
                            onSuccess(user)
                        } else {
                            onFail("User object is null after sign-in")
                        }
                    } else {
                        onFail("Firebase sign-in failed: ${taskResult.exception?.message}")
                    }
                }
        } catch (e: Exception) {
            onFail("Google Sign-In failed: ${e.message}")
        }
    }

    // Trigger sign-in when this composable is recomposed
    LaunchedEffect(Unit) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        val intent = googleSignInClient.signInIntent
        launcher.launch(intent)
    }
}

@Composable
fun SignInScreen(
    onLoggedIn: () -> Unit
) {
    var triggerSignIn by rememberSaveable { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Welcome to ClipSync",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    triggerSignIn = true
                    isLoading = true
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Continue with Google")
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Google Sign-In trigger
        if (triggerSignIn) {
            GoogleSignInHandler(
                onSuccess = { user ->
                    triggerSignIn = false
                    isLoading = false
                    errorMessage = null
                    onLoggedIn()
                },
                onFail = { error ->
                    triggerSignIn = false
                    isLoading = false
                    errorMessage = error
                }
            )
        }
    }
}


