package com.dyiz.kidslearningapp.Screens.badgesScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dyiz.kidslearningapp.Database.ViewModel.MainViewModel
import com.dyiz.kidslearningapp.R
import com.dyiz.kidslearningapp.badges.BadgeGameThresholds

data class BadgeItem(val imageRes: Int, val bgColor: Color)
@Composable
fun MyBadgesScreen(navController: NavHostController, viewModel: MainViewModel) {
    val badgesList = listOf(
        BadgeItem(R.drawable.badgealphagreatjob, Color(0xFFF06293)),//1
        BadgeItem(R.drawable.badgecountchamp, Color(0xFFF8A423)),//2
        BadgeItem(R.drawable.badgecolormaster, Color(0xFF1DB3DB)),//3
        BadgeItem(R.drawable.badgeshapehero, Color(0xFFFD802D)),//4
        BadgeItem(R.drawable.badgealphamaster, Color(0xFF37C86F)),//5
        BadgeItem(R.drawable.badgecountninja, Color(0xFF67419F)),//6
        BadgeItem(R.drawable.badgeshapechamp, Color(0xFF3177E1)),//7
        BadgeItem(R.drawable.badgecolorrainbow, Color(0xFFEAD534))//8
    )

    val childId = viewModel.sessionManager.currentChildId
    val badgeRefresh by viewModel.badgeGameProgress.refreshTick.collectAsState()

    // State for Dialog
    var selectedBadgeReq by remember { mutableStateOf<Pair<Int, BadgeRequirement>?>(null) }

    val unlockMask = remember(childId, badgeRefresh) {
        viewModel.badgeGameProgress.readUnlockMask(childId)
    }

    LaunchedEffect(childId) {
        if (childId > 0) {
            viewModel.badgeGameProgress.clearBadgeDotPending(childId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1f)
        ) {
            // Background
            Image(
                painter = painterResource(id = R.drawable.parentscreenbg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                contentScale = ContentScale.FillBounds
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // 1. Custom Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.backarrowgame),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { navController.navigateUp() }
                    )
                    Text(
                        text = "My Badges",
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(R.font.balootwomediam)),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // 2. Header Banner with Overlay Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 10.dp, top = 8.dp)
                ) {
                    // Main Background Box (F3F3F3)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFF3F3F3),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(vertical = 11.dp, horizontal = 15.dp)
                    ) {
                        // Dashed Border Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawBehind {
                                    drawRoundRect(
                                        color = Color(0xFFFF9D5C),
                                        style = Stroke(
                                            width = 3f,
                                            pathEffect = PathEffect.dashPathEffect(
                                                floatArrayOf(
                                                    10f,
                                                    10f
                                                ), 0f
                                            )
                                        ),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(15.dp.toPx())
                                    )
                                }
                                .padding(vertical = 15.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Play the Games &\nEarn Badges",
                                color = Color(0xFFFF9D5C),
                                fontSize = 23.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp,
                                fontFamily = FontFamily(Font(R.font.balooregular))
                            )
                        }
                    }

                    // Overlay Badge Image (Left Side)
                    Image(
                        painter = painterResource(id = R.drawable.medalicon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(75.dp)
                            .align(Alignment.TopStart)
                            .offset(x = (-17).dp, y = (-5).dp)
                    )
                }

                Spacer(modifier = Modifier.height(17.dp))

                // 3. Responsive Badges Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = badgesList.size,
                        key = { badgesList[it].imageRes }
                    ) { index ->
                        val badge = badgesList[index]
                        val unlocked = BadgeGameThresholds.isBadgeUnlocked(unlockMask, index + 1)
                        BadgeCard(badge, unlocked,
                            onClick = {
                                if (!unlocked) {
                                    selectedBadgeReq = (index + 1) to getBadgeRequirement(index + 1)
                                }
                            })
                    }
                }

                // 4. 3D Let's Play Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 60.dp, end = 60.dp, bottom = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Shadow/3D Effect Layer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .offset(y = 4.dp)
                            .background(
                                Color(0xFFD17128),
                                androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(57.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF915423),
                                        colorResource(id = R.color.hometoptextcolor),
                                        colorResource(id = R.color.hometoptextcolor),
                                        colorResource(id = R.color.hometoptextcolor)
                                    ),
                                    startY = 0f, endY = 50f
                                ), RoundedCornerShape(20.dp)
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Let's Play",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontFamily = FontFamily(Font(R.font.balooregular))
                        )
                    }
                }
            }
        }

        // --- Paste this inside the Box, at the end of CompositionLocalProvider ---
        selectedBadgeReq?.let { (index, req) ->
            val gameType = when (req.gameName) {
                "Alphabet Game" -> com.dyiz.kidslearningapp.badges.BadgeGameType.Alpha
                "Numbers Game" -> com.dyiz.kidslearningapp.badges.BadgeGameType.Number
                "Colors Game" -> com.dyiz.kidslearningapp.badges.BadgeGameType.Color
                else -> com.dyiz.kidslearningapp.badges.BadgeGameType.Shape
            }

            val currentMs = viewModel.badgeGameProgress.getPlayTimeMs(childId, gameType)
            val remainingMs = (req.targetMs - currentMs).coerceAtLeast(0L)
            val remainingMins = remainingMs / 60000
            val targetMins = req.targetMs / 60000

            androidx.compose.ui.window.Dialog(onDismissRequest = { selectedBadgeReq = null }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How to Unlock?",
                            fontSize = 22.sp,
                            fontFamily = FontFamily(Font(R.font.balooregular)),
                            color = Color(0xFFFF9D5C)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Play the ${req.gameName} for $targetMins minutes to earn this badge.",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.balooregular)),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress status
                        Text(
                            text = if (remainingMins > 0) "Remaining: $remainingMins mins" else "Almost there!",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.balooregular))
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // "Got it" Button
                        // 4. 3D Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .align(Alignment.CenterHorizontally)
                                .padding(horizontal = 40.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Shadow/3D Effect Layer
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                                    .offset(y = 4.dp)
                                    .background(
                                        Color(0xFFD17128),
                                        androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(57.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF915423),
                                                colorResource(id = R.color.hometoptextcolor),
                                                colorResource(id = R.color.hometoptextcolor),
                                                colorResource(id = R.color.hometoptextcolor)
                                            ),
                                            startY = 0f, endY = 50f
                                        ), RoundedCornerShape(20.dp)
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { selectedBadgeReq = null},
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Got it!",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily(Font(R.font.balooregular))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeCard(badge: BadgeItem, isUnlocked: Boolean,onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(!isUnlocked) Color.Gray.copy(alpha = 0.5f) else badge.bgColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = badge.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.85f),
                contentScale = ContentScale.Fit,
                colorFilter = if (!isUnlocked) {
                    ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                } else {
                    null
                }
            )
        }
    }
}


