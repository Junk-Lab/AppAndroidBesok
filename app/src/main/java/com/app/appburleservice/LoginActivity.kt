package com.app.appburleservice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.appburleservice.ui.theme.AppBurleServiceTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import kotlinx.coroutines.delay

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBurleServiceTheme {
                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackgroundColorGlobal)
                            .padding(innerPadding)
                    ) {
                        LoginContent()
                        Spacer(modifier = Modifier.weight(1f))
                        AppFooter()
                    }
                }
            }
        }
    }
}

@Composable
fun LoginContent() {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }  // Mensagem de erro

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                color = Color.White,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuário", fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorFieldGlobal, shape = RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textColorGlobal,
                    unfocusedTextColor = textColorGlobal,
                    focusedContainerColor = colorFieldGlobal,
                    unfocusedContainerColor = colorFieldGlobal,
                    focusedIndicatorColor = borderColorGlobal,
                    unfocusedIndicatorColor = borderColorGlobal,
                    focusedLabelColor = labelColorGlobal,
                    unfocusedLabelColor = labelColorGlobal,
                    cursorColor = cursorColorGlobal
                ),
                shape = RoundedCornerShape(7.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha", fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorFieldGlobal, shape = RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textColorGlobal,
                    unfocusedTextColor = textColorGlobal,
                    focusedContainerColor = colorFieldGlobal,
                    unfocusedContainerColor = colorFieldGlobal,
                    focusedIndicatorColor = borderColorGlobal,
                    unfocusedIndicatorColor = borderColorGlobal,
                    focusedLabelColor = labelColorGlobal,
                    unfocusedLabelColor = labelColorGlobal,
                    cursorColor = cursorColorGlobal
                ),
                shape = RoundedCornerShape(7.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Exibe a mensagem de erro, se houver
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            // Timeout para a mensagem de erro
            LaunchedEffect(errorMessage) {
                if (errorMessage != null) {
                    delay(5000) // 5 segundos
                    errorMessage = null // Limpa a mensagem de erro após 5 segundos
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (username.isEmpty() || password.isEmpty()) {
                        errorMessage = "Por favor, preencha todos os campos."  // Defina a mensagem de erro
                    } else {
                        errorMessage = null  // Limpe a mensagem de erro se tudo estiver correto
                        val intent = Intent(context, MainActivity::class.java).apply {
                            putExtra("USERNAME", username)
                        }
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    }
                },
                interactionSource = interactionSource,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPressed) colorButtonHoverGlobal else colorButtonGlobal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(50.dp)
                    .width(150.dp)
            ) {
                Text(text = "Entrar", fontSize = 18.sp)
            }
        }

        AppFooter(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

