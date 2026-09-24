package com.cat.androidcat.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cat.androidcat.ui.theme.*
import com.cat.androidcat.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onNavigateToQuestions: () -> Unit,
    onNavigateToAiGenerate: () -> Unit,
    onNavigateToMaterials: () -> Unit,
    onNavigateToUserMode: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showUsersSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Administrator CAT", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadDashboardData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Ringkasan Sistem CAT",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    title = "Bank Soal",
                    value = "${state.totalQuestions}",
                    icon = Icons.Default.Quiz,
                    color = PrimaryBlue,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToQuestions() }
                )
                AdminStatCard(
                    title = "Pengguna",
                    value = "${state.totalUsers}",
                    icon = Icons.Default.People,
                    color = CatGreen,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showUsersSheet = true }
                )
                AdminStatCard(
                    title = "Kategori",
                    value = "${state.categories.size}",
                    icon = Icons.Default.Category,
                    color = AccentIndigo,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Fitur Manajemen & Ujian",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 1. AI Question Generator
            AdminMenuCard(
                title = "AI Question Generator (Gemini) ✨",
                subtitle = "Buat paket soal otomatis berbasis kisi-kisi dan tingkat kesulitan.",
                icon = Icons.Default.AutoAwesome,
                iconColor = AccentIndigo,
                onClick = onNavigateToAiGenerate
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Bank Soal Manager
            AdminMenuCard(
                title = "Kelola Bank Soal (${state.totalQuestions} Soal)",
                subtitle = "Lihat butir soal, filter kategori, cari kata kunci, dan hapus soal.",
                icon = Icons.Default.ListAlt,
                iconColor = PrimaryBlue,
                onClick = onNavigateToQuestions
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Daftar Pengguna Terdaftar
            AdminMenuCard(
                title = "Daftar Pengguna / Peserta (${state.totalUsers})",
                subtitle = "Lihat akun peserta, role, dan tanggal pendaftaran.",
                icon = Icons.Default.People,
                iconColor = CatGreen,
                onClick = { showUsersSheet = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Modul Materi Belajar
            AdminMenuCard(
                title = "Modul & Materi Pembelajaran",
                subtitle = "Akses materi bacaan dan rangkuman rumus SKD.",
                icon = Icons.Default.MenuBook,
                iconColor = CatYellow,
                onClick = onNavigateToMaterials
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Beralih ke Mode Siswa
            AdminMenuCard(
                title = "Beralih ke Tampilan Siswa",
                subtitle = "Simulasi ujian langsung dari sudut pandang peserta.",
                icon = Icons.Default.School,
                iconColor = PrimaryBlueDark,
                onClick = onNavigateToUserMode
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal Bottom Sheet: Daftar Pengguna
    if (showUsersSheet) {
        ModalBottomSheet(
            onDismissRequest = { showUsersSheet = false },
            containerColor = SurfaceCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daftar Pengguna Terdaftar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Total ${state.users.size} akun terdaftar di sistem",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    IconButton(onClick = { showUsersSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(state.users) { u ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (u.isAdmin) AccentIndigo else PrimaryBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (u.name?.take(1) ?: u.email.take(1)).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = u.name ?: "Pengguna",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = u.email,
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (u.isAdmin) AccentIndigoLight else PrimaryBlueLight)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = u.role,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (u.isAdmin) AccentIndigo else PrimaryBlue
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(text = title, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun AdminMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}
