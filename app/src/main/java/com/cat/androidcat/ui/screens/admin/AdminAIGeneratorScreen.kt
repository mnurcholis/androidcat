package com.cat.androidcat.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cat.androidcat.data.model.CategoryDto
import com.cat.androidcat.ui.components.CatButton
import com.cat.androidcat.ui.theme.*
import com.cat.androidcat.util.MathFormatter
import com.cat.androidcat.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAIGeneratorScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    var selectedParentCategory by remember { mutableStateOf<CategoryDto?>(null) }
    var selectedSubcategory by remember { mutableStateOf<CategoryDto?>(null) }

    var topic by remember { mutableStateOf("") }
    var count by remember { mutableStateOf(5) }
    var difficulty by remember { mutableStateOf("medium") }
    var expandedParentDropdown by remember { mutableStateOf(false) }
    var expandedSubDropdown by remember { mutableStateOf(false) }
    var showApiKeyField by remember { mutableStateOf(false) }
    var apiKeyInput by remember { mutableStateOf(state.customApiKey) }
    var showApiKeyText by remember { mutableStateOf(false) }
    var customInstructions by remember { mutableStateOf("") }

    // Initialize selected parent category
    LaunchedEffect(state.categories) {
        if (state.categories.isNotEmpty() && selectedParentCategory == null) {
            val firstParent = state.categories.first()
            selectedParentCategory = firstParent
            selectedSubcategory = null
        }
    }

    LaunchedEffect(state.customApiKey) {
        if (apiKeyInput.isEmpty() && state.customApiKey.isNotEmpty()) {
            apiKeyInput = state.customApiKey
        }
    }

    val availableSubcategories = selectedParentCategory?.allChildren ?: emptyList()
    val effectiveCategoryId = selectedSubcategory?.id ?: selectedParentCategory?.id ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Question Generator", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
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
            // Success / Error banner
            val successMsg = state.successMessage
            if (successMsg != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CatGreenLight)
                        .padding(14.dp)
                ) {
                    Text(text = successMsg, color = CatGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            val errorMsg = state.error
            if (errorMsg != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CatRedLight)
                        .padding(14.dp)
                ) {
                    Text(text = errorMsg, color = CatRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Generator Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Konfigurasi Generator Soal SKD",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Kategori Utama (TWK, TIU, TKP)
                    Text("1. Kategori Utama", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedParentDropdown,
                        onExpandedChange = { expandedParentDropdown = !expandedParentDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedParentCategory?.name ?: "Pilih Kategori",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedParentDropdown) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedParentDropdown,
                            onDismissRequest = { expandedParentDropdown = false }
                        ) {
                            state.categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        selectedParentCategory = cat
                                        selectedSubcategory = null // Reset subcategory when parent changes
                                        expandedParentDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Subkategori (Analogi, Deret, Bela Negara, dll)
                    Text("2. Subkategori Spesifik", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedSubDropdown,
                        onExpandedChange = { expandedSubDropdown = !expandedSubDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedSubcategory?.name ?: "Semua Subkategori (${selectedParentCategory?.name ?: "Umum"})",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubDropdown) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedSubDropdown,
                            onDismissRequest = { expandedSubDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Semua Subkategori (${selectedParentCategory?.name ?: "Umum"})", fontWeight = FontWeight.SemiBold) },
                                onClick = {
                                    selectedSubcategory = null
                                    expandedSubDropdown = false
                                }
                            )

                            availableSubcategories.forEach { sub ->
                                DropdownMenuItem(
                                    text = { Text(sub.name) },
                                    onClick = {
                                        selectedSubcategory = sub
                                        expandedSubDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Topik Spesifik / Kisi-kisi
                    Text("3. Topik Tambahan / Kata Kunci (Opsional)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        placeholder = { Text("Contoh: Bilangan Pecahan, Pancasila Sila ke-3") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Jumlah Soal
                    Text("4. Jumlah Soal: $count butir", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Slider(
                        value = count.toFloat(),
                        onValueChange = { count = it.toInt() },
                        valueRange = 1f..15f,
                        steps = 13,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5. Tingkat Kesulitan
                    Text("5. Tingkat Kesulitan", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("easy" to "Mudah", "medium" to "Sedang", "hard" to "HOTS").forEach { (key, label) ->
                            val isSelected = difficulty == key
                            FilterChip(
                                selected = isSelected,
                                onClick = { difficulty = key },
                                label = { Text(label, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 6. Optional API Key Toggle
                    TextButton(
                        onClick = { showApiKeyField = !showApiKeyField },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = if (showApiKeyField) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showApiKeyField) "Sembunyikan Pengaturan API Key" else "Pengaturan Gemini API Key (Opsional)",
                            fontSize = 13.sp
                        )
                    }

                    if (showApiKeyField) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = {
                                apiKeyInput = it
                                viewModel.saveApiKey(it)
                            },
                            label = { Text("Google Gemini API Key") },
                            placeholder = { Text("AIzaSy...") },
                            trailingIcon = {
                                IconButton(onClick = { showApiKeyText = !showApiKeyText }) {
                                    Icon(
                                        imageVector = if (showApiKeyText) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (showApiKeyText) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Text(
                            text = "Kunci API akan tersimpan aman di HP ini untuk generator soal AI.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Button
                    CatButton(
                        text = "Generate dengan Gemini AI ✨",
                        onClick = {
                            viewModel.generateAiQuestions(
                                categoryId = effectiveCategoryId,
                                topic = topic.ifBlank { selectedSubcategory?.name },
                                count = count,
                                difficulty = difficulty,
                                apiKey = apiKeyInput.ifBlank { null },
                                customInstructions = customInstructions.ifBlank { null }
                            )
                        },
                        isLoading = state.isGenerating,
                        containerColor = AccentIndigo
                    )
                }
            }

            // Preview Section of Generated Questions
            if (state.generatedPreview.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hasil Generate (${state.generatedPreview.size} Soal)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Tinjau butir soal sebelum disimpan ke Bank Soal",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    TextButton(
                        onClick = { viewModel.clearGeneratedPreview() },
                        colors = ButtonDefaults.textButtonColors(contentColor = CatRed)
                    ) {
                        Text("Reset")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // List of preview questions
                state.generatedPreview.forEachIndexed { index, q ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CatGreenLight)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("Soal Baru #${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CatGreen)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = MathFormatter.format(q.text),
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            q.options.forEach { opt ->
                                val isCorrect = opt.key.equals(q.correctAnswer, ignoreCase = true)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isCorrect) CatGreenLight else BackgroundLight)
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${opt.key}. ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isCorrect) CatGreen else TextPrimary
                                    )
                                    Text(
                                        text = MathFormatter.format(opt.value),
                                        fontSize = 13.sp,
                                        color = if (isCorrect) CatGreen else TextPrimary
                                    )
                                }
                            }

                            if (!q.explanation.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Pembahasan: ${MathFormatter.format(q.explanation)}",
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Save to Bank Button
                CatButton(
                    text = "Simpan Semua ke Bank Soal (${state.generatedPreview.size} Soal) 💾",
                    onClick = { viewModel.saveGeneratedPreviewToBank() },
                    isLoading = state.isSavingAi,
                    containerColor = CatGreen
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
