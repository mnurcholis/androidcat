package com.cat.androidcat.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.cat.androidcat.data.model.CategoryDto
import com.cat.androidcat.ui.components.CatButton
import com.cat.androidcat.ui.theme.*
import com.cat.androidcat.viewmodel.AuthViewModel
import com.cat.androidcat.viewmodel.ExamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    examViewModel: ExamViewModel,
    onStartExam: (examId: String) -> Unit,
    onStartStudy: (examId: String) -> Unit,
    onNavigateToMaterials: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val examState by examViewModel.uiState.collectAsState()

    val userName = authState.user?.name ?: "Peserta CAT"
    val isAdmin = authState.user?.isAdmin == true

    var showStudyConfigSheet by remember { mutableStateOf(false) }
    var selectedParentCategory by remember { mutableStateOf<CategoryDto?>(null) }
    var selectedSubcategory by remember { mutableStateOf<CategoryDto?>(null) }
    var selectedQuestionCount by remember { mutableStateOf(20) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Halo, $userName 👋",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isAdmin) "Role: Administrator" else "Peserta SKD CPNS / Kedinasan",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = onNavigateToAdmin) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Panel",
                                tint = Color.White
                            )
                        }
                    }
                    IconButton(onClick = {
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
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
            // Passing Grade Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nilai Ambang Batas (Passing Grade) SKD",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PassingGradeBadge(label = "TWK", minScore = "65", color = CatGreen)
                        PassingGradeBadge(label = "TIU", minScore = "80", color = PrimaryBlue)
                        PassingGradeBadge(label = "TKP", minScore = "166", color = AccentIndigo)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Mulai Ujian / Belajar
            Text(
                text = "Pilih Mode Simulasi",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Exam Mode Card (Simulasi Resmi)
            ActionCard(
                title = "Simulasi Ujian CAT Resmi",
                subtitle = "Countdown timer, 110 butir soal, kalkulasi skor otomatis mirip tes BKN.",
                icon = Icons.Default.Quiz,
                badgeText = "Mode Ujian",
                badgeColor = PrimaryBlue,
                isLoading = examState.isLoading && examState.mode == "EXAM",
                onClick = {
                    examViewModel.startSession(
                        mode = "EXAM",
                        questionCount = 110,
                        durationMinutes = 100,
                        onStarted = onStartExam
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Study Mode Card (Latihan Mandiri Per Kategori)
            ActionCard(
                title = "Latihan & Pembahasan Soal",
                subtitle = "Pilih kategori (TWK, TIU, TKP, atau subkategori). Tanpa batas waktu dengan pembahasan instan.",
                icon = Icons.Default.MenuBook,
                badgeText = "Mode Belajar",
                badgeColor = CatGreen,
                isLoading = examState.isLoading && examState.mode == "STUDY",
                onClick = {
                    showStudyConfigSheet = true
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Study Materials Link Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToMaterials() },
                shape = RoundedCornerShape(16.dp),
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
                            .background(AccentIndigoLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LibraryBooks,
                            contentDescription = null,
                            tint = AccentIndigo,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Modul & Materi Pembelajaran",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Rangkuman TWK, rumus cepat TIU, dan tips TKP",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet: Konfigurasi Mode Belajar (Pilih Kategori & Subkategori)
    if (showStudyConfigSheet) {
        val availableSubcategories = selectedParentCategory?.allChildren ?: emptyList()

        ModalBottomSheet(
            onDismissRequest = { showStudyConfigSheet = false },
            containerColor = SurfaceCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pilih Kategori Belajar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Fokuskan latihan pada materi yang ingin dipelajari",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    IconButton(onClick = { showStudyConfigSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Kategori Utama
                Text(
                    text = "1. Kategori Ujian",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Semua Kategori (Campuran) Card
                val isAllSelected = selectedParentCategory == null
                StudyCategoryOptionCard(
                    title = "Semua Kategori (Campuran SKD)",
                    subtitle = "Latihan butir soal campuran acak TWK, TIU, dan TKP",
                    isSelected = isAllSelected,
                    onClick = {
                        selectedParentCategory = null
                        selectedSubcategory = null
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Kategori TWK, TIU, TKP
                examState.categories.forEach { cat ->
                    val isSelected = selectedParentCategory?.id == cat.id
                    val passingText = when (cat.name.uppercase()) {
                        "TWK" -> "Passing Grade: 65"
                        "TIU" -> "Passing Grade: 80"
                        "TKP" -> "Passing Grade: 166"
                        else -> ""
                    }

                    StudyCategoryOptionCard(
                        title = "${cat.name} (${cat.allChildren.size} Submateri)",
                        subtitle = passingText,
                        isSelected = isSelected,
                        onClick = {
                            selectedParentCategory = cat
                            selectedSubcategory = null
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 2. Subkategori Spesifik (jika kategori utama dipilih)
                if (selectedParentCategory != null && availableSubcategories.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "2. Subkategori Spesifik (${selectedParentCategory?.name})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedSubcategory == null,
                                onClick = { selectedSubcategory = null },
                                label = { Text("Semua ${selectedParentCategory?.name}", fontSize = 12.sp) }
                            )
                        }
                        items(availableSubcategories) { sub ->
                            FilterChip(
                                selected = selectedSubcategory?.id == sub.id,
                                onClick = { selectedSubcategory = sub },
                                label = { Text(sub.name, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Jumlah Soal
                Text(
                    text = "3. Jumlah Butir Soal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(10, 20, 30, 50).forEach { count ->
                        val isCountSelected = selectedQuestionCount == count
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCountSelected) CatGreenLight else BackgroundLight)
                                .border(
                                    width = if (isCountSelected) 2.dp else 1.dp,
                                    color = if (isCountSelected) CatGreen else BorderColor,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedQuestionCount = count }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$count",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isCountSelected) CatGreen else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Mulai
                val effectiveCategoryId = selectedSubcategory?.id ?: selectedParentCategory?.id

                CatButton(
                    text = "Mulai Latihan Mandiri 🚀",
                    onClick = {
                        showStudyConfigSheet = false
                        examViewModel.startSession(
                            mode = "STUDY",
                            category = effectiveCategoryId,
                            questionCount = selectedQuestionCount,
                            durationMinutes = null,
                            onStarted = onStartStudy
                        )
                    },
                    containerColor = CatGreen
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun StudyCategoryOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) CatGreenLight else BackgroundLight)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) CatGreen else BorderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = CatGreen)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isSelected) CatGreen else TextPrimary
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun PassingGradeBadge(label: String, minScore: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = color)
        Text(text = "Min $minScore", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = color)
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String,
    badgeColor: Color,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(22.dp))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            CatButton(
                text = "Mulai Sekarang",
                onClick = onClick,
                isLoading = isLoading,
                containerColor = badgeColor
            )
        }
    }
}