// Requirement data class
data class BadgeRequirement(val gameName: String, val targetMs: Long)

// Badge index (1 to 8) ke hisaab se requirement return karega
fun getBadgeRequirement(index: Int): BadgeRequirement {
    return when (index) {
        1 -> BadgeRequirement("Alphabet Game", BadgeGameThresholds.ALPHA_BADGE_1_MS)
        2 -> BadgeRequirement("Numbers Game", BadgeGameThresholds.NUMBER_BADGE_2_MS)
        3 -> BadgeRequirement("Colors Game", BadgeGameThresholds.COLOR_BADGE_3_MS)
        4 -> BadgeRequirement("Shapes Game", BadgeGameThresholds.SHAPE_BADGE_4_MS)
        5 -> BadgeRequirement("Alphabet Game", BadgeGameThresholds.ALPHA_BADGE_5_MS)
        6 -> BadgeRequirement("Numbers Game", BadgeGameThresholds.NUMBER_BADGE_6_MS)
        7 -> BadgeRequirement("Shapes Game", BadgeGameThresholds.SHAPE_BADGE_7_MS)
        8 -> BadgeRequirement("Colors Game", BadgeGameThresholds.COLOR_BADGE_8_MS)
        else -> BadgeRequirement("Unknown", 0L)
    }
}