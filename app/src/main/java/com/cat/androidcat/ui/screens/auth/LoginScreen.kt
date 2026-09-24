package com.cat.androidcat.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cat.androidcat.ui.components.CatButton
import com.cat.androidcat.ui.theme.*
import com.cat.androidcat.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (isAdmin: Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess, uiState.user) {
        if (uiState.isSuccess && uiState.user != null) {
            onLoginSuccess(uiState.user!!.isAdmin)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(PrimaryBlue, AccentIndigo)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CAT BKN",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Computer Assisted Test Mobile",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Card Form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-30).dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Masuk Akun",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "Gunakan email dan password terdaftar Anda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    // Error Message
                    if (uiState.error != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CatRedLight)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = uiState.error!!,
                                color = CatRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        placeholder = { Text("contoh@email.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email", tint = TextSecondary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password", tint = TextSecondary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password",
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    CatButton(
                        text = "Masuk Sekarang",
                        onClick = { viewModel.login(email, password) },
                        isLoading = uiState.isLoading
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Register link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Belum punya akun? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Daftar di sini",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}
