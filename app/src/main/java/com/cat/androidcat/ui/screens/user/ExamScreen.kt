package com.cat.androidcat.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cat.androidcat.ui.components.CatButton
import com.cat.androidcat.ui.components.OptionItem
import com.cat.androidcat.ui.components.TimerBadge
import com.cat.androidcat.ui.theme.*
import com.cat.androidcat.util.MathFormatter
import com.cat.androidcat.viewmodel.ExamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    viewModel: ExamViewModel,
    mode: String, // "EXAM" or "STUDY"
    onFinishExam: (examId: String) -> Unit,
    onBackToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val questions = state.questions
    val currentQuestion = questions.getOrNull(state.currentIndex)
    var showSheet by remember { mutableStateOf(false) }
    var showConfirmFinishDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isFinished, state.examId) {
        if (state.isFinished && state.examId != null) {
            onFinishExam(state.examId!!)
        }
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    val isDoubt = currentQuestion != null && state.doubtfulQuestions.contains(currentQuestion.id)
    val selectedOption = currentQuestion?.let { state.answers[it.id] }
    val studyFeedback = currentQuestion?.let { state.studyFeedback[it.id] }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Soal ${state.currentIndex + 1}/${questions.size}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (mode == "EXAM") "Simulasi CAT SKD" else "Mode Latihan Mandiri",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    if (mode == "EXAM") {
                        TimerBadge(secondsRemaining = state.remainingSeconds)
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Grid numbers icon
                    IconButton(onClick = { showSheet = true }) {
                        Icon(
                            Icons.Default.GridView,
                            contentDescription = "Daftar Nomor Soal",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        },
        bottomBar = {
            // Sticky Bottom Navigation Bar for Mobile Thumb Ergonomics
            Surface(
                color = SurfaceCard,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.previousQuestion() },
                        enabled = state.currentIndex > 0,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sebelumnya")
                    }

                    if (state.currentIndex == questions.size - 1) {
                        Button(
                            onClick = {
                                if (mode == "EXAM") {
                                    showConfirmFinishDialog = true
                                } else {
                                    onBackToHome()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CatGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (mode == "EXAM") "Selesai Ujian" else "Selesai Belajar")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.nextQuestion() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Selanjutnya")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }
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
            if (currentQuestion != null) {
                // Category Pill + Doubt Button (Exam Mode)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val catName = currentQuestion.category ?: currentQuestion.categoryRel?.name ?: "SKD"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryBlueLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = catName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    if (mode == "EXAM") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDoubt) CatYellowLight else Color.Transparent)
                                .clickable { viewModel.toggleDoubt() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = isDoubt,
                                onCheckedChange = { viewModel.toggleDoubt() },
                                colors = CheckboxDefaults.colors(checkedColor = CatYellow)
                            )
                            Text(
                                text = "Ragu-ragu",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDoubt) CatYellow else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Question Text Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Text(
                        text = MathFormatter.format(currentQuestion.text),
                        fontSize = 16.sp,
                        lineHeight = 25.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.padding(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options A to E
                currentQuestion.options.forEach { opt ->
                    val isOptSelected = selectedOption == opt.key

                    // In Study mode, show correct/incorrect indicators
                    val isCorrectIndicator: Boolean? = if (mode == "STUDY" && studyFeedback != null) {
                        if (opt.key == studyFeedback.correctAnswer) true
                        else if (isOptSelected && !studyFeedback.isCorrect) false
                        else null
                    } else null

                    OptionItem(
                        optionKey = opt.key,
                        optionText = MathFormatter.format(opt.value),
                        isSelected = isOptSelected,
                        isCorrectAnswer = isCorrectIndicator,
                        onSelect = { viewModel.selectOption(opt.key) },
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                // Study Mode: Explanation Card
                if (mode == "STUDY" && studyFeedback != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (studyFeedback.isCorrect) CatGreenLight else CatRedLight
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (studyFeedback.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (studyFeedback.isCorrect) CatGreen else CatRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (studyFeedback.isCorrect) "Jawaban Anda Benar! (+5 Poin)" else "Jawaban Anda Kurang Tepat (Kunci: ${studyFeedback.correctAnswer})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (studyFeedback.isCorrect) CatGreen else CatRed
                                )
                            }

                            if (!studyFeedback.explanation.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Pembahasan:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = MathFormatter.format(studyFeedback.explanation),
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Question Number Grid Bottom Sheet
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            containerColor = SurfaceCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Daftar Nomor Soal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Klik nomor untuk langsung berpindah soal",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)
                ) {
                    itemsIndexed(questions) { index, q ->
                        val isAnswered = state.answers.containsKey(q.id)
                        val isDoubtful = state.doubtfulQuestions.contains(q.id)
                        val isCurrent = index == state.currentIndex

                        val (bgColor, textColor) = when {
                            isCurrent -> PrimaryBlue to Color.White
                            isDoubtful -> CatYellow to Color.White
                            isAnswered -> CatGreen to Color.White
                            else -> BackgroundLight to TextPrimary
                        }

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) PrimaryBlueDark else BorderColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    viewModel.goToIndex(index)
                                    showSheet = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = textColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Confirm Finish Dialog
    if (showConfirmFinishDialog) {
        val answeredCount = state.answers.size
        val unansweredCount = questions.size - answeredCount

        AlertDialog(
            onDismissRequest = { showConfirmFinishDialog = false },
            title = {
                Text("Kirim Jawaban Ujian?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Anda telah menjawab $answeredCount dari ${questions.size} soal. Sisa $unansweredCount soal belum dijawab. Apakah Anda yakin ingin mengakhiri ujian?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmFinishDialog = false
                        viewModel.finishExam(onFinished = onFinishExam)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Ya, Selesaikan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmFinishDialog = false }) {
                    Text("Lanjutkan Ujian")
                }
            }
        )
    }
}
