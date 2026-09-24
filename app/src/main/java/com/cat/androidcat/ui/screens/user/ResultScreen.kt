package com.cat.androidcat.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cat.androidcat.ui.components.CatButton
import com.cat.androidcat.ui.theme.*
import com.cat.androidcat.util.MathFormatter
import com.cat.androidcat.viewmodel.ExamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: ExamViewModel,
    onBackToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val result = state.examResult

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hasil Ujian CAT", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        }
    ) { padding ->
        if (result == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Memuat hasil ujian...", color = TextSecondary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Score Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Skor Akhir Anda",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${result.score.toInt()}",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (result.score >= 65) CatGreen else CatRed
                        )
                        Text(
                            text = if (result.score >= 65) "MEMENUHI PASSING GRADE" else "BELUM MEMENUHI PASSING GRADE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (result.score >= 65) CatGreen else CatRed
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3 Stats Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatPill(label = "Total Soal", value = "${result.totalQuestions}", color = PrimaryBlue)
                            StatPill(label = "Benar", value = "${result.correctAnswers}", color = CatGreen)
                            StatPill(label = "Salah", value = "${result.wrongAnswers}", color = CatRed)
                        }
                    }
                }
            }

            // Section Header
            item {
                Text(
                    text = "Rincian Pembahasan Butir Soal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Question Details
            itemsIndexed(result.details) { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (item.isCorrect) CatGreenLight else CatRedLight)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "No. ${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (item.isCorrect) CatGreen else CatRed
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (item.isCorrect) "Benar" else "Salah",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (item.isCorrect) CatGreen else CatRed
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = MathFormatter.format(item.questionText),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            Text(
                                text = "Pilihan Anda: ",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = MathFormatter.format(item.userChoice.ifEmpty { "(Kosong)" }),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isCorrect) CatGreen else CatRed
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Kunci: ",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = MathFormatter.format(item.correctAnswer),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CatGreen
                            )
                        }

                        if (!item.explanation.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pembahasan: ${MathFormatter.format(item.explanation)}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Return to Home Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                CatButton(
                    text = "Kembali ke Menu Utama",
                    onClick = onBackToHome
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
    }
}
