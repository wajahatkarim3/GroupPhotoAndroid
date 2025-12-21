package com.wajahatkarim.groupphotos.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wajahatkarim.groupphotos.ui.theme.BackgroundLight
import com.wajahatkarim.groupphotos.ui.theme.CoralOrange
import com.wajahatkarim.groupphotos.ui.theme.CoralOrangeLight
import com.wajahatkarim.groupphotos.ui.theme.GroupPhotosTheme
import com.wajahatkarim.groupphotos.ui.theme.TextPrimary
import com.wajahatkarim.groupphotos.ui.theme.TextSecondary
import com.wajahatkarim.groupphotos.ui.theme.TextTertiary
import com.wajahatkarim.groupphotos.ui.viewmodel.PhotoViewModel
import com.wajahatkarim.groupphotos.ui.viewmodel.ProcessingState

@Composable
fun ProcessingScreen(
    viewModel: PhotoViewModel,
    onCancelClick: () -> Unit = {},
    onProcessingComplete: () -> Unit = {},
    onRetryClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val processingState by viewModel.processingState.collectAsState()

    // Start processing when screen is shown
    LaunchedEffect(Unit) {
        viewModel.mergePhotos(context)
    }

    // Navigate to result when processing is successful
    LaunchedEffect(processingState) {
        if (processingState is ProcessingState.Success) {
            onProcessingComplete()
        }
    }

    // Animation for the rotating circle
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        when (val state = processingState) {
            is ProcessingState.Idle,
            is ProcessingState.Processing -> {
                ProcessingContent(rotation = rotation)
            }
            is ProcessingState.Success -> {
                ProcessingContent(rotation = rotation)
            }
            is ProcessingState.Error -> {
                ErrorContent(
                    errorMessage = state.message,
                    onRetryClick = {
                        viewModel.resetProcessingState()
                        onRetryClick()
                    },
                    onCancelClick = onCancelClick
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Cancel Button (only show during processing)
        if (processingState is ProcessingState.Processing || processingState is ProcessingState.Idle) {
            TextButton(
                onClick = onCancelClick,
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CoralOrange
                )
            }
        }
    }
}

@Composable
private fun ProcessingContent(rotation: Float) {
    // Animated Circle with Magic Wand
    Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Orange swirl/marble background circle
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2

            // Draw gradient circle background
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFF5A623),
                        Color(0xFFFF7B00),
                        Color(0xFFFFA500),
                        Color(0xFFFF8C00),
                        Color(0xFFF5A623)
                    ),
                    center = center
                ),
                radius = radius,
                center = center
            )

            // Add some swirl lines for effect
            for (i in 0..5) {
                drawArc(
                    color = Color.White.copy(alpha = 0.3f),
                    startAngle = i * 60f,
                    sweepAngle = 30f,
                    useCenter = false,
                    style = Stroke(width = 8f, cap = StrokeCap.Round),
                    topLeft = Offset(center.x - radius * 0.7f, center.y - radius * 0.7f),
                    size = androidx.compose.ui.geometry.Size(radius * 1.4f, radius * 1.4f)
                )
            }
        }

        // Magic wand icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoFixHigh,
                contentDescription = null,
                tint = CoralOrange,
                modifier = Modifier.size(32.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(48.dp))

    // Headline
    Text(
        text = "Making magic\nhappen...",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        textAlign = TextAlign.Center,
        lineHeight = 40.sp
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Subtitle
    Text(
        text = "Blending the photographer into the\ngroup photo.",
        fontSize = 16.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center,
        lineHeight = 24.sp
    )

    Spacer(modifier = Modifier.height(40.dp))

    // Progress indicator
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = CoralOrange,
            strokeWidth = 3.dp,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This may take a moment...",
            fontSize = 14.sp,
            color = TextTertiary
        )
    }

    Spacer(modifier = Modifier.height(40.dp))

    // Tip Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CoralOrangeLight.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "\uD83D\uDCA1",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Tip: Our AI analyzes lighting to match shadows perfectly.",
                fontSize = 14.sp,
                color = TextTertiary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetryClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    // Error Icon
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFEBEE)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Error,
            contentDescription = null,
            tint = Color(0xFFE53935),
            modifier = Modifier.size(64.dp)
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Error Headline
    Text(
        text = "Oops! Something\nwent wrong",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        textAlign = TextAlign.Center,
        lineHeight = 36.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Error Message
    Text(
        text = errorMessage,
        fontSize = 14.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 32.dp),
        lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(40.dp))

    // Retry Button
    Button(
        onClick = onRetryClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CoralOrange
        )
    ) {
        Text(
            text = "Try Again",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Cancel Button
    TextButton(
        onClick = onCancelClick,
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Text(
            text = "Go Back",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProcessingScreenPreview() {
    GroupPhotosTheme {
        // Preview with a mock - can't preview with real ViewModel easily
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            ProcessingContent(rotation = 0f)
        }
    }
}
