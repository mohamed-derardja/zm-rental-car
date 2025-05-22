package com.example.myapplication.ui.screens.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.poppins
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.Identity

private const val TAG = "SignInScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onSignInSuccess: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Facebook login launcher
    val facebookLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleFacebookActivityResult(result.resultCode, result.data)
    }

    // Google login launcher
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = Identity.getSignInClient(context).getSignInCredentialFromIntent(result.data)
            viewModel.handleGoogleActivityResult(task)
        }
    }

    // Facebook login callback
    val callbackManager = remember { CallbackManager.Factory.create() }
    
    LaunchedEffect(Unit) {
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    viewModel.handleFacebookLoginSuccess(result)
                }

                override fun onCancel() {
                    viewModel.handleFacebookLoginCancel()
                }

                override fun onError(error: FacebookException) {
                    viewModel.handleFacebookLoginError(error)
                }
            })
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> onSignInSuccess()
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TopDecoration()

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text(stringResource(R.string.remember_me))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.sign_in))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToForgotPassword,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.forgot_password))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Or continue with")

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SocialButton(
                icon = R.drawable.facebook,
                text = stringResource(R.string.facebook),
                onClick = { 
                    LoginManager.getInstance().logInWithReadPermissions(
                        context as android.app.Activity,
                        listOf("email", "public_profile")
                    )
                }
            )

            SocialButton(
                icon = R.drawable.google,
                text = stringResource(R.string.google),
                onClick = { viewModel.loginWithGoogle(googleLauncher) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.dont_have_account))
            TextButton(onClick = onNavigateToRegister) {
                Text(stringResource(R.string.create_account))
            }
        }
    }

    if (uiState is AuthUiState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    if (uiState is AuthUiState.Error) {
        val error = (uiState as AuthUiState.Error).message
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
private fun SocialButton(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun TopDecoration() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        // Big star (main sparkle), as background
        Image(
            painter = painterResource(id = R.drawable.bigstar),
            contentDescription = "Big Star",
            modifier = Modifier
                .size(width = 320.dp, height = 210.dp)
                .align(Alignment.Center)
        )
        // Small star, top left, closer to big star
        Image(
            painter = painterResource(id = R.drawable.smallstar),
            contentDescription = "Small Star",
            modifier = Modifier
                .size(22.dp)
                .align(Alignment.TopStart)
                .offset(x = 35.dp, y = 80.dp)
        )
        // Medium star, top right
        Image(
            painter = painterResource(id = R.drawable.meduimstat),
            contentDescription = "Medium Star",
            modifier = Modifier
                .size(38.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-45).dp, y = 20.dp)
        )
        // Texts inside the big star
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign In",
                fontSize = 30.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF030303),
                letterSpacing = 0.10.sp
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Good to See You Again!\nLet's Find Your Perfect Car.",
                fontSize = 14.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                letterSpacing = 0.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInScreenPreview() {
    MyApplicationTheme {
        SignInScreen()
    }
} 