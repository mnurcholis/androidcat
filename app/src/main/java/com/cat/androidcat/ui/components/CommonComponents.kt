package com.cat.androidcat.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cat.androidcat.ui.theme.*

@Composable
fun CatButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    containerColor: Color = PrimaryBlue,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.8f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Memproses...", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        } else {
            Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun OptionItem(
    optionKey: String,
    optionText: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    isCorrectAnswer: Boolean? = null,
    enabled: Boolean = true
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isCorrectAnswer == true -> CatGreen
            isCorrectAnswer == false && isSelected -> CatRed
            isSelected -> PrimaryBlue
            else -> BorderColor
        },
        label = "borderColor"
    )

    val bgColor by animateColorAsState(
        targetValue = when {
            isCorrectAnswer == true -> CatGreenLight
            isCorrectAnswer == false && isSelected -> CatRedLight
            isSelected -> PrimaryBlueLight.copy(alpha = 0.5f)
            else -> SurfaceCard
        },
        label = "bgColor"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (isSelected || isCorrectAnswer != null) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled, onClick = onSelect)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circle letter key (A, B, C, D, E)
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCorrectAnswer == true -> CatGreen
                        isCorrectAnswer == false && isSelected -> CatRed
                        isSelected -> PrimaryBlue
                        else -> BackgroundLight
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = optionKey,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isSelected || isCorrectAnswer == true || (isCorrectAnswer == false && isSelected))
                    Color.White else TextPrimary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = optionText,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TimerBadge(
    secondsRemaining: Long,
    modifier: Modifier = Modifier
) {
    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val isWarning = secondsRemaining in 1..300 // <= 5 mins
    val isCritical = secondsRemaining in 1..60 // <= 1 min

    val (bgColor, textColor) = when {
        isCritical -> CatRedLight to CatRed
        isWarning -> CatYellowLight to CatYellow
        else -> PrimaryBlueLight to PrimaryBlue
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = "Timer",
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = timeFormatted,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            color = textColor
        )
    }
}
