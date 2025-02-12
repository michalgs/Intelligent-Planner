package com.example.io_project.ui.screens.authscreen

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.io_project.ui.theme.IO_ProjectTheme
import com.example.io_project.Constants.HOME_SCREEN
import com.example.io_project.ui.components.AuthContent
import com.example.io_project.ui.components.AuthTopBar
import com.example.io_project.ui.components.OneTapSignIn
import com.example.io_project.ui.components.SignInWithGoogle
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential



@Composable
fun AuthScreen() {
    val viewModel: AuthViewModel = hiltViewModel()
    AuthScreen(
        viewModel
    ) {}
}

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    navigateTo: (String) -> Unit
) {
    Scaffold(
        topBar = {
            AuthTopBar()
        },
        content = { padding ->
            AuthContent(
                padding = padding,
                oneTapSignIn = {
                    viewModel.oneTapSignIn()
                }
            )
        }
    )

    val launcher = rememberLauncherForActivityResult(StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val credentials = viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                val googleIdToken = credentials.googleIdToken
                val googleCredentials = getCredential(googleIdToken, null)
                viewModel.signInWithGoogle(googleCredentials)
            } catch (it: ApiException) {
                print(it)
            }
        }
    }

    fun launch(signInResult: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }

    OneTapSignIn(
        launch = {
            launch(it)
        }
    )

    SignInWithGoogle(
        navigateToHomeScreen = { signedIn ->
            if (signedIn) {
                navigateTo("$HOME_SCREEN/false")
            }
        }
    )
}

@Preview
@Composable
fun AuthScreenPreview() {
    IO_ProjectTheme {
        AuthScreen()
    }
}