package com.wajahatkarim.groupphotos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wajahatkarim.groupphotos.R
import com.wajahatkarim.groupphotos.ui.theme.BackgroundCard
import com.wajahatkarim.groupphotos.ui.theme.BackgroundLight
import com.wajahatkarim.groupphotos.ui.theme.CoralOrange
import com.wajahatkarim.groupphotos.ui.theme.CoralOrangeLight
import com.wajahatkarim.groupphotos.ui.theme.GroupPhotosTheme
import com.wajahatkarim.groupphotos.ui.theme.RedHeart
import com.wajahatkarim.groupphotos.ui.theme.TextPrimary
import com.wajahatkarim.groupphotos.ui.theme.TextSecondary
import com.wajahatkarim.groupphotos.ui.theme.TextTertiary

@Composable
fun HomeScreen(
    onNewGroupPhotoClick: () -> Unit = {},
    onViewPastCreationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GroupSnap",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = TextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Illustration Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CoralOrangeLight,
                            CoralOrangeLight.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(BackgroundCard),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.group_selfie),
                contentDescription = "Group photo illustration",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Headline
        Text(
            text = "Everyone gets in\nthe picture.",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtitle
        Text(
            text = "No more taking turns. Merge\nyourself into the shot instantly.",
            fontSize = 16.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // New Group Photo Button
        Button(
            onClick = onNewGroupPhotoClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CoralOrange
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.PhotoCamera,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "New Group Photo",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

//        // View Past Creations Button
//        OutlinedButton(
//            onClick = onViewPastCreationsClick,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp),
//            shape = RoundedCornerShape(28.dp),
//            colors = ButtonDefaults.outlinedButtonColors(
//                contentColor = TextPrimary
//            )
//        ) {
//            Icon(
//                imageVector = Icons.Outlined.Image,
//                contentDescription = null,
//                tint = CoralOrange,
//                modifier = Modifier.size(22.dp)
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Text(
//                text = "View Past Creations",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = TextPrimary
//            )
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))

        // Footer
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Made with \u2764\uFE0F",
                fontSize = 14.sp,
                color = TextTertiary
            )
            Text(
                text = " by ",
                fontSize = 14.sp,
                color = TextTertiary
            )
            Text(
                text = "Wajahat Karim",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    GroupPhotosTheme {
        HomeScreen()
    }
}