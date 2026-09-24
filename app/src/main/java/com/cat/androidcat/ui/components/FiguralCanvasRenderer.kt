package com.cat.androidcat.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cat.androidcat.data.model.FiguralCellDto
import com.cat.androidcat.data.model.FiguralDataDto
import com.cat.androidcat.data.model.FiguralOptionItemDto
import com.cat.androidcat.data.model.FiguralShapeDto
import com.cat.androidcat.ui.theme.*

/**
 * Native Vector Canvas Renderer for Figural & Figural 9-Kotak Questions.
 * Renders mathematical geometric figures with zero bandwidth, instant speed,
 * and high sharpness on AMOLED Dark Theme.
 */

@Composable
fun FiguralQuestionView(
    figuralData: FiguralDataDto,
    modifier: Modifier = Modifier
) {
    val grid = figuralData.questionGrid ?: emptyList()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (figuralData.type.equals("sequence", ignoreCase = true)) {
            // Sequence / Serial pattern (horizontal row of cells)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                grid.sortedBy { it.index }.forEach { cell ->
                    FiguralCellBox(
                        cell = cell,
                        cellSize = 56.dp
                    )
                }
            }
        } else {
            // Standard 3x3 Grid (Figural 9 Kotak)
            val sortedCells = grid

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BackgroundLight)
                    .border(2.dp, BorderColor, RoundedCornerShape(10.dp))
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (r in 0..2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (c in 0..2) {
                            val cell = sortedCells.find { it.row == r && it.col == c }
                                ?: FiguralCellDto(row = r, col = c, isMissing = (r == 2 && c == 2))
                            FiguralCellBox(
                                cell = cell,
                                cellSize = 76.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FiguralCellBox(
    cell: FiguralCellDto,
    cellSize: Dp = 70.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(cellSize)
            .clip(RoundedCornerShape(8.dp))
            .background(if (cell.isMissing) AccentAmberLight else SurfaceCardElevated)
            .border(
                width = if (cell.isMissing) 2.dp else 1.dp,
                color = if (cell.isMissing) AccentAmber else BorderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cell.isMissing) {
            Text(
                text = "?",
                fontSize = (cellSize.value * 0.42f).sp,
                fontWeight = FontWeight.ExtraBold,
                color = AccentAmberText
            )
        } else {
            FiguralShapesCanvas(
                shapes = cell.shapes ?: emptyList(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun FiguralOptionThumbnail(
    optionItem: FiguralOptionItemDto?,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val shapes = optionItem?.shapes ?: emptyList()
    if (shapes.isEmpty()) return

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceCardElevated)
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        FiguralShapesCanvas(
            shapes = shapes,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun FiguralShapesCanvas(
    shapes: List<FiguralShapeDto>?,
    modifier: Modifier = Modifier,
    primaryColor: Color = TextPrimary,
    strokeWidthDp: Dp = 2.dp
) {
    val items = shapes ?: emptyList()
    Canvas(modifier = modifier.padding(4.dp)) {
        val strokeWidthPx = strokeWidthDp.toPx()
        val w = size.width
        val h = size.height
        val minDim = minOf(w, h)

        for (item in items) {
            val scaleFactor = when ((item.size ?: "medium").lowercase()) {
                "small" -> 0.35f
                "large" -> 0.85f
                else -> 0.60f // medium
            }

            val shapeRadius = (minDim * scaleFactor) / 2f
            val shapeDiameter = shapeRadius * 2f

            // Position offset relative to center
            val cx = when ((item.position ?: "center").lowercase()) {
                "left", "top_left", "bottom_left" -> w * 0.30f
                "right", "top_right", "bottom_right" -> w * 0.70f
                else -> w * 0.50f
            }
            val cy = when ((item.position ?: "center").lowercase()) {
                "top", "top_left", "top_right" -> h * 0.30f
                "bottom", "bottom_left", "bottom_right" -> h * 0.70f
                else -> h * 0.50f
            }
            val center = Offset(cx, cy)

            val drawStyle: DrawStyle = if ((item.fill ?: "solid").equals("solid", ignoreCase = true)) {
                Fill
            } else {
                Stroke(width = strokeWidthPx)
            }

            withTransform({
                if (item.rotation != 0f) {
                    rotate(degrees = item.rotation, pivot = center)
                }
            }) {
                when ((item.shape ?: "circle").lowercase()) {
                    "circle" -> {
                        drawCircle(
                            color = primaryColor,
                            radius = shapeRadius,
                            center = center,
                            style = drawStyle
                        )
                    }

                    "square" -> {
                        drawRect(
                            color = primaryColor,
                            topLeft = Offset(cx - shapeRadius, cy - shapeRadius),
                            size = Size(shapeDiameter, shapeDiameter),
                            style = drawStyle
                        )
                    }

                    "triangle" -> {
                        val path = Path().apply {
                            moveTo(cx, cy - shapeRadius)
                            lineTo(cx + shapeRadius, cy + shapeRadius)
                            lineTo(cx - shapeRadius, cy + shapeRadius)
                            close()
                        }
                        drawPath(path = path, color = primaryColor, style = drawStyle)
                    }

                    "diamond" -> {
                        val path = Path().apply {
                            moveTo(cx, cy - shapeRadius)
                            lineTo(cx + shapeRadius, cy)
                            lineTo(cx, cy + shapeRadius)
                            lineTo(cx - shapeRadius, cy)
                            close()
                        }
                        drawPath(path = path, color = primaryColor, style = drawStyle)
                    }

                    "arrow" -> {
                        // Arrow pointing upwards from center
                        val path = Path().apply {
                            // Arrowhead
                            moveTo(cx, cy - shapeRadius)
                            lineTo(cx + shapeRadius * 0.7f, cy)
                            lineTo(cx + shapeRadius * 0.25f, cy)
                            lineTo(cx + shapeRadius * 0.25f, cy + shapeRadius)
                            lineTo(cx - shapeRadius * 0.25f, cy + shapeRadius)
                            lineTo(cx - shapeRadius * 0.25f, cy)
                            lineTo(cx - shapeRadius * 0.7f, cy)
                            close()
                        }
                        drawPath(path = path, color = primaryColor, style = drawStyle)
                    }

                    "cross", "plus" -> {
                        val arm = shapeRadius * 0.35f
                        val path = Path().apply {
                            moveTo(cx - arm, cy - shapeRadius)
                            lineTo(cx + arm, cy - shapeRadius)
                            lineTo(cx + arm, cy - arm)
                            lineTo(cx + shapeRadius, cy - arm)
                            lineTo(cx + shapeRadius, cy + arm)
                            lineTo(cx + arm, cy + arm)
                            lineTo(cx + arm, cy + shapeRadius)
                            lineTo(cx - arm, cy + shapeRadius)
                            lineTo(cx - arm, cy + arm)
                            lineTo(cx - shapeRadius, cy + arm)
                            lineTo(cx - shapeRadius, cy - arm)
                            lineTo(cx - arm, cy - arm)
                            close()
                        }
                        drawPath(path = path, color = primaryColor, style = drawStyle)
                    }

                    "star" -> {
                        // 4-pointed star
                        val path = Path().apply {
                            moveTo(cx, cy - shapeRadius)
                            quadraticBezierTo(cx, cy, cx + shapeRadius, cy)
                            quadraticBezierTo(cx, cy, cx, cy + shapeRadius)
                            quadraticBezierTo(cx, cy, cx - shapeRadius, cy)
                            quadraticBezierTo(cx, cy, cx, cy - shapeRadius)
                            close()
                        }
                        drawPath(path = path, color = primaryColor, style = drawStyle)
                    }

                    "line" -> {
                        drawLine(
                            color = primaryColor,
                            start = Offset(cx, cy - shapeRadius),
                            end = Offset(cx, cy + shapeRadius),
                            strokeWidth = strokeWidthPx * 1.5f
                        )
                    }

                    "dot" -> {
                        drawCircle(
                            color = primaryColor,
                            radius = shapeRadius * 0.4f,
                            center = center,
                            style = Fill
                        )
                    }

                    else -> {
                        // Fallback to square
                        drawRect(
                            color = primaryColor,
                            topLeft = Offset(cx - shapeRadius, cy - shapeRadius),
                            size = Size(shapeDiameter, shapeDiameter),
                            style = drawStyle
                        )
                    }
                }
            }
        }
    }
}
