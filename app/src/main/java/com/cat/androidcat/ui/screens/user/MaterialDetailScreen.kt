package com.cat.androidcat.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cat.androidcat.ui.theme.*
import com.cat.androidcat.viewmodel.MaterialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailScreen(
    materialId: String,
    viewModel: MaterialViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(materialId) {
        viewModel.fetchMaterialDetail(materialId)
    }

    val material = state.selectedMaterial

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(material?.title ?: "Detail Materi", maxLines = 1, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
            return@Scaffold
        }

        if (material != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(BackgroundLight)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                material.category?.name?.let { catName ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryBlueLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(catName, color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text(
                    text = material.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Text(
                        text = material.content,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
