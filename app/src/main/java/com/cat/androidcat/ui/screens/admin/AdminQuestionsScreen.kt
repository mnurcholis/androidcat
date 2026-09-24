package com.cat.androidcat.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.cat.androidcat.ui.components.FiguralOptionThumbnail
import com.cat.androidcat.ui.components.FiguralQuestionView
import com.cat.androidcat.ui.theme.*
import com.cat.androidcat.util.MathFormatter
import com.cat.androidcat.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminQuestionsScreen(
    viewModel: AdminViewModel,
    onNavigateToAiGenerate: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var questionToDelete by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var expandedQuestionId by remember { mutableStateOf<String?>(null) }

    val filteredQuestions = remember(state.questions, searchQuery) {
        if (searchQuery.isBlank()) {
            state.questions
        } else {
            state.questions.filter { q ->
                q.text.contains(searchQuery, ignoreCase = true) ||
                        (q.category ?: "").contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank Soal (${state.totalQuestions})", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadDashboardData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Segarkan", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAiGenerate,
                containerColor = AccentIndigo,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                text = { Text("Buat Soal AI", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            // Search Bar & Filter Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari butir soal / kata kunci...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Hapus", tint = TextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedCategoryFilter == null,
                            onClick = { viewModel.filterByCategory(null) },
                            label = { Text("Semua (${state.totalQuestions})", fontSize = 12.sp) }
                        )
                    }
                    items(state.categories) { cat ->
                        val isParentActive = state.selectedCategoryFilter == cat.id || 
                                              state.selectedCategoryFilter == cat.name ||
                                              cat.allChildren.any { it.id == state.selectedCategoryFilter }
                        FilterChip(
                            selected = isParentActive,
                            onClick = { viewModel.filterByCategory(cat.id) },
                            label = { Text(cat.name, fontSize = 12.sp) }
                        )
                    }
                }

                // Subcategory Filter Chips (if parent is selected)
                val activeParent = state.categories.find { it.id == state.selectedCategoryFilter || it.name == state.selectedCategoryFilter }
                    ?: state.categories.find { cat -> cat.allChildren.any { it.id == state.selectedCategoryFilter } }

                if (activeParent != null && activeParent.allChildren.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = state.selectedCategoryFilter == activeParent.id,
                                onClick = { viewModel.filterByCategory(activeParent.id) },
                                label = { Text("Semua ${activeParent.name}", fontSize = 11.sp) }
                            )
                        }
                        items(activeParent.allChildren) { sub ->
                            FilterChip(
                                selected = state.selectedCategoryFilter == sub.id,
                                onClick = { viewModel.filterByCategory(sub.id) },
                                label = { Text(sub.name, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (filteredQuestions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Tidak ada soal yang cocok dengan \"$searchQuery\"" else "Belum ada soal pada kategori ini.",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(filteredQuestions) { index, q ->
                        val isExpanded = expandedQuestionId == q.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    expandedQuestionId = if (isExpanded) null else q.id
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val catName = q.category ?: q.categoryRel?.name ?: "SKD"
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PrimaryBlueLight)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("No. ${index + 1} • $catName", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = "Rincian",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )

                                        Spacer(modifier = Modifier.width(6.dp))

                                        IconButton(
                                            onClick = { questionToDelete = q.id },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = CatRed, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = MathFormatter.format(q.text),
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )

                                if (q.figuralData != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    FiguralQuestionView(figuralData = q.figuralData)
                                }

                                if (!q.correctAnswer.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Kunci Jawaban: ${MathFormatter.format(q.correctAnswer)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CatGreen
                                    )
                                }

                                // Expandable Options & Explanation
                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = BorderColor)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text("Pilihan Jawaban:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    q.options.forEach { opt ->
                                        val isCorrect = opt.key.equals(q.correctAnswer, ignoreCase = true)
                                        val figuralOpt = q.figuralData?.options?.get(opt.key)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isCorrect) CatGreenLight else BackgroundLight)
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${opt.key}. ",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isCorrect) CatGreen else TextPrimary
                                            )
                                            if (!figuralOpt?.shapes.isNullOrEmpty()) {
                                                FiguralOptionThumbnail(
                                                    optionItem = figuralOpt,
                                                    size = 36.dp,
                                                    modifier = Modifier.padding(end = 8.dp)
                                                )
                                            }
                                            Text(
                                                text = MathFormatter.format(opt.value),
                                                fontSize = 13.sp,
                                                color = if (isCorrect) CatGreen else TextPrimary
                                            )
                                        }
                                    }

                                    if (!q.explanation.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("Pembahasan:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                        Text(
                                            text = MathFormatter.format(q.explanation),
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB
                    }
                }
            }
        }
    }

    if (questionToDelete != null) {
        AlertDialog(
            onDismissRequest = { questionToDelete = null },
            title = { Text("Hapus Soal?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus butir soal ini dari bank soal?") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = questionToDelete!!
                        questionToDelete = null
                        viewModel.deleteQuestion(id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CatRed)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { questionToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
