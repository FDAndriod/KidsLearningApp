package com.dyiz.kidslearningapp.utils

import androidx.compose.animation.core.copy
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dyiz.kidslearningapp.R

@Composable
fun BadgeUnlockedDialog(badgeIndex: Int, onDismiss: () -> Unit) {
    val badgesList = listOf(
        R.drawable.badgealphagreatjob, R.drawable.badgecountchamp,
        R.drawable.badgecolormaster, R.drawable.badgeshapehero,
        R.drawable.badgealphamaster, R.drawable.badgecountninja,
        R.drawable.badgeshapechamp, R.drawable.badgecolorrainbow
    )

    // Basic Dialog to overlay everything
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 1. Glowing background effect
            Box(
                modifier = Modifier
                    .size(400.dp).offset(y = 16.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFE082).copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // 2. Main Content Container
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    // Yellow Card Box
                    Card(
                        modifier = Modifier
                            .size(width = 250.dp, height = 250.dp)
                            .padding(bottom = 30.dp), // Space for badge to hang off
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCC33))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 20.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                text = "UNLOCKED!",
                                color = Color(0xFF67419F),
                                fontSize = 34.sp,
                                fontFamily = FontFamily(Font(R.font.balooregular))
                            )
                        }
                    }

                    // Overlapping Badge Image
                    Image(
                        painter = painterResource(id = badgesList[badgeIndex - 1]),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .offset(y = 26.dp) // Moves it slightly below the card edge
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Close button or just tap outside to dismiss
                Text(
                    text = "Tap to Close",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable (
                        indication = null, interactionSource = remember { MutableInteractionSource() }
                    ){ onDismiss() }
                )
            }
        }
    }
}